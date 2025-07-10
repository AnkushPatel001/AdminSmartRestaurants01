package com.example.adminsmartrestaurants.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminsmartrestaurants.R
import com.example.adminsmartrestaurants.databinding.ItemallitemBinding
import com.example.adminsmartrestaurants.model.AllMenu
import com.squareup.picasso.Picasso

class AddItemAdapter(
    private val contextMenu: Context
) : RecyclerView.Adapter<AddItemAdapter.AddItemViewHolder>() {

    private val menulist = ArrayList<AllMenu>()
    private val itemQuantities: MutableList<Int> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = ItemallitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddItemViewHolder(binding)
    }

    override fun getItemCount(): Int = menulist.size

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(menulist[position], position)
    }

    inner class AddItemViewHolder(private val binding: ItemallitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(menuItem: AllMenu, position: Int) {
            binding.apply {
                FoodName.text = menuItem.foodName
                FoodPrice.text = menuItem.foodPrice
                Picasso.get()
                    .load(menuItem.foodImage)
                    .placeholder(R.drawable.img_4)
                    .error(R.drawable.img_4)
                    .into(foodImageView)

                ItemCount.text = itemQuantities[position].toString()

                MinusButton.setOnClickListener {
                    decreaseQuantity(position)
                }

                PlusButton.setOnClickListener {
                    increaseQuantity(position)
                }

                DeleteButton.setOnClickListener {
                    deleteItem(position)
                }
            }
        }
    }

    private fun increaseQuantity(position: Int) {
        if (itemQuantities[position] < 99) {
            itemQuantities[position]++
            notifyItemChanged(position)
        }
    }

    private fun decreaseQuantity(position: Int) {
        if (itemQuantities[position] > 1) {
            itemQuantities[position]--
            notifyItemChanged(position)
        }
    }

    private fun deleteItem(position: Int) {
        menulist.removeAt(position)
        itemQuantities.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, menulist.size)
    }

    fun updateData(newMenuList: List<AllMenu>) {
        menulist.clear()
        menulist.addAll(newMenuList)
        itemQuantities.clear()
        itemQuantities.addAll(List(menulist.size) { 1 })
        notifyDataSetChanged()
    }
}
