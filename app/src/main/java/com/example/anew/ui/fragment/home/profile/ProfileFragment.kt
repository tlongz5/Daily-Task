package com.example.anew.ui.fragment.home.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.anew.R
import com.example.anew.databinding.FragmentProfileBinding
import com.example.anew.databinding.ProfileBottomSheetImageSourceBinding
import com.example.anew.model.fakeData
import com.example.anew.ui.activity.login.LoginActivity
import com.example.anew.viewmodelFactory.MyViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.jvm.java

class ProfileFragment: Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private val MyViewModelFactory = MyViewModelFactory()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(this, MyViewModelFactory).get(ProfileViewModel::class.java)

        binding.edtName.setText(fakeData.user!!.name)
        binding.tvEmail.setText(fakeData.user!!.email)
        binding.edtPhoneNumber.setText(fakeData.user!!.phoneNumber)


        Glide.with(this)
            .load(fakeData.user!!.photoUrl)
            .error(R.drawable.ic_launcher_background)
            .circleCrop()
            .into(binding.avatar)

        binding.avatar.setOnClickListener {
            showBottomSheet()
        }


        binding.btnLogOut.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()

            //signout account
            profileViewModel.signOut(requireContext())
        }

        binding.btnEditName.setOnClickListener {
            binding.edtName.isFocusableInTouchMode = true
            binding.edtName.requestFocus()

            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.edtName, InputMethodManager.SHOW_IMPLICIT)

        }

        binding.btnEditPhone.setOnClickListener {
            binding.edtPhoneNumber.isFocusableInTouchMode = true
            binding.edtPhoneNumber.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.edtPhoneNumber, InputMethodManager.SHOW_IMPLICIT)
        }

    }

    private fun showBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingBottomSheet = ProfileBottomSheetImageSourceBinding.inflate(layoutInflater)
        bottomSheet.setContentView(bindingBottomSheet.root)
        bindingBottomSheet.tvViewAvatar.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("ImageUri", fakeData.user!!.photoUrl)
            findNavController().navigate(R.id.action_profileFragment_to_viewAvatarFragment,bundle)
            bottomSheet.dismiss()
        }
        bindingBottomSheet.tvPickFromDevice.setOnClickListener {
//Note: not implement yet
            pickAvtPreset()
            bottomSheet.dismiss()
        }
        bindingBottomSheet.tvUploadPhoto.setOnClickListener {
//Note
            bottomSheet.dismiss()
        }

        bottomSheet.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun pickAvtPreset() {
        val bottomSheetDialogFragment = PickPresetAvatarBottomSheet()
        bottomSheetDialogFragment.show(parentFragmentManager, bottomSheetDialogFragment.tag)
    }

}