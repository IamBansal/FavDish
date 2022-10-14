package com.example.favdish.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.favdish.R
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.FragmentDishDetailsBinding
import com.example.favdish.model.entity.FavDish
import com.example.favdish.utils.Constants
import com.example.favdish.viewModel.FavDishViewModel
import com.example.favdish.viewModel.FavDishViewModelFactory
import java.io.IOException

@Suppress("DEPRECATION")
class DishDetailsFragment : Fragment() {

    private lateinit var binding: FragmentDishDetailsBinding
    private lateinit var favDishDetails: FavDish

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

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_share, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.shareDish -> {
                        shareDish()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val args: DishDetailsFragmentArgs by navArgs()
        favDishDetails = args.dishDetails
        args.let {
            try {
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
//                    .listener(object : RequestListener<Drawable> {
//                        override fun onLoadFailed(
//                            e: GlideException?,
//                            model: Any?,
//                            target: Target<Drawable>?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            return false
//                        }
//
//                        override fun onResourceReady(
//                            resource: Drawable?,
//                            model: Any?,
//                            target: Target<Drawable>?,
//                            dataSource: DataSource?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//
//                            Palette.from(resource!!.toBitmap()).generate { palette ->
//                                palette?.let {
//                                    val intColor = palette.vibrantSwatch?.rgb ?: 0
//                                    binding.clDishDetail.setBackgroundColor(intColor)
//                                }
//                            }
//
//                            return false
//                        }
//
//                    })
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

    private fun shareDish() {

        val type = "text/plain"
        val subject = "Checkout this dish"
        var extraText: String
        val shareWith = "Share with"

        favDishDetails.let {
            var image = ""
            if (it.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE) {
                image = it.image
            }

            val cookingInstructions: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(
                    it.directionToCook, Html.FROM_HTML_MODE_COMPACT
                ).toString()
            } else {
                Html.fromHtml(it.directionToCook).toString()
            }

            extraText =
                "$image \n" +
                        "\nTitle: ${it.title} \n" +
                        "\nType: ${it.type} \n" +
                        "\nCategory: ${it.category} \n" +
                        "\nIngredients: \n${it.ingredients} \n" +
                        "\nInstructions to cook: \n$cookingInstructions \n" +
                        "\nTime required to cook the dish approx ${it.cookingTime} minutes."

        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = type
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, extraText)
        startActivity(Intent.createChooser(intent, shareWith))

    }

}