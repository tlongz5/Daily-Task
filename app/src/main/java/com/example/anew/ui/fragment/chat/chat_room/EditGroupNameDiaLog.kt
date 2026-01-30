package com.example.anew.ui.fragment.chat.chat_room

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.anew.R
import com.example.anew.databinding.DialogEditNameBinding

class EditGroupNameDiaLog : DialogFragment(R.layout.dialog_edit_name) {
    private lateinit var binding: DialogEditNameBinding

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
        binding = DialogEditNameBinding.bind(view)
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
                    putString("edit_name", groupName)
                }
                setFragmentResult("edit_name", bundle)
                dismiss()
            }
        }

        binding.edtGroupName.requestFocus()
//        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

    }
}