package com.example.favdish.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.favdish.databinding.ItemDishLayoutBinding
import com.example.favdish.model.entity.FavDish
import com.example.favdish.view.fragments.AllDishesFragment

class FavDishAdapter(private val fragment: Fragment): RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes: List<FavDish> = listOf()

    class ViewHolder(view: ItemDishLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val ivDishImage = view.ivDish
        val tvTitle = view.tvTitleDish
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
            }
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