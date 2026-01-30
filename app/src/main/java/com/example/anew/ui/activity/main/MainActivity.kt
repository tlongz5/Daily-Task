package com.example.anew.ui.activity.main

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.anew.R
import com.example.anew.databinding.ActivityMainBinding
import com.example.anew.support.fakeData
import com.example.anew.viewmodelFactory.MyViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private lateinit var viewModel: MainViewModel
    private val myViewModelFactory = MyViewModelFactory()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, myViewModelFactory)[MainViewModel::class.java]
        // init if need
        viewModel.initCounter(fakeData.user!!.uid)
        lifecycleScope.launch {
            viewModel.counter.collect {
                val count = it.toInt()
                if (count > 0) {
                    binding.bottomNav.getOrCreateBadge(R.id.NotificationFragment).number = count
                } else binding.bottomNav.removeBadge(R.id.NotificationFragment)

            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val navController = findNavController(R.id.fragmentContainerView)
                if(!navController.popBackStack()) finish()
            }
        })

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.HomeFragment,
                R.id.ChatFragment,
                R.id.NotificationFragment,
                R.id.AddFragment,
                R.id.CalendarFragment -> {
                    binding.bottomNav.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNav.visibility = View.GONE
                }
            }
        }
    }

    fun showBottomNav(type: Boolean){
        binding.bottomNav.visibility = if(type) View.VISIBLE else View.GONE
    }
}