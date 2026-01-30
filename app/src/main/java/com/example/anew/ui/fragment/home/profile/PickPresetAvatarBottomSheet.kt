package com.example.anew.ui.fragment.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.anew.databinding.BottomSheetPickPresetAvatarBinding
import com.example.anew.support.MyHelper
import com.example.anew.support.fakeData
import com.example.anew.ui.fragment.home.adapter.AvatarPresetAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PickPresetAvatarBottomSheet : BottomSheetDialogFragment() {

    var _binding: BottomSheetPickPresetAvatarBinding? = null
    private val binding get() = _binding!!
    private var saveImageItem:Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetPickPresetAvatarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //load data to recycler view and active save button when click item
        binding.rcvImagePreset.adapter = AvatarPresetAdapter {
            Glide.with(requireContext())
                .load(MyHelper.avatar[it])
                .centerCrop()
                .into(binding.avatar)

            saveImageItem = MyHelper.avatar[it]
            binding.tvSave.alpha = 1f
            binding.tvSave.isEnabled = true
        }
        binding.rcvImagePreset.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.tvSave.alpha=0.5f
        binding.tvSave.isEnabled=false

        Glide.with(requireContext())
            .load(fakeData.user!!.photoUrl)
            .centerCrop()
            .into(binding.avatar)

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        //save to bundle and back to profile fragment
        binding.tvSave.setOnClickListener {
            if(saveImageItem!=null){
                val bundle = Bundle()
                bundle.putInt("ImageUri", saveImageItem!!)
                setFragmentResult("requestKey", bundle)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}