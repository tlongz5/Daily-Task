package com.example.anew.ui.fragment.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anew.ui.fragment.chat.adapter.MessageAdapter
import com.example.anew.databinding.FragmentMessageBinding
import com.example.anew.model.GroupType
import com.example.anew.model.fakeData

class MessageFragment(): Fragment() {
    var _binding: FragmentMessageBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get data from bundle
        val data = arguments?.getString("chat_name")
        val chat_name = if(data!=null) data else "Chat"

        val listMessage = when(chat_name){
            "Chat"-> fakeData.Messages.filter { it.type == GroupType.PERSONAL }
            "Group"-> fakeData.Messages.filter { it.type == GroupType.GROUP }
            "Project" -> fakeData.Messages.filter { it.type == GroupType.PROJECT }
            else -> emptyList()
        }

        binding.rcvMessage.adapter = MessageAdapter(listMessage)
        binding.rcvMessage.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        fun newInstance(chat_name: String): MessageFragment {
            val fragment = MessageFragment()
            val args = Bundle()
            args.putString("chat_name", chat_name)
            fragment.arguments = args
            return fragment
        }
    }
}