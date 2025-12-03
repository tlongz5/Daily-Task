package com.example.anew.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.anew.R
import com.example.anew.databinding.FragmentHomeBinding
import com.example.anew.model.Team
import com.example.anew.support.fakeData
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
            if(!it.isEmpty()){
                binding.rcvOngoingProject.visibility = View.VISIBLE
                binding.tvNoDataInOngoingProject.visibility = View.GONE
                binding.rcvOngoingProject.adapter = OngoingProjectAdapter(it.take(20) as MutableList<Team>)
            }else{
                binding.rcvOngoingProject.visibility = View.GONE
                binding.tvNoDataInOngoingProject.visibility = View.VISIBLE
            }
        }

        homeViewModel.completedProjectState.observe(viewLifecycleOwner){
            if(!it.isEmpty()){
                binding.rcvTaskCompleted.visibility = View.VISIBLE
                binding.tvNoDataInCompletedProject.visibility = View.GONE
                binding.rcvTaskCompleted.adapter = CompletedProjectAdapter(it.take(20) as MutableList<Team>)
            }else{
                binding.rcvTaskCompleted.visibility = View.GONE
                binding.tvNoDataInCompletedProject.visibility = View.VISIBLE
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.getProjectData()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.edtSearchTask.addTextChangedListener {
            homeViewModel.reloadProjectDataWithSearch(it.toString())
        }

//      // handle load data user and click avatar
        binding.userName.text = fakeData.user!!.name
        Glide.with(this)
            .load(fakeData.user!!.photoUrl)
            .error(R.drawable.ic_launcher_background)
            .circleCrop()
            .override(48,48)
            .into(binding.avatar)

        binding.avatar.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_profileFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}