package com.example.anew.ui.fragment.chat.other_user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.anew.R
import com.example.anew.databinding.FragmentOtherUserProfileBinding
import com.example.anew.viewmodelFactory.MyViewModelFactory

class OtherUserProfileFragment : Fragment() {
    private val MyViewModelFactory = MyViewModelFactory()
    private lateinit var viewModel: OtherUserProfileViewModel

    private var _binding: FragmentOtherUserProfileBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtherUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel= ViewModelProvider(this, MyViewModelFactory).get(OtherUserProfileViewModel::class.java)
        val bundle = arguments
        if (bundle != null) {
            val uid = bundle.getString("uid")
            checkFriend(uid!!)
            viewModel.getUserData(uid!!)
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.tvDisplayName.text = user.name
            binding.
            binding.tvEmail.text = user.email
            binding.tvPhoneNumber.text = user.phoneNumber
            Glide.with(this)
                .load(user.photoUrl)
                .circleCrop()
                .into(binding.avatar)

            binding.btnAddfriend.setOnClickListener {
                viewModel.requestFriend(user.uid)
                Toast.makeText(context, "Request sent", Toast.LENGTH_SHORT).show()
            }

            binding.btnUnFriend.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Unfriend")
                    .setMessage("Are you sure you want to unfriend ?")
                    .setPositiveButton("Yes"){
                            _, _ ->
                        viewModel.unFriend(user.uid)
                        checkFriend(user.uid)
                        Toast.makeText(context, "Unfriend successfully", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No"){
                            _, _ ->
                    }
                    .create()
                    .show()
            }

            binding.btnFriendRequested.setOnClickListener {
                viewModel.fromRequestToCancel(user.uid)
            }

            binding.btnSendMessage.setOnClickListener {
                findNavController().navigate(R.id.action_otherUserProfileFragment_to_chatRoomFragment, Bundle().apply {
                    putString("uid", user.uid)
                })
            }
        }

        viewModel.isFriend.observe(viewLifecycleOwner) {
            if (it==1) {
                binding.btnAddfriend.visibility = View.GONE
                binding.btnUnFriend.visibility = View.VISIBLE
                binding.btnFriendRequested.visibility = View.GONE
            } else if (it==0) {
                binding.btnAddfriend.visibility = View.GONE
                binding.btnUnFriend.visibility = View.GONE
                binding.btnFriendRequested.visibility = View.VISIBLE
            } else {
                binding.btnAddfriend.visibility = View.VISIBLE
                binding.btnUnFriend.visibility = View.GONE
                binding.btnFriendRequested.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkFriend(uid: String) {
        viewModel.checkFriend(uid)
    }

}