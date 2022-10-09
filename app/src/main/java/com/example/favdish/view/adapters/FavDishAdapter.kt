package com.example.favdish.view.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.favdish.R
import com.example.favdish.databinding.ItemDishLayoutBinding
import com.example.favdish.model.entity.FavDish
import com.example.favdish.utils.Constants
import com.example.favdish.view.activity.AddUpdateDish
import com.example.favdish.view.fragments.AllDishesFragment
import com.example.favdish.view.fragments.FavoriteDishFragment

class FavDishAdapter(private val fragment: Fragment): RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes: List<FavDish> = listOf()

    class ViewHolder(view: ItemDishLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val ivDishImage = view.ivDish
        val tvTitle = view.tvTitleDish
        val ivMore = view.ivMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDishLayoutBinding.inflate(LayoutInflater.from(fragment.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)
        holder.tvTitle.text = dish.title

        holder.itemView.setOnClickListener {
            if (fragment is AllDishesFragment){
                fragment.dishDetails(dish)
            } else if (fragment is FavoriteDishFragment) {
                fragment.dishDetails(dish)
            }
        }

        holder.ivMore.setOnClickListener {
            val popupMenu = PopupMenu(fragment.context, holder.ivMore)
            popupMenu.menuInflater.inflate(R.menu.dish_options_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.editDish -> {
                        val intent = Intent(fragment.requireActivity(), AddUpdateDish::class.java)
                        intent.putExtra(Constants.EXTRA_DISH_DETAILS, dish)
                        fragment.requireActivity().startActivity(intent)

                        return@setOnMenuItemClickListener true
                    }
                    R.id.deleteDish -> {
                        if (fragment is AllDishesFragment){
                            fragment.deleteDish(dish)
                        }
                        return@setOnMenuItemClickListener true
                    }
                    else -> {
                        return@setOnMenuItemClickListener true
                    }
                }
            }
            popupMenu.show()
        }

        if (fragment is AllDishesFragment){
            holder.ivMore.visibility = View.VISIBLE
        } else {
            holder.ivMore.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun dishesList(list : List<FavDish>){
        dishes = list
        notifyDataSetChanged()
    }

}