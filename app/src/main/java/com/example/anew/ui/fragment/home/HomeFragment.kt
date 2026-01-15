package com.example.anew.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
        homeViewModel = ViewModelProvider(this, MyViewModelFactory)[HomeViewModel::class.java]

        //init
        binding.shimmerContainer.visibility = View.VISIBLE
        binding.shimmerContainer.startShimmer()
        binding.swipeRefreshLayout.visibility = View.GONE
        binding.btnCreate.setOnClickListener {
            findNavController().navigate(R.id.AddFragment)
        }

        //check data in firebase if no change and update in UI
        homeViewModel.getProjectData()


        binding.rcvOngoingProject.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvTaskCompleted.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,false)

        homeViewModel.projectState.observe(viewLifecycleOwner){
            homeViewModel.reloadProjectDataWithSearch("")
        }

        homeViewModel.ongoingProjectState.observe(viewLifecycleOwner){
            if(!it.isEmpty()){
                binding.rcvOngoingProject.visibility = View.VISIBLE
                binding.layoutEmptyOngoingProject.visibility = View.GONE
                binding.rcvOngoingProject.adapter = OngoingProjectAdapter(it.take(20) as MutableList<Team>){
                    findNavController().navigate(R.id.action_HomeFragment_to_taskDetailFragment,Bundle().apply {
                        putString("id",it)
                    })
                }
            }else{
                binding.rcvOngoingProject.visibility = View.GONE
                binding.layoutEmptyOngoingProject.visibility = View.VISIBLE
            }
        }

        homeViewModel.completedProjectState.observe(viewLifecycleOwner){
            if(!it.isEmpty()){
                binding.rcvTaskCompleted.visibility = View.VISIBLE
                binding.tvNoDataInCompletedProject.visibility = View.GONE
                binding.rcvTaskCompleted.adapter = CompletedProjectAdapter(it.take(20) as MutableList<Team>){
                    findNavController().navigate(R.id.action_HomeFragment_to_taskDetailFragment,Bundle().apply {
                        putString("id",it)
                    })
                }
            }else{
                binding.rcvTaskCompleted.visibility = View.GONE
                binding.tvNoDataInCompletedProject.visibility = View.VISIBLE
            }
        }

        homeViewModel.isCheckSwapScreen.observe(viewLifecycleOwner){
            if(it==true){
                binding.shimmerContainer.visibility = View.GONE
                binding.shimmerContainer.stopShimmer()
                binding.swipeRefreshLayout.visibility = View.VISIBLE
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.getProjectData()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.edtSearchTask.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
            override fun onQueryTextChange(newText: String?): Boolean {
                homeViewModel.reloadProjectDataWithSearch(newText.toString())
                return true
            }
        })

//      // handle load data user and click avatar
        binding.userName.text = fakeData.user!!.name
        Glide.with(view)
            .load(fakeData.user!!.photoUrl)
            .error(R.drawable.ic_launcher_background)
            .circleCrop()
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