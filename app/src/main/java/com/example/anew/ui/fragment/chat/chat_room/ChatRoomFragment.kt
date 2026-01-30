package com.example.anew.ui.fragment.chat.chat_room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.R
import com.example.anew.databinding.DialogAddMemberBinding
import com.example.anew.databinding.DialogConfirmLeaveBinding
import com.example.anew.databinding.DialogDeleteMemberBinding
import com.example.anew.databinding.DialogGroupOptionBinding
import com.example.anew.databinding.DialogPrivateOptionBinding
import com.example.anew.databinding.DialogProjectOptionBinding
import com.example.anew.databinding.FragmentChatRoomBinding
import com.example.anew.model.UiState
import com.example.anew.model.User
import com.example.anew.support.DataTranfer
import com.example.anew.support.animCb
import com.example.anew.support.animProgress
import com.example.anew.support.fakeData
import com.example.anew.ui.fragment.add.PickFriendAdapter
import com.example.anew.ui.fragment.chat.chat_room.EditGroupNameDiaLog
import com.example.anew.ui.fragment.chat.adapter.ChatRoomAdapter
import com.example.anew.ui.fragment.chat.adapter.DeleteMemberAdapter
import com.example.anew.viewmodelFactory.MyViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlin.String

class ChatRoomFragment : Fragment() {
    private val myViewModelFactory = MyViewModelFactory()
    private lateinit var viewModel: ChatRoomViewModel
    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatId: String
    private lateinit var chatName: String
    private lateinit var chatType: String
    private lateinit var receiverId: String
    private lateinit var receiverName: String
    private lateinit var receiverAvatar: String
    private lateinit var adminId: String

    //list to save user picked from add members to group
    private val listUserPicked = mutableListOf<String>()

    private var listMember = mutableListOf<User>()

    private val listFriend = mutableListOf<User>()

    private var isFriend = false
    private var dialogPrivateBinding: DialogPrivateOptionBinding? = null


    private val pickImg = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(10)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.pushMessage(
                chatId,
                chatName,

                fakeData.user!!.uid,
                fakeData.user!!.name,
                fakeData.user!!.photoUrl,

                receiverId,
                receiverName,
                receiverAvatar, //save all avatar, group or private

                "Sent ${uris.size} image",
                uris,
                chatType
            )
        }
    }

    private val pickOneImgOnly = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    //chua Dam bao thuoC tinh atomic
                    viewModel.changeAvatar(chatType, chatId, uri)
                    viewModel.changeAvatarProject( chatId, uri)
                    binding.imgAvatarToolbar.setImageURI(uri)
                    Snackbar.make(
                        binding.root,
                        "Waiting...",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }catch (e:Exception){
                    Toast.makeText(requireContext(),"Something went wrong, please try again",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, myViewModelFactory)[ChatRoomViewModel::class.java]

        //getData from Bundle
        chatType = arguments?.getString("chat_type") ?: "Private"
        chatId = arguments?.getString("chatId") ?: ""
        chatName = arguments?.getString("chat_name") ?: ""
        receiverId = arguments?.getString("receiver_id") ?: ""
        receiverName = arguments?.getString("receiver_name") ?: ""
        receiverAvatar = arguments?.getString("receiver_avatar") ?: ""
        adminId = arguments?.getString("admin_id") ?: ""

        setUpViewModel(chatId!!)

        //init
        if (chatType == "Project") {
            //get data for progress
            viewModel.getProject(chatId)
        }
        if(chatType == "Private") viewModel.checkFriend(receiverId)

        binding.tvNameToolbar.text = chatName
        Glide.with(this)
            .load(receiverAvatar)
            .circleCrop()
            .into(binding.imgAvatarToolbar)

        binding.rcvMessage.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ChatRoomAdapter()
        adapter.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (!binding.rcvMessage.canScrollVertically(1))
                        binding.rcvMessage.post {
                            binding.rcvMessage.scrollToPosition(adapter.itemCount - 1)
                        }
                }
            })
        binding.rcvMessage.adapter = adapter
        binding.rcvMessage.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!binding.rcvMessage.canScrollVertically(-1)){
                    viewModel.loadMoreMessages(chatId, adapter.currentList.first().messageId)
                }
            }
        })

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSendMessage.setOnClickListener {
            val message = binding.editMessage.text.toString()
            binding.editMessage.clearFocus()
            binding.editMessage.text.clear()
            if (message.isEmpty() || message.isBlank()) return@setOnClickListener
            viewModel.pushMessage(
                chatId,
                chatName,

                fakeData.user!!.uid,
                fakeData.user!!.name,
                fakeData.user!!.photoUrl,

                receiverId,
                receiverName,
                receiverAvatar, //save all avatar group or private

                message,
                emptyList(),
                chatType
            )

        }

        binding.btnPickImage.setOnClickListener {
            pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnShow.setOnClickListener {
            when (chatType) {
                "Group" -> showDialogGroupOptions()
                "Private" -> showDialogPrivateOptions()
                else -> showDialogProjectOptions()
            }
        }

        parentFragmentManager.setFragmentResultListener(
            "edit_name",
            viewLifecycleOwner
        ) { _, bundle ->
            val groupName = bundle.getString("edit_name")
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.editGroupName(chatType,chatId, groupName!!)
                //giai phap tam thoi vi chua dam bao tinh atomic, co time thi fix
                if (chatType=="Project") viewModel.editNameProject(chatId,groupName)
                binding.tvNameToolbar.text = groupName
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViewModel(chatId: String) {
        viewModel.getMessages(chatId)
        if (chatType != "Private") viewModel.getConversationInfo(chatId)

        viewModel.conversationInfoState.observe(viewLifecycleOwner) {
            adminId = it.adminId
            binding.tvDynamic.text = "${it.users.size} members"

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.loadData(it.users.keys.toList())
                listMember = it.users.keys.map { it -> DataTranfer.userCache[it]!! }.toMutableList()
            }
        }

        viewModel.projectState.observe(viewLifecycleOwner) {

            binding.progressRing.visibility = View.VISIBLE
            binding.checkBox.visibility = View.VISIBLE
            animProgress(binding.progressRing, null,
                binding.progressRing.progress,it.completedPercent)

            binding.checkBox.setOnCheckedChangeListener(null)
            binding.checkBox.isEnabled= false
            binding.checkBox.alpha = 0.6f
            binding.progressRing.alpha = 0.6f
            val isChecked = it.membersCompleted.contains(fakeData.user!!.uid)
            binding.checkBox.isChecked = isChecked
            binding.checkBox.animCb(isChecked)

            if(it.inProgress){
                binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                    viewModel.updateProgress(isChecked)
                    binding.checkBox.animCb(isChecked)
                }
                binding.checkBox.isEnabled= true
                binding.checkBox.alpha = 1f
                binding.progressRing.alpha = 1f
            }
        }

        viewModel.checkFriendState.observe(viewLifecycleOwner){
            isFriend = (it==1)

            dialogPrivateBinding?.let { dialogPrivateBinding ->
                updateDialogBinding(dialogPrivateBinding)
            }
        }

        viewModel.getFriendListData(fakeData.user!!.uid)
        viewModel.friendListState.observe(viewLifecycleOwner) {
            listFriend.clear()
            listFriend.addAll(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.messageState.collect {
                    when (it) {
                        is UiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is UiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rcvMessage.visibility = View.VISIBLE
                            (binding.rcvMessage.adapter as ChatRoomAdapter).submitList(it.data)

                        }

                        is UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rcvMessage.visibility = View.GONE
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    // Process each option
    private fun showDialogGroupOptions() {
        val dialogBinding = DialogGroupOptionBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        if(adminId!=fakeData.user!!.uid){
            dialogBinding.btnDeleteMember.visibility = View.GONE
            dialogBinding.btnChangeLeader.visibility = View.GONE
        }

        dialogBinding.btnChangeGroupPhoto.setOnClickListener {
            pickOneImgOnly.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        dialogBinding.btnEditName.setOnClickListener {
            setupChangeNameLogic()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnDeleteMember.setOnClickListener {
            setupDeleteMemberLogic(dialog)
        }

        dialogBinding.btnAddMember.setOnClickListener {
            setupAddMemberLogic(dialog)
        }

        dialogBinding.btnChangeLeader.setOnClickListener {
            setupChangeLeaderLogic(dialog)
        }

        dialogBinding.btnLeaveGroup.setOnClickListener {
            setupLeaveGroupLogic(dialog)
        }

        dialog.show()
    }

    private fun showDialogPrivateOptions() {
        val dialogBinding = DialogPrivateOptionBinding.inflate(layoutInflater)
        dialogPrivateBinding = dialogBinding
        val dialog = AlertDialog.Builder(requireContext())
            .setView( dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnProfile.setOnClickListener {
            findNavController().navigate(
                R.id.action_chatRoomFragment_to_otherUserProfileFragment,
                Bundle().apply {
                    putString("uid", receiverId)
                },navOptions {
                    anim {
                        enter = R.anim.side_in_right
                        exit = android.R.anim.fade_out
                        popEnter = android.R.anim.fade_in
                        popExit = android.R.anim.slide_out_right
                    }
                })
            dialog.dismiss()
        }

        updateDialogBinding( dialogBinding)

        dialogBinding.btnMakeFriend.setOnClickListener {
            viewModel.requestFriend(receiverId)
        }

        dialogBinding.btnUnfriend.setOnClickListener {
            viewModel.unFriend(receiverId)
        }


        dialog.show()

    }

    private fun updateDialogBinding( dialogBinding: DialogPrivateOptionBinding) {
        if(!isFriend){
            dialogBinding.btnMakeFriend.isEnabled =true
            dialogBinding.btnMakeFriend.alpha = 1f
            dialogBinding.btnUnfriend.isEnabled = false
            dialogBinding.btnUnfriend.alpha = 0.5f
        }else{
            dialogBinding.btnMakeFriend.isEnabled = false
            dialogBinding.btnMakeFriend.alpha = 0.5f
            dialogBinding.btnUnfriend.isEnabled = true
            dialogBinding.btnUnfriend.alpha = 1f
        }
    }

    private fun showDialogProjectOptions() {
        val dialogBinding = DialogProjectOptionBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        if(adminId!=fakeData.user!!.uid){
            dialogBinding.btnDeleteMember.visibility = View.GONE
            dialogBinding.btnChangeLeader.visibility = View.GONE
        }

        dialogBinding.btnChangeGroupPhoto.setOnClickListener {
            setupChangeAvatarLogic()
        }

        dialogBinding.btnEditName.setOnClickListener {
            setupChangeNameLogic()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnDeleteMember.setOnClickListener {
            setupDeleteMemberLogic(dialog)
        }

        dialogBinding.btnAddMember.setOnClickListener {
            setupAddMemberLogic(dialog)
        }

        dialogBinding.btnChangeLeader.setOnClickListener {
            setupChangeLeaderLogic(dialog)
        }

        dialogBinding.btnLeaveGroup.setOnClickListener {
            setupLeaveGroupLogic(dialog)
        }

        dialogBinding.btnCheckProject.setOnClickListener {
            findNavController().navigate(
                R.id.action_chatRoomFragment_to_taskDetailFragment,
                Bundle().apply {
                    putString("id", chatId)
                },navOptions {
                    anim {
                        enter = R.anim.side_in_right
                        exit = android.R.anim.fade_out
                        popEnter = android.R.anim.fade_in
                        popExit = android.R.anim.slide_out_right
                    }
                })
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupChangeNameLogic() {
        val dialog = EditGroupNameDiaLog()
        dialog.show(parentFragmentManager, "edit_name")
    }

    private fun setupChangeAvatarLogic() {
        pickOneImgOnly.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setupDeleteMemberLogic(dialog: AlertDialog) {
        val dialogDeleteBinding = DialogDeleteMemberBinding.inflate(layoutInflater)
        val dialogDelete = AlertDialog.Builder(requireContext())
            .setView(dialogDeleteBinding.root)
            .create()
        dialogDelete.window?.setBackgroundDrawableResource(android.R.color.transparent)

        //init
        dialogDeleteBinding.rcvMembers.layoutManager = LinearLayoutManager(requireContext())
        dialogDeleteBinding.rcvMembers.adapter = DeleteMemberAdapter(adminId) {
            val acceptDialog = AlertDialog.Builder(requireContext())
                .setMessage("Are you sure to delete this member?")
                .setPositiveButton("Yes") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        //Note for update after
                        removeMember(chatType,chatId,it.uid)
                        listMember-=it
                        binding.tvDynamic.text = "${listMember.size} members"
                        dialogDelete.dismiss()
                    }
                }
                .setNegativeButton("No") { _, _ ->
                }
                .create()

            acceptDialog.show()
            acceptDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        }
        (dialogDeleteBinding.rcvMembers.adapter as DeleteMemberAdapter).submitList(listMember)

        dialogDeleteBinding.btnCancel.setOnClickListener {
            dialogDelete.dismiss()
        }

        dialogDelete.show()
        dialog.dismiss()
    }

    private fun setupLeaveGroupLogic(dialog: AlertDialog) {
        val dialogLeaveBinding = DialogConfirmLeaveBinding.inflate(layoutInflater)
        val dialogLeave = AlertDialog.Builder(requireContext())
            .setView(dialogLeaveBinding.root)
            .create()
        dialogLeave.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogLeaveBinding.btnCancel.setOnClickListener {
            dialogLeave.dismiss()
        }

        dialogLeaveBinding.btnConfirm.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    removeMember(chatType,chatId,fakeData.user!!.uid)
                    Snackbar.make(
                        binding.root,
                        "Leave group successfully",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    dialogLeave.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong, please try again",
                        Toast.LENGTH_SHORT
                    )
                }
            }

        }
        dialogLeave.show()
        dialog.dismiss()
    }

    suspend fun removeMember(chatType: String, chatId: String, userId: String) {
        viewModel.removeMemberFromGroup(chatType, chatId, userId)
        if (chatType=="Project") viewModel.updateDataAfterAddOrDelete(false,listOf(userId))
        // if leave group
        if(userId==fakeData.user!!.uid) {
            putBundle()
            findNavController().popBackStack()
        }

        //if admin leave group or admin DELETE themself
        else if(userId==adminId){
            val newListMember = listMember.filter { user -> user.uid!= adminId}
            if(newListMember.isNotEmpty()){
                adminId = newListMember.random().uid
                viewModel.changeLeader(chatId, adminId)
            }
            putBundle()
            findNavController().popBackStack()
        }
    }

    private fun putBundle() {
        val bundle = Bundle().apply { putBoolean("swap_screen", true) }
        requireActivity().supportFragmentManager.setFragmentResult("request", bundle)
        findNavController().popBackStack()
    }

    private fun setupAddMemberLogic(dialog: AlertDialog) {
        //clear after add
        listUserPicked.clear()

        //reuse adapter pick friend to use add member
        val dialogAddBinding = DialogAddMemberBinding.inflate(layoutInflater)
        val dialogAdd = AlertDialog.Builder(requireContext())
            .setView(dialogAddBinding.root)
            .create()
        dialogAdd.window?.setBackgroundDrawableResource(android.R.color.transparent)

        //init
        dialogAddBinding.rcvFriends.layoutManager = LinearLayoutManager(requireContext())
        dialogAddBinding.rcvFriends.adapter = PickFriendAdapter { isChecked, user ->
            if (isChecked) listUserPicked.add(user.uid)
            else listUserPicked.remove(user.uid)

            if (listUserPicked.isEmpty()) {
                dialogAddBinding.btnAdd.isEnabled = false
                dialogAddBinding.btnAdd.alpha = 0.5f
            } else {
                dialogAddBinding.btnAdd.isEnabled = true
                dialogAddBinding.btnAdd.alpha = 1f
            }
        }

        //cast fix temp
        viewLifecycleOwner.lifecycleScope.launch {
            (dialogAddBinding.rcvFriends.adapter as PickFriendAdapter).submitData(
                PagingData.from(listFriend - listMember )
            )
        }

        dialogAddBinding.btnCancel.setOnClickListener {
            dialogAdd.dismiss()
        }

        dialogAddBinding.btnAdd.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
    //giai phap tam thoi vi chua dam bao tinh atomic, bh co time thi fix

                    //handle if chatType is Project
                    if(chatType=="Project") viewModel.updateDataAfterAddOrDelete(true,listUserPicked)

                    viewModel.addMember(chatType, chatId, listUserPicked)
                    listMember.addAll(listUserPicked.map { DataTranfer.userCache[it]!! })
                    binding.tvDynamic.text = "${listMember.size} members"
                    Snackbar.make(binding.root, "Add member successfully", Snackbar.LENGTH_SHORT)
                        .show()
                    dialogAdd.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong, please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

        dialogAdd.show()
        dialog.dismiss()
    }

    private fun setupChangeLeaderLogic(dialog: AlertDialog) {
        val dialogDeleteBinding = DialogDeleteMemberBinding.inflate(layoutInflater)
        val dialogDelete = AlertDialog.Builder(requireContext())
            .setView(dialogDeleteBinding.root)
            .create()
        dialogDelete.window?.setBackgroundDrawableResource(android.R.color.transparent)

        //init
        dialogDeleteBinding.tvTitle.text = "Change Leader"
        dialogDeleteBinding.tvDescription.text = "Select member to change leader"
        dialogDeleteBinding.rcvMembers.layoutManager = LinearLayoutManager(requireContext())
        dialogDeleteBinding.rcvMembers.adapter = DeleteMemberAdapter(adminId) {
            val acceptDialog = AlertDialog.Builder(requireContext())
                .setMessage("Are you sure you want to change leader?")
                .setPositiveButton("Yes") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        try {
                            viewModel.changeLeader(chatId, it.uid)
                            adminId = it.uid
                            dialogDelete.dismiss()
                            Snackbar.make(
                                binding.root,
                                "Change leader successfully",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "Something went wrong, please try again",
                                Toast.LENGTH_SHORT
                            )
                        }
                    }
                }
                .setNegativeButton("No") { _, _ ->
                }
                .create()

            acceptDialog.show()
            acceptDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        }
        (dialogDeleteBinding.rcvMembers.adapter as DeleteMemberAdapter).submitList(listMember)

        dialogDeleteBinding.btnCancel.setOnClickListener {
            dialogDelete.dismiss()
        }

        dialogDelete.show()
        dialog.dismiss()
    }


}