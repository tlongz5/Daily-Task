package com.example.anew.ui.fragment.home.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.anew.R
import com.example.anew.databinding.FragmentProfileBinding
import com.example.anew.databinding.ProfileBottomSheetImageSourceBinding
import com.example.anew.model.User
import com.example.anew.support.convertUriToCloudinaryUrl
import com.example.anew.support.deleteUserFromSharePref
import com.example.anew.support.fakeData
import com.example.anew.support.saveUserToSharePrefAndDataLocal
import com.example.anew.support.swapBitmapToUrl
import com.example.anew.support.updateAvatarFromSharePref
import com.example.anew.ui.activity.login.LoginActivity
import com.example.anew.viewmodelFactory.MyViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlin.jvm.java

class ProfileFragment: Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private val MyViewModelFactory = MyViewModelFactory()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    val pickImg = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            profileViewModel.updateAvatar(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(this, MyViewModelFactory)[ProfileViewModel::class.java]
        profileViewModel.imgState.observe(viewLifecycleOwner){
            Glide.with(requireContext())
                .load(it)
                .centerCrop()
                .into(binding.avatar)

            //update local
            updateAvatarFromSharePref(requireContext(),it)
        }

        binding.tvName2.setText(fakeData.user!!.name)
        binding.edtName.setText(fakeData.user!!.name)
        binding.edtUsername.setText("${fakeData.user!!.username}")
        binding.edtEmail.setText(fakeData.user!!.email)
        binding.edtPhoneNumber.setText(fakeData.user!!.phoneNumber)


        Glide.with(this)
            .load(fakeData.user!!.photoUrl)
            .error(R.drawable.ic_launcher_background)
            .centerCrop()
            .into(binding.avatar)

        binding.avatar.setOnClickListener {
            showBottomSheet()
        }

        binding.btnEdit.setOnClickListener {
            unlockEdit(binding.edtName)
            unlockEdit(binding.edtPhoneNumber)
            unlockEdit( binding.edtUsername)
            binding.btnSave.visibility = View.VISIBLE
            binding.btnEdit.visibility = View.GONE

            binding.edtName.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.edtName, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val username = binding.edtUsername.text.toString().trim()
            val phoneNumber = binding.edtPhoneNumber.text.toString().trim()

            if (name.isNotEmpty() && username.isNotEmpty() && phoneNumber.isNotEmpty()) {
                if(!phoneNumber.all { it.isDigit() } || !phoneNumber.startsWith("0")){
                    Toast.makeText(requireContext(), "Incorrect phone number format", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(!name.all { it.isLetter() || it.isWhitespace() } || !username.all { it.isDigit() || it.isLowerCase() } ){
                    Toast.makeText(requireContext(), "Incorrect name format", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewLifecycleOwner.lifecycleScope.launch {

                    if(profileViewModel.checkDuplicateUsername(username)){
                        Toast.makeText(requireContext(), "Username already exists, please choose another", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    binding.btnSave.isEnabled = false
                    binding.btnSave.alpha = 0.7f

                    Snackbar.make(view, "Save Loading...", Snackbar.LENGTH_SHORT).show()
                    try {
                        //update data in firebase and local
                        profileViewModel.updateProfile(name, username, phoneNumber)
                        saveUserToSharePrefAndDataLocal(
                            User(
                                fakeData.user!!.uid,
                                username,
                                name,
                                fakeData.user!!.email,
                                fakeData.user!!.photoUrl,
                                phoneNumber
                            ),requireContext())
                        Toast.makeText(requireContext(), "Save Success", Toast.LENGTH_SHORT).show()
                        binding.btnSave.visibility = View.GONE
                        binding.btnEdit.visibility = View.VISIBLE
                        binding.edtName.isEnabled = false
                        binding.edtUsername.isEnabled = false
                        binding.edtPhoneNumber.isEnabled = false
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Save Error", Toast.LENGTH_SHORT).show()
                        binding.btnSave.isEnabled = true
                        binding.btnSave.alpha = 1f
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnLogOut.setOnClickListener {
            try {
                //signout account
                profileViewModel.signOut(requireContext())

                val intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Logout Error", Toast.LENGTH_SHORT).show()
            }

        }

        binding.root.setOnClickListener {
            binding.edtUsername.clearFocus()
            binding.edtName.clearFocus()
            binding.edtPhoneNumber.clearFocus()
        }

        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val result = bundle.getInt("ImageUri")
                    profileViewModel.updateAvatar(
                        swapBitmapToUrl(requireContext(),
                            ContextCompat.getDrawable(requireContext(), result))
                    )

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Save Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun unlockEdit(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.isCursorVisible = true
        editText.isEnabled = true
    }

    private fun showBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingBottomSheet = ProfileBottomSheetImageSourceBinding.inflate(layoutInflater)
        bottomSheet.setContentView(bindingBottomSheet.root)
        bindingBottomSheet.tvViewAvatar.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("ImageUri", fakeData.user!!.photoUrl)

            // view avatar
            findNavController().navigate(R.id.action_profileFragment_to_viewAvatarFragment,bundle)
            bottomSheet.dismiss()
        }
        bindingBottomSheet.tvPickFromDevice.setOnClickListener {
            //pick from app
            pickAvtPreset()
            bottomSheet.dismiss()
        }
        bindingBottomSheet.tvUploadPhoto.setOnClickListener {
            pickImg.launch("image/*")
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