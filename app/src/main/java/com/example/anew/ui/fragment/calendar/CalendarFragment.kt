package com.example.anew.ui.fragment.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anew.R
import com.example.anew.data.local.MyHelper
import com.example.anew.databinding.FragmentCalendarBinding
import com.example.anew.utils.toCalendarDay
import com.example.anew.viewmodelfactory.MyViewModelFactory
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val myViewModelFactory = MyViewModelFactory()
    private lateinit var viewModel: CalendarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //init
        binding.rcvEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvEvent.adapter = CalendarAdapter { projectId ->
            findNavController().navigate(
                R.id.action_CalendarFragment_to_taskDetailFragment,
                Bundle().apply {
                    putString("id", projectId)
                },navOptions {
                    anim {
                        enter = R.anim.side_in_right
                        exit = android.R.anim.fade_out
                        popEnter = android.R.anim.fade_in
                        popExit = android.R.anim.slide_out_right
                    }
                })
        }

        binding.calendarView.setSelectedDate(CalendarDay.today())

        viewModel = ViewModelProvider(this, myViewModelFactory)[CalendarViewModel::class.java]
        viewModel.getTeamList(MyHelper.user!!.uid)
        viewModel.teamList.observe(viewLifecycleOwner) { projectList ->
            val projects =projectList.sortedBy { project -> project.dueTime }
            val redColor = getColor(requireContext(), R.color.task_failed_tv)
            val calendarList = HashSet<CalendarDay>()
            projects.forEach { project ->
                calendarList.add(project.dueTime!!.toCalendarDay())
             }

                binding.calendarView.addDecorator(
                    object : DayViewDecorator {
                        override fun shouldDecorate(day: CalendarDay?): Boolean {
                            return calendarList.contains(day)
                        }
                        override fun decorate(view: DayViewFacade?) {
                            view?.addSpan(DotSpan(8f, redColor))
                        }
                    }
                )

                binding.calendarView.setOnDateChangedListener { widget, date, selected ->
                    if(selected){
                        (binding.rcvEvent.adapter as CalendarAdapter).submitList(projects.filter { project ->
                            project.dueTime!!.toCalendarDay()==date
                        })
                    }
                }
            }
        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}