package com.example.anew.ui.fragment.chat.chat_room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anew.R
import com.example.anew.databinding.FragmentChatRoomBinding
import com.example.anew.model.UiState
import com.example.anew.support.fakeData
import com.example.anew.ui.fragment.chat.adapter.ChatRoomAdapter
import com.example.anew.viewmodelFactory.MyViewModelFactory
import kotlinx.coroutines.launch
import kotlin.String

class ChatRoomFragment : Fragment() {
    private val myViewModelFactory = MyViewModelFactory()
    private lateinit var viewModel: ChatRoomViewModel
    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatId: String
    private lateinit var chatName: String
    private lateinit var chatType: String
    private lateinit var receiverId: String
    private lateinit var receiverName: String
    private lateinit var receiverAvatar: String

    private val pickImg = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(10)){ uris ->
        if(uris.isNotEmpty()){
            viewModel.pushMessage(
                chatId,
                chatName,

                fakeData.user!!.uid,
                fakeData.user!!.name,
                fakeData.user!!.photoUrl,

                receiverId,
                receiverName,
                receiverAvatar, //save all avatar, group or private

                "Sent ${uris.size} image",
                uris,
                chatType
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, myViewModelFactory)[ChatRoomViewModel::class.java]

        //getData from Bundle
        chatType = arguments?.getString("chat_type") ?: "Private"
        chatId = arguments?.getString("chatId") ?: ""
        chatName = arguments?.getString("chat_name") ?: ""
        receiverId = arguments?.getString("receiver_id") ?: ""
        receiverName = arguments?.getString("receiver_name") ?: ""
        receiverAvatar = arguments?.getString("receiver_avatar") ?: ""

        setupToolbar(chatType)
        setUpViewModel(chatId!!)

        binding.rcvMessage.layoutManager= LinearLayoutManager(requireContext())
        binding.rcvMessage.adapter= ChatRoomAdapter()
        binding.btnSendMessage.setOnClickListener {
            binding.editMessage.text.clear()
            binding.editMessage.clearFocus()
            val message = binding.editMessage.text.toString()
            if(message.isEmpty()) return@setOnClickListener
            viewModel.pushMessage(
                chatId,
                chatName,

                fakeData.user!!.uid,
                fakeData.user!!.name,
                fakeData.user!!.photoUrl,

                receiverId,
                receiverName,
                receiverAvatar, //save all avatar, group or private

                message,
                emptyList(),
                chatType
            )

        }

        binding.btnPickImage.setOnClickListener {
            pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setupToolbar(chatType: String) {
        binding.toolbar.apply {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            inflateMenu(R.menu.chat_room_menu)
            if(chatType=="Private") {
                menu.setGroupVisible(R.id.group_actions, false)
                menu.setGroupVisible(R.id.admin_actions, false)
                menu.setGroupVisible(R.id.project_action, false)
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.voice_call -> {
                        true
                    }
                    R.id.group_info -> {
                        true
                    }
                    R.id.add_member -> {
                        true
                    }
                    R.id.leave_group -> {
                        true
                    }
                    R.id.delete_group -> {
                        true
                    }
                    R.id.make_admin -> {
                        true
                    }
                    else -> false
                }
            }
        }
    }
    private fun setUpViewModel(chatId: String) {
        viewModel.getMessages(chatId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.messageState.collect {
                    when (it) {
                        is UiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rcvMessage.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rcvMessage.visibility = View.VISIBLE
                            (binding.rcvMessage.adapter as ChatRoomAdapter).submitList(it.data)

                        }
                        is UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rcvMessage.visibility = View.GONE
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }
        }
    }
}