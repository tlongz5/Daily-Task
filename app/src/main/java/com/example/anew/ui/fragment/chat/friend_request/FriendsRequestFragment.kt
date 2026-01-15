package com.example.anew.ui.fragment.chat.friend_request

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anew.R
import com.example.anew.databinding.FragmentFriendsRequestBinding
import com.example.anew.ui.fragment.chat.adapter.FriendsRequestAdapter
import com.example.anew.viewmodelFactory.MyViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class FriendsRequestFragment : BottomSheetDialogFragment() {
    private lateinit var viewModel: FriendsRequestViewModel
    private val myViewModelFactory: MyViewModelFactory = MyViewModelFactory()
    private var _binding : FragmentFriendsRequestBinding? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendsRequestBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, myViewModelFactory)[FriendsRequestViewModel::class.java]

        viewModel.fetchFriendsRequest()
        viewModel.friendsRequest.observe(viewLifecycleOwner){
            binding.progressBar.visibility = View.GONE
            (binding.rcvFriendsRequest.adapter as FriendsRequestAdapter).submitList(it)
        }

        binding.rcvFriendsRequest.adapter = FriendsRequestAdapter(
            callbackAccept = {
                viewModel.addFriend(it)
            },
            callbackDecline = {
                viewModel.removeRequestFriend(it)
            },
            callbackCheckProfile = {
                findNavController().navigate(R.id.action_friendsRequestFragment_to_otherUserProfileFragment, Bundle().apply {
                    putString("uid", it)
                })
            }
        )
        binding.rcvFriendsRequest.layoutManager = LinearLayoutManager(requireContext())

        binding.searchFriend.clearFocus()
        binding.searchFriend.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchFriend.clearFocus()
                if(query.isNullOrEmpty()) return false
                lifecycleScope.launch {
                    if(viewModel.checkSearchFriend(query)){
                        findNavController().navigate(R.id.action_friendsRequestFragment_to_otherUserProfileFragment, Bundle().apply {
                            putString("uid", viewModel.getUidDataFromEmail(query))
                        })
                    }else {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding =null
    }

    //NOTEEEEEEEE
    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        bottomSheetDialog.setOnShowListener { dialogInterface ->
            val dialog = dialogInterface as BottomSheetDialog
            val frameLayout = dialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )

            frameLayout?.let {
                it.layoutParams.height = (resources.displayMetrics.heightPixels*0.8).toInt()
                it.requestLayout()

                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return bottomSheetDialog
    }


}