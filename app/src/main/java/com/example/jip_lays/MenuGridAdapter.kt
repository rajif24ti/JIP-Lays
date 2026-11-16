package com.example.jip_lays

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jip_lays.databinding.ItemMenuGridBinding

class MenuGridAdapter(
    private val items: List<MenuGridModel>,
    private val onItemClick: (MenuGridModel) -> Unit
) : RecyclerView.Adapter<MenuGridAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMenuGridBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvMenuTitle.text = item.title
        holder.binding.tvMenuDesc.text = item.description
        holder.binding.imgMenuIcon.setImageResource(item.iconRes)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
