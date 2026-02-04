package com.example.anew.ui.fragment.common.selectaddmember

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anew.databinding.FragmentSelectAddMemberBinding
import com.example.anew.data.local.MyHelper
import com.example.anew.ui.fragment.common.dialog.CreateGroupDialog
import com.example.anew.ui.fragment.add.adapter.FriendLoadStateAdapter
import com.example.anew.ui.fragment.common.adapter.MembersPickedAdapter
import com.example.anew.ui.fragment.common.adapter.PickFriendAdapter
import com.example.anew.ui.fragment.add.add.AddViewModel
import com.example.anew.viewmodelfactory.MyViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SelectAddMemberFragment : Fragment() {
    private lateinit var sharedAddFragment: AddViewModel
    private lateinit var selectAddMemberViewModel: SelectAddMemberViewModel
    private val MyViewModelFactory = MyViewModelFactory()

    private val pickFriendAdapter = PickFriendAdapter(
        callback = { isChecked, user ->
            selectAddMemberViewModel.updateFriendPickedState(isChecked, user)
        }
    )

    private val membersPickedAdapter = MembersPickedAdapter {
        selectAddMemberViewModel.updateFriendPickedState(false, it)
        pickFriendAdapter.updateUserListSelection(it, false)
    }

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
        selectAddMemberViewModel = ViewModelProvider(this, MyViewModelFactory)[SelectAddMemberViewModel::class.java]
        sharedAddFragment = ViewModelProvider(requireActivity(), MyViewModelFactory)[AddViewModel::class.java]

        binding.rcvFriends.adapter = pickFriendAdapter.withLoadStateFooter(FriendLoadStateAdapter { pickFriendAdapter.retry() })
        lifecycleScope.launch {
            selectAddMemberViewModel.friendPagingData.collectLatest { pagingData ->
                pickFriendAdapter.submitData(pagingData)
            }
        }

        binding.rcvTeamMembersPicked.adapter  = membersPickedAdapter
        binding.rcvTeamMembersPicked.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        selectAddMemberViewModel.friendPickedState.observe(viewLifecycleOwner){
            membersPickedAdapter.submitList(it)
            pickFriendAdapter.reloadPickedFriend(it.map { user -> user.uid })
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            if(selectAddMemberViewModel.friendPickedState.value.size<2){
                Toast.makeText(requireContext(), "Please select at least 2 members", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(arguments?.getString("chat_type")!=null) {

                val dialog = CreateGroupDialog()
                dialog.show(childFragmentManager, "createGroupDialog")
            }
            else {
                sharedAddFragment.saveUser(selectAddMemberViewModel.friendPickedState.value)
                findNavController().popBackStack()
            }
        }

        childFragmentManager.setFragmentResultListener("create_group", viewLifecycleOwner) { _, bundle ->
            val groupName = bundle.getString("group_name")
            try {
                selectAddMemberViewModel.createGroup(
                    "",
                    groupName!!,
                    MyHelper.groupAvatar.random(),
                    MyHelper.user!!.uid,
                    selectAddMemberViewModel.friendPickedState.value.plus(MyHelper.user!!).map { it.uid },
                    "Group"
                )
                Toast.makeText(requireContext(), "Create Group Successfully", Toast.LENGTH_SHORT).show()
            }catch (e: Exception){
                Toast.makeText(requireContext(), "Error, please try again later", Toast.LENGTH_SHORT).show()
            }
            findNavController().popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}