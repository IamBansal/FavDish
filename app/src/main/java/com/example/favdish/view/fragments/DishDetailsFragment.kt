package com.example.favdish.view.fragments

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.favdish.R
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.FragmentDishDetailsBinding
import com.example.favdish.viewModel.FavDishViewModel
import com.example.favdish.viewModel.FavDishViewModelFactory
import java.io.IOException

class DishDetailsFragment : Fragment() {

    private lateinit var binding: FragmentDishDetailsBinding
    private val favDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDishDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: DishDetailsFragmentArgs by navArgs()

        args.let {
            try {
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {

                            Palette.from(resource!!.toBitmap()).generate { palette ->
                                palette?.let {
                                    val intColor = palette.vibrantSwatch?.rgb ?: 0
                                    binding.clDishDetail.setBackgroundColor(intColor)
                                }
                            }

                            return false
                        }

                    })
                    .into(binding.ivDishDetail)
                binding.tvTitleDishDetail.text = it.dishDetails.title
                binding.tvTypeDishDetail.text = it.dishDetails.type
                binding.tvCategoryDishDetail.text = it.dishDetails.category
                binding.tvIngredientsDishDetail.text = it.dishDetails.ingredients
                binding.tvDirectionDishDetail.text = it.dishDetails.directionToCook
                binding.tvTimeDishDetail.text =
                    "This dish will take an estimated time of ${it.dishDetails.cookingTime} minutes."

                if (args.dishDetails.favoriteDish) {
                    binding.ivDishFav.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_fav_dish_selected
                        )
                    )
                } else {
                    binding.ivDishFav.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_fav_dish
                        )
                    )
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        binding.ivDishFav.setOnClickListener {
            args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish

            favDishViewModel.update(args.dishDetails)

            if (args.dishDetails.favoriteDish) {
                binding.ivDishFav.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_fav_dish_selected
                    )
                )
                Toast.makeText(requireActivity(), "Favorite", Toast.LENGTH_SHORT).show()
            } else {
                binding.ivDishFav.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_fav_dish
                    )
                )
                Toast.makeText(requireActivity(), "Un-favorite", Toast.LENGTH_SHORT).show()
                }
        }

    }

}