package com.example.anew.ui.fragment.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.anew.databinding.FragmentChatBinding
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

        // ViewPager2 + TabLayout
        val fragments = listOf(MessageFragment.newInstance("Chat"),
            MessageFragment.newInstance("Group"),
            MessageFragment.newInstance("Project"))

        val titles = listOf("Chat","Group","Project")

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