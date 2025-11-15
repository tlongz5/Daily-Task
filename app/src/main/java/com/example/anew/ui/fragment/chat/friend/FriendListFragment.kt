package com.example.anew.ui.fragment.chat.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anew.R
import com.example.anew.databinding.FragmentFriendListBinding
import com.example.anew.ui.fragment.chat.adapter.FriendListAdapter
import com.example.anew.viewmodelFactory.MyViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FriendListFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: FriendListViewModel
    private val MyViewModelFactory = MyViewModelFactory()


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
        viewModel= ViewModelProvider(this, MyViewModelFactory).get(FriendListViewModel::class.java)
        viewModel.fetchFriends()

        viewModel.friends.observe(viewLifecycleOwner) {
            (binding.rcvFriendList.adapter as FriendListAdapter).updateData(it)
        }

        binding.rcvFriendList.adapter = FriendListAdapter(mutableListOf(),
            onClickJohnChatRoom = { // NOTOOTOTOTOOTOTOTOT
                findNavController().navigate(R.id.action_friendListFragment_to_chatRoomFragment, Bundle().apply {
                    putString("uid", it)
                })
            },
            onClickViewProfileUser = {
                findNavController().navigate(R.id.action_friendListFragment_to_otherUserProfileFragment,Bundle().apply {
                    putString("uid", it)
                })
            })
        binding.rcvFriendList.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}