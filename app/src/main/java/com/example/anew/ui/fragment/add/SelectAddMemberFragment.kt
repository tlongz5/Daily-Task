package com.example.anew.ui.fragment.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.anew.R
import com.example.anew.databinding.FragmentSelectAddMemberBinding
import com.example.anew.model.User
import com.example.anew.viewmodelFactory.MyViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SelectAddMemberFragment : Fragment() {

    private lateinit var selectAddMemberViewModel: SelectAddMemberViewModel
    private val MyViewModelFactory = MyViewModelFactory()

    private val pickFriendAdapter = PickFriendAdapter(
        callback = { isChecked, user ->
            setChangeUserPicked(isChecked,user)
        }
    )


    var _binding: FragmentSelectAddMemberBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectAddMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectAddMemberViewModel = ViewModelProvider(this, MyViewModelFactory).get(SelectAddMemberViewModel::class.java)

        binding.rcvFriends.adapter = pickFriendAdapter.withLoadStateFooter(FriendLoadStateAdapter { pickFriendAdapter.retry() })

        lifecycleScope.launch {
            selectAddMemberViewModel.friendPagingData.collectLatest { pagingData ->
                pickFriendAdapter.submitData(pagingData)
            }
        }

        binding.rcvTeamMembersPicked.adapter 

        selectAddMemberViewModel.friendPickedState.observe(viewLifecycleOwner){
            binding.rcvTeamMembersPicked.adapter= MembersPickedAdapter(it.toMutableList()) { user ->
                
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setChangeUserPicked(checked: Boolean, user: User) {
        selectAddMemberViewModel.updateFriendPickedState(checked,user)
    }
}