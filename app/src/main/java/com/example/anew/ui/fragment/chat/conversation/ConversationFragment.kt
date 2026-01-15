package com.example.anew.ui.fragment.chat.conversation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anew.R
import com.example.anew.databinding.FragmentConversationBinding
import com.example.anew.model.UiState
import com.example.anew.support.fakeData
import com.example.anew.ui.fragment.chat.adapter.ConversationAdapter
import com.example.anew.viewmodelFactory.MyViewModelFactory
import kotlinx.coroutines.launch

class ConversationFragment(): Fragment() {
    private val myViewModelFactory = MyViewModelFactory()
    private lateinit var viewModel: ConversationViewModel
    private var _binding: FragmentConversationBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentConversationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, myViewModelFactory)[ConversationViewModel::class.java]

        //get data from bundle
        val data = arguments?.getString("chat_type")
        val chatType = data ?: "Private"

        binding.rcvMessage.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvMessage.adapter = ConversationAdapter(callback = {
            findNavController().navigate(R.id.action_ChatFragment_to_chatRoomFragment, Bundle().apply {
                putString("chat_type", chatType)
                putString("chat_name", it.chatName)
                putString("receiver_id", it.roomId.split("_").find { it != fakeData.user!!.uid })
                // if type!=private, receiverName=chatName
                putString("receiver_name", it.chatName)
                putString("receiver_avatar", it.avatar)
                putString("chatId", it.roomId)
            })

            //Update seen message
            viewModel.updateSeen(it.roomId,chatType,fakeData.user!!.uid)
        })

        viewModel.getConversation(fakeData.user!!.uid,chatType)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.conservationState.collect { uiState ->
                    when(uiState){
                        is UiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }is UiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            (binding.rcvMessage.adapter as ConversationAdapter).submitList(uiState.data)
                        }is UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        fun newInstance(chatType: String): ConversationFragment {
            val fragment = ConversationFragment()
            val args = Bundle()
            args.putString("chat_type", chatType)
            fragment.arguments = args
            return fragment
        }
    }
}