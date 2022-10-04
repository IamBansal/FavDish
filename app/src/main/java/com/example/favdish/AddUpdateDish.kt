package com.example.favdish

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.favdish.databinding.ActivityAddUpdateDishBinding
import com.example.favdish.databinding.DialogCustomImageSelectionBinding

class AddUpdateDish : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddUpdateDishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        binding.addDishImage.setOnClickListener(this)

    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Add Dish"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun customImageSelectionDialog(){
        val dialog = Dialog(this)
        val dialogBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.camera.setOnClickListener {
            Toast.makeText(this@AddUpdateDish, "Camera clicked", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialogBinding.gallery.setOnClickListener {
            Toast.makeText(this@AddUpdateDish, "Gallery clicked", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when(view.id) {
                R.id.addDishImage -> {
                    customImageSelectionDialog()
                }
            }
        }
    }
}