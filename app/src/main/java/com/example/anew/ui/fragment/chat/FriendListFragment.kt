package com.example.anew.ui.fragment.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.anew.R
import com.example.anew.databinding.FragmentFriendListBinding
import com.example.anew.ui.fragment.chat.adapter.FriendListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FriendListFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFriendListBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcvFriendList.adapter = FriendListAdapter(mutableListOf(),
            onClick = {
                val bundle = Bundle().apply {
                    putString("userUid",it)
                }
                findNavController().navigate(R.id.action_friendListFragment_to_otherUserProfileFragment,bundle)
            })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}