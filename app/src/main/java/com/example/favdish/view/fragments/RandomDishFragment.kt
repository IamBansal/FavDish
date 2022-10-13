package com.example.favdish.view.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.favdish.R
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.FragmentRandomDishBinding
import com.example.favdish.model.entity.FavDish
import com.example.favdish.model.entity.RandomDish
import com.example.favdish.utils.Constants
import com.example.favdish.viewModel.FavDishViewModel
import com.example.favdish.viewModel.FavDishViewModelFactory
import com.example.favdish.viewModel.RandomDishesViewModel

class RandomDishFragment : Fragment() {

    private lateinit var binding: FragmentRandomDishBinding
    private lateinit var randomDishesViewModel: RandomDishesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRandomDishBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        randomDishesViewModel = ViewModelProvider(this)[RandomDishesViewModel::class.java]

        randomDishesViewModel.getRandomRecipeFromAPI()

        randomDishViewModelObserver()

        binding.srlRandomDish.setOnRefreshListener {
            randomDishesViewModel.getRandomRecipeFromAPI()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setRandomDishResponseInUI(recipe: RandomDish.Recipe) {
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(binding.ivDishDetail)

        binding.tvTitleDishDetail.text = recipe.title

        var dishType = "other"

        if (recipe.dishTypes.isNotEmpty()) {
            dishType = recipe.dishTypes[0]
            binding.tvTypeDishDetail.text = dishType
        }

        binding.tvCategoryDishDetail.text = "other"

        var ingredients = ""

        for (value in recipe.extendedIngredients) {
            ingredients = if (ingredients.isEmpty()) {
                value.original
            } else {
                ingredients + ", \n" + value.original
            }
        }

        binding.tvIngredientsDishDetail.text = ingredients

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvDirectionDishDetail.text = Html.fromHtml(
                recipe.instructions, Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            binding.tvDirectionDishDetail.text = Html.fromHtml(recipe.instructions)
        }

        binding.ivDishFav.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_fav_dish
            )
        )

        var addedToFavorites = false

        binding.tvTimeDishDetail.text = recipe.cookingMinutes.toString()

        binding.ivDishFav.setOnClickListener {

            if (addedToFavorites) {
                Toast.makeText(requireActivity(), "Already added", Toast.LENGTH_SHORT).show()
            } else {
                val randomDishDetails = FavDish(
                    recipe.image,
                    Constants.DISH_IMAGE_SOURCE_ONLINE,
                    recipe.title,
                    dishType,
                    "Other",
                    ingredients,
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )

                addedToFavorites = true
                val favDishViewModel: FavDishViewModel by viewModels {
                    FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
                }
                favDishViewModel.insert(
                    randomDishDetails
                )

                binding.ivDishFav.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_fav_dish_selected
                    )
                )
                Toast.makeText(requireActivity(), "Fav", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun randomDishViewModelObserver() {

        randomDishesViewModel.randomDishResponse.observe(
            viewLifecycleOwner
        ) { randomDishResponse ->
            randomDishResponse?.let {
                if (binding.srlRandomDish.isRefreshing){
                    binding.srlRandomDish.isRefreshing = false
                }
                setRandomDishResponseInUI(randomDishResponse.recipes[0])
            }
        }

        randomDishesViewModel.randomDishLoadingError.observe(
            viewLifecycleOwner
        ) { dataError ->
            dataError?.let {
                if (binding.srlRandomDish.isRefreshing){
                    binding.srlRandomDish.isRefreshing = false
                }
            }
        }

        randomDishesViewModel.loadRandomDish.observe(
            viewLifecycleOwner
        ) { loadRandomDish ->
            loadRandomDish?.let {

            }
        }

    }

}