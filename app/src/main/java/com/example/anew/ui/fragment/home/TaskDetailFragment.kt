package com.example.anew.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.anew.R
import com.example.anew.databinding.FragmentTaskDetailBinding
import com.example.anew.model.User
import com.example.anew.support.DataTranfer
import com.example.anew.support.fakeData
import com.example.anew.support.toDayAndMonth
import com.example.anew.support.toHourAndMinute
import com.example.anew.ui.fragment.add.AddTeamMembersAdapter
import com.example.anew.viewmodelFactory.MyViewModelFactory
import kotlinx.coroutines.launch

class TaskDetailFragment : Fragment() {
    private val myViewModelFactory = MyViewModelFactory()
    private lateinit var viewModel: TaskDetailViewModel
    private var _binding: FragmentTaskDetailBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getString("id")

        //init view model
        viewModel = ViewModelProvider(this, myViewModelFactory)[TaskDetailViewModel::class.java]
        viewModel.getProjectData(id!!)
        viewModel.projectState.observe(viewLifecycleOwner) {
            binding.tvProjectName.text = it.title
            binding.tvTaskDetail.text = it.description
            binding.tvDate.text = it.dueTime!!.toDayAndMonth()
            binding.tvTime.text = it.dueTime!!.toHourAndMinute()
            binding.progressRing.progress = it.completedPercent
            binding.tvPercent.text = "${it.completedPercent}%"

            viewModel.getUserDataFromUid(it.admin, it.members)
            viewModel.getImgGroupData(it.id)
        }

        viewModel.adminState.observe(viewLifecycleOwner) {
            Glide.with(requireContext())
                .load(it.photoUrl)
                .error(R.drawable.avt1)
                .centerCrop()
                .into(binding.avtAdmin)

        }

        viewModel.membersState.observe(viewLifecycleOwner) {
            (binding.rcvTeamMembers.adapter as AddTeamMembersAdapter).submitList(it)
        }

        viewModel.conversationState.observe(viewLifecycleOwner) { conversationInfo ->
            Glide.with(requireContext())
                .load(conversationInfo.avatar)
                .error(R.drawable.avt1)
                .centerCrop()
                .into(binding.imgGroup)

            binding.btnConversation.setOnClickListener {
                findNavController().navigate(
                    R.id.action_taskDetailFragment_to_chatRoomFragment,
                    Bundle().apply {
                        putString("chat_type", "Project")
                        putString("chat_name", conversationInfo.chatName)
                        putString("receiver_id", conversationInfo.roomId)
                        putString("receiver_name", conversationInfo.chatName)
                        putString("receiver_avatar", conversationInfo.avatar)
                        putString("chatId", conversationInfo.roomId)
                    }
                )
            }
        }


        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.rcvTeamMembers.adapter = AddTeamMembersAdapter()
        binding.rcvTeamMembers.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL, false
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}