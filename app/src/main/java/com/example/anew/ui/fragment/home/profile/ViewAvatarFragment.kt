package com.example.anew.ui.fragment.home.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.anew.R
import com.example.anew.databinding.FragmentViewAvatarBinding

class ViewAvatarFragment : Fragment() {
    var _binding : FragmentViewAvatarBinding? =null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewAvatarBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.toolbar.inflateMenu(R.menu.view_avatar_menu)
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.save_image -> {
                    saveImageToDevice()
                    Toast.makeText(requireContext(), "Saved image", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        //load image from bundle
        val ImageUri= arguments?.getString("ImageUri")

        Glide.with(requireContext())
            .load(ImageUri)
            .error(R.drawable.ic_launcher_background)
            .into(binding.avatar)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun saveImageToDevice() {

    }
}