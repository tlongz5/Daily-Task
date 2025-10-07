package com.example.anew.ui.fragment.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.anew.ui.fragment.add.AddTeamMembersAdapter
import com.example.anew.databinding.FragmentAddBinding

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentAddBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcvTeamMembers.adapter = AddTeamMembersAdapter()
        val gridLayout = GridLayoutManager(requireContext(), 3)
        binding.rcvTeamMembers.layoutManager = gridLayout
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}