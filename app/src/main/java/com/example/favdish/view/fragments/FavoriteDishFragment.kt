package com.example.favdish.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.FragmentFavoriteDishBinding
import com.example.favdish.model.entity.FavDish
import com.example.favdish.view.activity.MainActivity
import com.example.favdish.view.adapters.FavDishAdapter
import com.example.favdish.viewModel.FavDishViewModel
import com.example.favdish.viewModel.FavDishViewModelFactory

class FavoriteDishFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteDishBinding

    private val favDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFavoriteDishBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFavDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        val favDishAdapter = FavDishAdapter(this@FavoriteDishFragment)

        binding.rvFavDishesList.adapter = favDishAdapter

        favDishViewModel.favDishesList.observe(viewLifecycleOwner) { dishes ->
            dishes.let {
                if (it.isNotEmpty()) {
                    binding.rvFavDishesList.visibility = View.VISIBLE
                    binding.tvNoDishesAdded.visibility = View.GONE

                    favDishAdapter.dishesList(it)
                } else {
                    binding.rvFavDishesList.visibility = View.GONE
                    binding.tvNoDishesAdded.visibility = View.VISIBLE
                }
            }

        }

    }

    override fun onResume() {
        super.onResume()
        //To show bottom navigation view when all dishes is open
        if (requireActivity() is MainActivity){
            (activity as MainActivity?)!!.showBottomNavView()
        }
    }

    fun dishDetails(favDish: FavDish){
        findNavController().navigate(FavoriteDishFragmentDirections.actionNavigationFavoriteDishesToNavigationDishDetail(favDish))

        //To hide bottom navigation view when dish details is open
        if (requireActivity() is MainActivity){
            (activity as MainActivity?)!!.hideBottomNavView()
        }

    }

}