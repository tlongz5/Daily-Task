package com.example.anew.ui.fragment.home.taskdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.anew.R
import com.example.anew.data.local.MyHelper
import com.example.anew.databinding.FragmentTaskDetailBinding
import com.example.anew.utils.animCb
import com.example.anew.utils.animProgress
import com.example.anew.utils.toDayAndMonth
import com.example.anew.utils.toHourAndMinute
import com.example.anew.ui.fragment.add.adapter.AddTeamMembersAdapter
import com.example.anew.viewmodelfactory.MyViewModelFactory

class TaskDetailFragment : Fragment() {
    private val myViewModelFactory = MyViewModelFactory()
    private lateinit var viewModel: TaskDetailViewModel
    private var _binding: FragmentTaskDetailBinding? = null
    val binding get() = _binding!!

    var checkSwap =false

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

        // check if usr not in group
        requireActivity().supportFragmentManager.setFragmentResultListener("request", viewLifecycleOwner) { _, bundle ->
            val swapScreen = bundle.getBoolean("swap_screen")
            if(swapScreen) checkSwap = true
        }

        //init view model
        viewModel = ViewModelProvider(this, myViewModelFactory)[TaskDetailViewModel::class.java]
        viewModel.getProjectData(id!!)
        viewModel.projectState.observe(viewLifecycleOwner) {
            if(checkSwap || !it.members.contains(MyHelper.user!!.uid)){
                Log.d("checkSwap", "viw")
                swapScreen()
                return@observe
            }

            Log.d("checkSwap","viewModel")
            binding.tvProjectName.text = it.title
            binding.tvTaskDetail.text = it.description
            binding.tvDate.text = it.dueTime!!.toDayAndMonth()
            binding.tvTime.text = it.dueTime!!.toHourAndMinute()

            animProgress(binding.progressRing, binding.tvPercent,
                binding.progressRing.progress,it.completedPercent)

            binding.checkBox.setOnCheckedChangeListener(null)
            binding.checkBox.isEnabled= false
            binding.checkBox.alpha = 0.6f
            binding.progressRing.alpha = 0.6f
            val isChecked = it.membersCompleted.contains(MyHelper.user!!.uid)
            binding.checkBox.isChecked = isChecked
            binding.checkBox.animCb(isChecked)

            if(it.inProgress){
                binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                    viewModel.updateProgress(isChecked)
                    binding.checkBox.animCb(isChecked)
                }
                binding.checkBox.isEnabled= true
                binding.checkBox.alpha = 1f
                binding.progressRing.alpha = 1f
            }

            if((binding.rcvTeamMembers.adapter as AddTeamMembersAdapter).currentList.isEmpty()) {
                viewModel.getUserDataFromUid(it.admin, it.members)
                viewModel.getImgGroupData(it.projectId)
            }
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
                        putString("admin_id", conversationInfo.adminId)
                    },navOptions {
                        anim {
                            enter = R.anim.side_in_right
                            exit = android.R.anim.fade_out
                            popEnter = android.R.anim.fade_in
                            popExit = android.R.anim.slide_out_right
                        }
                    }
                )
            }

        }
        binding.layoutError.btnBack2.setOnClickListener {
            findNavController().popBackStack()
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

    private fun swapScreen() {
        binding.layoutPresent.visibility = View.GONE
        binding.layoutError.root.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}