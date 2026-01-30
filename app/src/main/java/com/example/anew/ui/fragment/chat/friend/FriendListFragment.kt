package com.example.anew.ui.fragment.chat.friend

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anew.R
import com.example.anew.databinding.FragmentFriendListBinding
import com.example.anew.support.fakeData
import com.example.anew.ui.fragment.chat.adapter.FriendListAdapter
import com.example.anew.viewmodelFactory.MyViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FriendListFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: FriendListViewModel
    private val MyViewModelFactory = MyViewModelFactory()


    private var _binding: FragmentFriendListBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this, MyViewModelFactory).get(FriendListViewModel::class.java)
        viewModel.fetchFriends()

        viewModel.friends.observe(viewLifecycleOwner) {
            (binding.rcvFriendList.adapter as FriendListAdapter).updateData(it)
            binding.progressBar.visibility = View.GONE
        }

        binding.rcvFriendList.adapter = FriendListAdapter(mutableListOf(),
            onClickJohnChatRoom = { // NOTOOTOTOTOOTOTOTOT
                findNavController().navigate(R.id.action_friendListFragment_to_chatRoomFragment, Bundle().apply {
                    putString("chat_type", "Private")
                    putString("chat_name", it.name)
                    putString("receiver_id", it.uid)
                    putString("receiver_name", it.name)
                    putString("receiver_avatar", it.photoUrl)
                    putString("chatId", listOf(it.uid, fakeData.user!!.uid).sorted().joinToString("_"))
                },navOptions {
                    anim {
                        enter = R.anim.side_in_right
                        exit = android.R.anim.fade_out
                        popEnter = android.R.anim.fade_in
                        popExit = android.R.anim.slide_out_right
                    }
                })
            },
            onClickViewProfileUser = {
                findNavController().navigate(R.id.action_friendListFragment_to_otherUserProfileFragment,Bundle().apply {
                    putString("uid", it)
                },navOptions {
                    anim {
                        enter = R.anim.side_in_right
                        exit = android.R.anim.fade_out
                        popEnter = android.R.anim.fade_in
                        popExit = android.R.anim.slide_out_right
                    }
                })
            })
        binding.rcvFriendList.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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