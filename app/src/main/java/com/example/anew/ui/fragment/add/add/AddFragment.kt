package com.example.anew.ui.fragment.add.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anew.R
import com.example.anew.databinding.BottomSheetSetTextAddBinding
import com.example.anew.databinding.FragmentAddBinding
import com.example.anew.model.Team
import com.example.anew.data.local.MyHelper
import com.example.anew.utils.getCurrentDate
import com.example.anew.utils.getCurrentTime
import com.example.anew.utils.mergeDateAndTime
import com.example.anew.utils.toDayAndMonth
import com.example.anew.utils.tranferToHourAndMinute
import com.example.anew.ui.activity.main.MainActivity
import com.example.anew.ui.fragment.add.adapter.AddTeamMembersAdapter
import com.example.anew.viewmodelfactory.MyViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.UUID

class AddFragment : Fragment() {
    private val myViewModelFactory = MyViewModelFactory()
    private lateinit var viewModel: AddViewModel

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
        binding.tvDate.text= if (viewModel.setDate!=null) viewModel.setDate!!.toDayAndMonth() else getCurrentDate()
        binding.tvTime.text = if (viewModel.setHour!=null) tranferToHourAndMinute(viewModel.setHour!!,viewModel.setMinute!!) else getCurrentTime()
        binding.tvProjectName.text = viewModel.projectName?:""
        binding.tvTaskDetail.text = viewModel.taskDetail?:""

        if(viewModel.setDate==null) viewModel.setDate = MaterialDatePicker.todayInUtcMilliseconds()
        if(viewModel.setHour==null) {
            viewModel.setHour = getCurrentTime().split(":")[0].toInt()
            viewModel.setMinute = getCurrentTime().split(":")[1].take(2).toInt()
        }

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
                viewModel.setHour = materialTimePicker.hour
                viewModel.setMinute = materialTimePicker.minute
                binding.tvTime.text = tranferToHourAndMinute(viewModel.setHour!!,viewModel.setMinute!!)
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
                viewModel.setDate = it
                binding.tvDate.text = it.toDayAndMonth()
            }
            materialDatePicker.show(childFragmentManager,"date_picker")
        }

        binding.btnAddMember.setOnClickListener {
            findNavController().navigate(R.id.action_AddFragment_to_selectAddMemberFragment,null,
                navOptions {
                    anim {
                        enter = R.anim.side_in_right
                        exit = android.R.anim.fade_out
                        popEnter = android.R.anim.fade_in
                        popExit = android.R.anim.slide_out_right
                    }
                })
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        //NOTEEEEEEE
        binding.btnCreateNewTask.setOnClickListener {
            if(binding.tvProjectName.text.trim().isEmpty() ||
                binding.tvTaskDetail.text.trim().isEmpty() ||
                viewModel.teamState.value.isNullOrEmpty() ||
                viewModel.setDate==null || viewModel.setHour==null){
                Toast.makeText(context, "Please fill all information", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(mergeDateAndTime(viewModel.setDate!!,viewModel.setHour!!,viewModel.setMinute!!)!! < System.currentTimeMillis()){
                Toast.makeText(context, "Please select a valid date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // create team, bug change image from user but insignificant
            val team = Team(
                UUID.randomUUID().toString(),
                binding.tvProjectName.text.toString(),
                binding.tvTaskDetail.text.toString(),
                MyHelper.groupAvatar.random(),
                MyHelper.user!!.uid,
                viewModel.teamState.value.plus(MyHelper.user!!).map { it.uid },
                viewModel.teamState.value.plus(MyHelper.user!!).map { it.photoUrl }.take(4),
                0,
                mergeDateAndTime(viewModel.setDate!!,viewModel.setHour!!,viewModel.setMinute!!),
                true,
                listOf()
            )

            viewModel.createProject(team)

            //reset data in viewModel
            viewModel.setDate = null
            viewModel.setHour = null
            viewModel.setMinute = null
            viewModel.projectName = null
            viewModel.taskDetail = null
            viewModel.saveUser(listOf())

            Toast.makeText(context, "Create Success", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = (45 * resources.displayMetrics.density).toInt()
        }
    }

//Note
    private fun getBottomSheet(type: Boolean) {
        var check = false // check Done
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
                viewModel.projectName = it.toString()
            }else {
                binding.tvTaskDetail.text = it.toString()
                viewModel.taskDetail = it.toString()
            }
        }

        bottomSheet.setOnDismissListener {
            if(!check){
                if(type) {
                    binding.tvProjectName.text = ""
                    viewModel.projectName = ""
                }
                else {
                    binding.tvTaskDetail.text = ""
                    viewModel.taskDetail = ""
                }
            }
        }
        bottomSheet.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}