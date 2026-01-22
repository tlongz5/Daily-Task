package com.example.anew.ui.fragment.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anew.R
import com.example.anew.databinding.FragmentNotificationBinding
import com.example.anew.support.fakeData
import com.example.anew.viewmodelFactory.MyViewModelFactory

class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NotificationViewModel
    private val myViewModelFactory = MyViewModelFactory()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, myViewModelFactory)[NotificationViewModel::class.java]
        viewModel.getNotification(fakeData.user!!.uid)

        viewModel.notificationList.observe(viewLifecycleOwner){
            (binding.rcvNotification.adapter as NotificationAdapter).submitList(it)
        }

        binding.rcvNotification.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvNotification.adapter = NotificationAdapter{ notificationId, type,projectId, userId ->

            //update status
            viewModel.updateStatus(notificationId)

            when(type){
                "create_project" -> {
                    val bundle = Bundle()
                    bundle.putString("id", projectId)
                    findNavController().navigate(R.id.action_NotificationFragment_to_taskDetailFragment, bundle)
                }
                "end_project" -> {
                    val bundle = Bundle()
                    bundle.putString("id", projectId)
                    findNavController().navigate(R.id.action_NotificationFragment_to_taskDetailFragment, bundle)
                }
                "request_friend" -> {
                    findNavController().navigate(R.id.action_NotificationFragment_to_friendsRequestFragment)
                }
                "become_friend" -> {
                    val bundle = Bundle()
                    bundle.putString("uid", userId)
                    findNavController().navigate(R.id.action_NotificationFragment_to_otherUserProfileFragment, bundle)
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}