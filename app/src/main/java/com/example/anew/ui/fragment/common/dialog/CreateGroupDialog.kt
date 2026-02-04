package com.example.anew.ui.fragment.common.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.anew.R
import com.example.anew.databinding.DialogCreateGroupBinding

class CreateGroupDialog: DialogFragment(R.layout.dialog_create_group) {
    private lateinit var binding: DialogCreateGroupBinding

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogCreateGroupBinding.bind(view)
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnCreate.setOnClickListener {
            val groupName = binding.edtGroupName.text.toString().trim()
            if(groupName.isEmpty()){
                binding.edtGroupName.error = "Please enter Group Name"
            }else {
                binding.edtGroupName.error = null
                val bundle = Bundle().apply {
                    putString("group_name", groupName)
                }
                setFragmentResult("create_group", bundle)
                dismiss()
            }
        }

        binding.edtGroupName.requestFocus()
//        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

    }
}