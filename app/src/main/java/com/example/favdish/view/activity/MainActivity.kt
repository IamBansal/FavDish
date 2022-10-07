package com.example.favdish.view.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.favdish.R
import com.example.favdish.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_all_dishes, R.id.navigation_favorite_dishes, R.id.navigation_random_dishes
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    fun hideBottomNavView(){
        binding.navView.clearAnimation()
        binding.navView.visibility = View.GONE
        binding.navView.animate().translationY(binding.navView.height.toFloat()).duration = 300
    }

    fun showBottomNavView(){
        binding.navView.clearAnimation()
        binding.navView.visibility = View.VISIBLE
        binding.navView.animate().translationY(0f).duration = 300
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Home"
    }
}