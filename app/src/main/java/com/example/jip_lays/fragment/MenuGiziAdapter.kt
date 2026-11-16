package com.example.jip_lays.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jip_lays.databinding.ItemMenuGiziBinding

data class MenuGizi(
    val nama: String,
    val gambar: Int,
    val kalori: String,
    val protein: String,
    val lemak: String,
    val karbohidrat: String,
    val serat: String
)

class MenuGiziAdapter(
    private val daftarMenu: List<MenuGizi>
) : RecyclerView.Adapter<MenuGiziAdapter.MenuGiziViewHolder>() {

    inner class MenuGiziViewHolder(
        private val binding: ItemMenuGiziBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: MenuGizi) {
            binding.ivMenuGizi.setImageResource(menu.gambar)
            binding.tvNamaMenuGizi.text = menu.nama
            binding.tvKalori.text = menu.kalori
            binding.tvProtein.text = menu.protein
            binding.tvLemak.text = menu.lemak
            binding.tvKarbohidrat.text = menu.karbohidrat
            binding.tvSerat.text = menu.serat
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuGiziViewHolder {
        val binding = ItemMenuGiziBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuGiziViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuGiziViewHolder, position: Int) {
        holder.bind(daftarMenu[position])
    }

    override fun getItemCount(): Int = daftarMenu.size
}
