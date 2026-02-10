package com.example.anew.ui.fragment.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anew.R
import com.example.anew.data.local.MyHelper
import com.example.anew.databinding.FragmentNotificationBinding
import com.example.anew.viewmodelfactory.MyViewModelFactory

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
        viewModel.getNotification(MyHelper.user!!.uid)

        viewModel.notificationList.observe(viewLifecycleOwner){
            (binding.rcvNotification.adapter as NotificationAdapter).submitList(it)
        }

        binding.rcvNotification.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvNotification.adapter = NotificationAdapter{ notification ->

            //update status
            if(!notification.checkRead)
            viewModel.updateStatus(notification.notificationId)

            when(notification.type){
                "create_project" -> {
                    val bundle = Bundle()
                    bundle.putString("id", notification.projectId)
                    findNavController().navigate(R.id.action_NotificationFragment_to_taskDetailFragment,
                        bundle,navOptions {
                            anim {
                                enter = R.anim.side_in_right
                                exit = android.R.anim.fade_out
                                popEnter = android.R.anim.fade_in
                                popExit = android.R.anim.slide_out_right
                            }
                        })
                }
                "end_project" -> {
                    val bundle = Bundle()
                    bundle.putString("id", notification.projectId)
                    findNavController().navigate(R.id.action_NotificationFragment_to_taskDetailFragment,
                        bundle,navOptions {
                            anim {
                                enter = R.anim.side_in_right
                                exit = android.R.anim.fade_out
                                popEnter = android.R.anim.fade_in
                                popExit = android.R.anim.slide_out_right
                            }
                        })
                }
                "request_friend" -> {
                    findNavController().navigate(R.id.action_NotificationFragment_to_friendsRequestFragment)
                }
                "become_friend" -> {
                    val bundle = Bundle()
                    bundle.putString("uid", notification.userId)
                    findNavController().navigate(R.id.action_NotificationFragment_to_otherUserProfileFragment, bundle,
                        navOptions {
                            anim {
                                enter = R.anim.side_in_right
                                exit = android.R.anim.fade_out
                                popEnter = android.R.anim.fade_in
                                popExit = android.R.anim.slide_out_right
                            }
                        })
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}