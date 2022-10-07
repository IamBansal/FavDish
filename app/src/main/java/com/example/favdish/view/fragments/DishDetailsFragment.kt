package com.example.favdish.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.favdish.databinding.FragmentDishDetailsBinding
import java.io.IOException

class DishDetailsFragment : Fragment() {

    private lateinit var binding: FragmentDishDetailsBinding

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
                    .into(binding.ivDishDetail)
                binding.tvTitleDishDetail.text = it.dishDetails.title
                binding.tvTypeDishDetail.text = it.dishDetails.type
                binding.tvCategoryDishDetail.text = it.dishDetails.category
                binding.tvIngredientsDishDetail.text = it.dishDetails.ingredients
                binding.tvDirectionDishDetail.text = it.dishDetails.directionToCook
                binding.tvTimeDishDetail.text =
                    "This dish will take an estimated time of ${it.dishDetails.cookingTime} minutes."

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

}