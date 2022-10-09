package com.example.favdish.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.favdish.databinding.ItemCustomListBinding
import com.example.favdish.view.activity.AddUpdateDish
import com.example.favdish.view.fragments.AllDishesFragment

class ListItemAdapter(
    private val activity: Activity,
    private val fragment: Fragment?,
    private val listItems: ArrayList<String>,
    private val selection: String
) : RecyclerView.Adapter<ListItemAdapter.ViewHolder>() {

    class ViewHolder(view: ItemCustomListBinding) : RecyclerView.ViewHolder(view.root) {
        val tvText = view.tvText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCustomListBinding.inflate(
                LayoutInflater.from(activity),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvText.text = item

        holder.itemView.setOnClickListener {
            if (activity is AddUpdateDish){
                activity.selectedListItem(item, selection)
            }
            if (fragment is AllDishesFragment){
                fragment.filterSelection(item)
            }
        }

    }

    override fun getItemCount(): Int {
        return listItems.size
    }

}