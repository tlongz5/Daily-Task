package com.example.anew.ui.fragment.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anew.R
import com.example.anew.databinding.BottomSheetSetTextAddBinding
import com.example.anew.databinding.FragmentAddBinding
import com.example.anew.model.Team
import com.example.anew.support.fakeData
import com.example.anew.support.getCurrentDate
import com.example.anew.support.getCurrentTime
import com.example.anew.support.mergeDateAndTime
import com.example.anew.support.toDayAndMonth
import com.example.anew.support.tranferToHourAndMinute
import com.example.anew.viewmodelFactory.MyViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddFragment : Fragment() {
    private val myViewModelFactory = MyViewModelFactory()
    private lateinit var viewModel: AddViewModel
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

        viewModel = ViewModelProvider(requireActivity(), myViewModelFactory)[AddViewModel::class.java]
        viewModel.teamState.observe(viewLifecycleOwner){
            (binding.rcvTeamMembers.adapter as AddTeamMembersAdapter).submitList(it)
            binding.btnCreateNewTask.isEnabled = true
            binding.btnCreateNewTask.alpha=1f
        }

        //init
        binding.tvDate.text= getCurrentDate()
        binding.tvTime.text = getCurrentTime()
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

        binding.layoutTime.setOnClickListener {
            val materialTimePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTheme(R.style.TimePickerTheme)
                .setTitleText("Set Time")
                .build()

            materialTimePicker.addOnPositiveButtonClickListener {
                setHour = materialTimePicker.hour
                setMinute = materialTimePicker.minute
                binding.tvTime.text = tranferToHourAndMinute(setHour!!,setMinute!!)
            }
            materialTimePicker.show(childFragmentManager,"time_picker")
        }

        binding.layoutDate.setOnClickListener {
            val materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Set Date")
                .setTheme(R.style.DatePickerTheme)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            materialDatePicker.addOnPositiveButtonClickListener {
                setDate = it
                binding.tvDate.text = it.toDayAndMonth()
            }
            materialDatePicker.show(childFragmentManager,"date_picker")
        }

        binding.btnAddMember.setOnClickListener {
            findNavController().navigate(R.id.action_AddFragment_to_selectAddMemberFragment)
        }

        //NOTEEEEEEE
        binding.btnCreateNewTask.setOnClickListener {
            if(binding.tvProjectName.text.isEmpty() ||
                binding.tvTaskDetail.text.isEmpty() ||
                viewModel.teamState.value.isNullOrEmpty() ||
                setDate==null || setHour==null){
                Toast.makeText(context, "Please fill all information", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // create team, bug change image from user but insignificant
            val team = Team(
                UUID.randomUUID().toString(),
                binding.tvProjectName.text.toString(),
                binding.tvTaskDetail.text.toString(),
                fakeData.user!!.uid,
                viewModel.teamState.value.plus(fakeData.user!!).map { it.uid },
                viewModel.teamState.value.plus(fakeData.user!!).map { it.photoUrl }.take(4),
                0,
                mergeDateAndTime(setDate!!,setHour!!,setMinute!!),
                true
            )
            viewModel.createProject(team)
            Toast.makeText(context, "Create Success", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
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
            if(type){
                binding.tvProjectName.text = it.toString()
            }else binding.tvTaskDetail.text = it.toString()
        }

        bottomSheet.setOnDismissListener {
            if(!check){
                if(type) binding.tvProjectName.text = ""
                else binding.tvTaskDetail.text = ""
            }
        }
        bottomSheet.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}