package com.example.favdish.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.favdish.R
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.ActivityAddUpdateDishBinding
import com.example.favdish.databinding.DialogCustomImageSelectionBinding
import com.example.favdish.databinding.DialogCustomListBinding
import com.example.favdish.model.entity.FavDish
import com.example.favdish.utils.Constants
import com.example.favdish.view.adapters.ListItemAdapter
import com.example.favdish.viewModel.FavDishViewModel
import com.example.favdish.viewModel.FavDishViewModelFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddUpdateDish : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddUpdateDishBinding
    private var imagePath: String = ""
    private lateinit var mCustomListDialog: Dialog
    private var favDishDetails: FavDish? = null

    private val favDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_DISH_DETAILS)) {
            favDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)
        }

        setUpActionBar()

        favDishDetails?.let {
            if (it.id != 0) {
                imagePath = it.image
                Glide.with(this@AddUpdateDish)
                    .load(imagePath)
                    .centerCrop()
                    .into(binding.dishImage)

                binding.etTitle.setText(it.title)
                binding.etType.setText(it.type)
                binding.etCategory.setText(it.category)
                binding.etIngredients.setText(it.ingredients)
                binding.etCookingTime.setText(it.cookingTime)
                binding.etDirection.setText(it.directionToCook)
                binding.btnAddDish.text = "Update Dish"
            }
        }

        binding.addDishImage.setOnClickListener(this)
        binding.etType.setOnClickListener(this)
        binding.etCookingTime.setOnClickListener(this)
        binding.etCategory.setOnClickListener(this)
        binding.btnAddDish.setOnClickListener(this)

    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar)

        if (favDishDetails != null && favDishDetails!!.id != 0) {
            supportActionBar?.let {
                it.title = "Edit Dish"
            }
        } else {
            supportActionBar?.let {
                it.title = "Add Dish"
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun customImageSelectionDialog() {
        val dialog = Dialog(this)
        val dialogBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.camera.setOnClickListener {

            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showDialogForPermissions()
                }
            }).onSameThread().check()

            dialog.dismiss()
        }

        dialogBinding.gallery.setOnClickListener {
            Dexter.withContext(this).withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    startActivityForResult(
                        Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        ), GALLERY
                    )
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(
                        this@AddUpdateDish,
                        "You have denied the permissions required.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    showDialogForPermissions()
                }

            }).onSameThread().check()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun selectedListItem(item: String, selection: String) {
        when (selection) {
            Constants.DISH_TYPE -> {
                mCustomListDialog.dismiss()
                binding.etType.setText(item)
            }
            Constants.DISH_CATEGORY -> {
                mCustomListDialog.dismiss()
                binding.etCategory.setText(item)
            }
            Constants.DISH_COOKING_TIME -> {
                mCustomListDialog.dismiss()
                binding.etCookingTime.setText(item)
            }
        }
    }

    private fun showDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions required for this feature.\nIt can be enabled under Application Settings.")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun customItemsDialog(title: String, itemsList: ArrayList<String>, selection: String) {
        mCustomListDialog = Dialog(this)
        val binding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)

        binding.tvTitle.text = title

        binding.rvList.layoutManager = LinearLayoutManager(this)
        val adapter = ListItemAdapter(this, itemsList, selection)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()

    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.addDishImage -> {
                    customImageSelectionDialog()
                }
                R.id.etType -> {
                    customItemsDialog(
                        "SELECT DISH TYPE",
                        Constants.dishTypes(),
                        Constants.DISH_TYPE
                    )
                }
                R.id.etCategory -> {
                    customItemsDialog(
                        "SELECT DISH CATEGORY",
                        Constants.dishCategories(),
                        Constants.DISH_CATEGORY
                    )
                }
                R.id.etCookingTime -> {
                    customItemsDialog(
                        "SELECT COOKING TIME IN MINUTES",
                        Constants.dishCookTime(),
                        Constants.DISH_COOKING_TIME
                    )
                }
                R.id.btnAddDish -> {
                    val title = binding.etTitle.text.toString().trim { it <= ' ' }
                    val type = binding.etType.text.toString().trim { it <= ' ' }
                    val category = binding.etCategory.text.toString().trim { it <= ' ' }
                    val ingredients = binding.etIngredients.text.toString().trim { it <= ' ' }
                    val cookingTime = binding.etCookingTime.text.toString().trim { it <= ' ' }
                    val cookingDirection = binding.etDirection.text.toString().trim { it <= ' ' }

                    when {
                        TextUtils.isEmpty(imagePath) -> {
                            Toast.makeText(
                                this@AddUpdateDish,
                                "Select dish image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        TextUtils.isEmpty(title) -> {
                            Toast.makeText(
                                this@AddUpdateDish,
                                "Enter some title",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        TextUtils.isEmpty(type) -> {
                            Toast.makeText(
                                this@AddUpdateDish,
                                "Select dish type",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        TextUtils.isEmpty(category) -> {
                            Toast.makeText(
                                this@AddUpdateDish,
                                "Select dish category",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        TextUtils.isEmpty(ingredients) -> {
                            Toast.makeText(
                                this@AddUpdateDish,
                                "Enter dish ingredients",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        TextUtils.isEmpty(cookingDirection) -> {
                            Toast.makeText(
                                this@AddUpdateDish,
                                "Enter directions to cook",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        TextUtils.isEmpty(cookingTime) -> {
                            Toast.makeText(
                                this@AddUpdateDish,
                                "Select dish cooking time",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {

                            var dishId = 0
                            var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                            var favoriteDish = false

                            favDishDetails?.let {
                                if (it.id != 0) {
                                    dishId = it.id
                                    imageSource = it.imageSource
                                    favoriteDish = it.favoriteDish
                                }
                            }


                            val favDishDetails = FavDish(
                                imagePath,
                                imageSource,
                                title,
                                type,
                                category,
                                ingredients,
                                cookingTime,
                                cookingDirection,
                                favoriteDish,
                                dishId
                            )

                            if (dishId == 0) {
                                favDishViewModel.insert(favDishDetails)
                                Toast.makeText(
                                    this@AddUpdateDish,
                                    "Getting your dish ready.....",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                favDishViewModel.update(favDishDetails)
                                Toast.makeText(
                                    this@AddUpdateDish,
                                    "Updating your dish.....",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            finish()
                        }
                    }

                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                data?.extras?.let {
                    val thumbnail: Bitmap = it.get("data") as Bitmap

                    Glide.with(this).load(thumbnail).centerCrop().into(binding.dishImage)
                    imagePath = saveImageToInternalStorage(thumbnail)

                    Glide.with(this).load(R.drawable.ic_baseline_edit_24).into(binding.addDishImage)

                }
            } else if (requestCode == GALLERY) {
                data?.let {
                    val selectedPhotoUri = data.data

                    Glide.with(this)
                        .load(selectedPhotoUri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("TAG", "Error loading image", e)
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {

                                resource?.let {
                                    val bitmap: Bitmap = resource.toBitmap()
                                    imagePath = saveImageToInternalStorage(bitmap)
                                }

                                return false
                            }
                        })
                        .into(binding.dishImage)

                    Glide.with(this).load(R.drawable.ic_baseline_edit_24).into(binding.addDishImage)

                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this@AddUpdateDish, "Image selection cancelled.", Toast.LENGTH_SHORT)
                .show()
        }

    }

    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "FavDishImages"
    }
}