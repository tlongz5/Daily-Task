package com.example.anew.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.anew.databinding.FragmentHomeBinding
import com.example.anew.model.Team
import com.example.anew.ui.fragment.home.adapter.CompletedProjectAdapter
import com.example.anew.ui.fragment.home.adapter.OngoingProjectAdapter
import com.example.anew.viewmodelFactory.MyViewModelFactory

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private val MyViewModelFactory = MyViewModelFactory()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this, MyViewModelFactory).get(HomeViewModel::class.java)

        homeViewModel.projectState.observe(viewLifecycleOwner){
            homeViewModel.reloadProjectDataWithSearch("")
        }

        homeViewModel.ongoingProjectState.observe(viewLifecycleOwner){
            binding.rcvOngoingProject.adapter = OngoingProjectAdapter(it)
        }

        homeViewModel.completedProjectState.observe(viewLifecycleOwner){
            binding.rcvTaskCompleted.adapter = CompletedProjectAdapter(it)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.getProjectData()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.edtSearchTask.addTextChangedListener {
            homeViewModel.reloadProjectDataWithSearch(it.toString())
        }

//        val onGoingProject = mutableListOf<Team>()
//        val completedProject = mutableListOf<Team>()
//
//        homeViewModel.getProjectsData(completedProject, onGoingProject)
//        binding.rcvTaskCompleted.adapter = CompletedProjectAdapter(completedProject)
//        binding.rcvOngoingProject.adapter = OngoingProjectAdapter(onGoingProject)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}