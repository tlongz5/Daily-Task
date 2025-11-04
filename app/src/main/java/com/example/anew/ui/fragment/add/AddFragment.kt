package com.example.anew.ui.fragment.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anew.R
import com.example.anew.databinding.BottomSheetSetTextAddBinding
import com.example.anew.ui.fragment.add.AddTeamMembersAdapter
import com.example.anew.databinding.FragmentAddBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddFragment : Fragment() {

    var setDate: Long?=null
    var setHour: Int?=null
    var setMinute: Int?=null

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
        val linearLayout = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)
        binding.rcvTeamMembers.layoutManager =linearLayout

        binding.tvProjectName.setOnClickListener {
            getBottomSheet(true)
        }

        binding.tvTaskDetail.setOnClickListener {
            getBottomSheet(false)
        }

        binding.dueTime.setOnClickListener {
            val materialTimePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Set Time")
                .build()

            materialTimePicker.addOnPositiveButtonClickListener {
                setHour = materialTimePicker.hour
                setMinute = materialTimePicker.minute
                binding.tvTime.text = "$setHour:$setMinute"
            }
            materialTimePicker.show(childFragmentManager,"time_picker")
        }

        binding.dueDate.setOnClickListener {
            val materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Set Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            materialDatePicker.addOnPositiveButtonClickListener {
                setDate = it
                val date = Date(it)
                val tranformDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.tvDate.text = tranformDate.format(date)
            }
            materialDatePicker.show(childFragmentManager,"date_picker")
        }

        binding.btnAddMember.setOnClickListener {
            findNavController().navigate(R.id.action_AddFragment_to_selectAddMemberFragment)
        }
    }

//Note
    private fun getBottomSheet(type: Boolean) {
        var check: Boolean = false // check Done

        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingBottomSheet = BottomSheetSetTextAddBinding.inflate(layoutInflater)
        bottomSheet.setContentView(bindingBottomSheet.root)

        bindingBottomSheet.edtSetText.requestFocus()
    //Note
        bottomSheet.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        bindingBottomSheet.btnDone.setOnClickListener {
            check=true
            bottomSheet.dismiss()
        }

        bindingBottomSheet.edtSetText.addTextChangedListener {
            if(type == true){
                binding.tvProjectName.text = it.toString()
            }else binding.tvTaskDetail.text = it.toString()
        }

        bottomSheet.setOnDismissListener {
            if(check == false){
                binding.tvProjectName.text = ""
                binding.tvTaskDetail.text = ""
            }
        }
        bottomSheet.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}