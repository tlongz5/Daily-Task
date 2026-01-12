package com.example.anew.ui.fragment.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.anew.R
import com.example.anew.databinding.FragmentChatBinding
import com.example.anew.ui.fragment.chat.conversation.ConversationFragment
import com.google.android.material.tabs.TabLayoutMediator

class ChatFragment : Fragment() {
    var _binding : FragmentChatBinding? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentChatBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFriend.setOnClickListener {
            findNavController().navigate(R.id.action_ChatFragment_to_friendListFragment)
        }

        binding.btnGroup.setOnClickListener {
            findNavController().navigate(R.id.action_ChatFragment_to_selectAddMemberFragment,
                Bundle().apply {
                    putString("chat_type", "Group")
                })
        }

        binding.btnFriendRequested.setOnClickListener {
            findNavController().navigate(R.id.action_ChatFragment_to_friendsRequestFragment)
        }

        // ViewPager2 + TabLayout
        val fragments = listOf(
            ConversationFragment.newInstance("Private"),
            ConversationFragment.newInstance("Group"),
            ConversationFragment.newInstance("Project"))

        val titles = listOf("Private","Group","Project")

        val viewPager2Adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }

            override fun getItemCount(): Int {
                return fragments.size
            }
        }

        binding.viewPagerMessage.adapter=viewPager2Adapter
        TabLayoutMediator(binding.tabLayoutMessage, binding.viewPagerMessage) { tab, position ->
            tab.text = titles[position]
        }.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding =null
    }

}