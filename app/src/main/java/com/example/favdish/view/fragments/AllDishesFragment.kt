package com.example.favdish.view.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.favdish.R
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.DialogCustomListBinding
import com.example.favdish.databinding.FragmentAllDishesBinding
import com.example.favdish.model.entity.FavDish
import com.example.favdish.utils.Constants
import com.example.favdish.view.activity.AddUpdateDish
import com.example.favdish.view.activity.MainActivity
import com.example.favdish.view.adapters.FavDishAdapter
import com.example.favdish.view.adapters.ListItemAdapter
import com.example.favdish.viewModel.FavDishViewModel
import com.example.favdish.viewModel.FavDishViewModelFactory

class AllDishesFragment : Fragment() {

    private lateinit var binding: FragmentAllDishesBinding
    private lateinit var favDishAdapter: FavDishAdapter
    private lateinit var customListDialog: Dialog

    private val favDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllDishesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        favDishAdapter = FavDishAdapter(this@AllDishesFragment)

        binding.rvDishesList.adapter = favDishAdapter

        favDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
            dishes.let {
                if (it.isNotEmpty()) {
                    binding.rvDishesList.visibility = View.VISIBLE
                    binding.tvNoDishesAdded.visibility = View.GONE

                    favDishAdapter.dishesList(it)
                } else {
                    binding.rvDishesList.visibility = View.GONE
                    binding.tvNoDishesAdded.visibility = View.VISIBLE
                }
            }

        }

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.toAddUpdateDish -> {
                        startActivity(Intent(requireActivity(), AddUpdateDish::class.java))
                        true
                    }
                    R.id.filterDishes -> {
                        filterDishesListDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    override fun onResume() {
        super.onResume()
        //To show bottom navigation view when all dishes is open
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavView()
        }
    }

    fun deleteDish(dish: FavDish) {
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog.setTitle("Delete Dish????")
            .setMessage("You sure you want to delete the ${dish.title}.\nYou won't be able to restore that.")
            .setIcon(R.drawable.ic_baseline_delete_forever_24)
            .setPositiveButton("Yes, Delete") { dialog, _ ->
                favDishViewModel.delete(dish)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    fun dishDetails(favDish: FavDish) {
        findNavController().navigate(
            AllDishesFragmentDirections.actionNavigationAllDishesToNavigationDishDetail(
                favDish
            )
        )

        //To hide bottom navigation view when dish details is open
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavView()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun filterDishesListDialog() {
        customListDialog = Dialog(requireActivity())
        val binding = DialogCustomListBinding.inflate(layoutInflater)

        customListDialog.setContentView(binding.root)
        binding.tvTitle.text = "Select Item to Filter"

        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)

        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvList.adapter = ListItemAdapter(
            requireActivity(),
            this@AllDishesFragment,
            dishTypes,
            Constants.FILTER_SELECTION
        )
        customListDialog.show()

    }

    fun filterSelection(filterItemSelection: String) {
        customListDialog.dismiss()

        if (filterItemSelection == Constants.ALL_ITEMS) {

            (activity as MainActivity?)!!.supportActionBar!!.title = "All Dishes"

            favDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
                dishes.let {
                    if (it.isNotEmpty()) {
                        binding.rvDishesList.visibility = View.VISIBLE
                        binding.tvNoDishesAdded.visibility = View.GONE

                        favDishAdapter.dishesList(it)
                    } else {
                        binding.rvDishesList.visibility = View.GONE
                        binding.tvNoDishesAdded.visibility = View.VISIBLE
                    }
                }
            }
        } else {

            (activity as MainActivity?)!!.supportActionBar!!.title =
                "All Dishes in $filterItemSelection"

            favDishViewModel.getFilteredList(filterItemSelection)
                .observe(viewLifecycleOwner) { dishes ->
                    dishes.let {
                        if (it.isNotEmpty()) {
                            binding.rvDishesList.visibility = View.VISIBLE
                            binding.tvNoDishesAdded.visibility = View.GONE

                            favDishAdapter.dishesList(it)
                        } else {
                            binding.rvDishesList.visibility = View.GONE
                            binding.tvNoDishesAdded.visibility = View.VISIBLE
                        }
                    }
                }
        }

    }

}