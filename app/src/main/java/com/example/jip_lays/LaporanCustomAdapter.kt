package com.example.jip_lays

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.jip_lays.databinding.ItemLaporanCustomBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class LaporanCustomAdapter(
    private val context: Context,
    private var items: List<KerusakanModel>
) : BaseAdapter() {

    fun updateData(newItems: List<KerusakanModel>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ItemLaporanCustomBinding
        val view: View

        if (convertView == null) {
            binding = ItemLaporanCustomBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemLaporanCustomBinding
            view = convertView
        }

        val item = items[position]

        binding.tvNamaPeralatan.text = item.namaPeralatan
        binding.tvLokasiTanggal.text = "Lokasi: ${item.lokasiDivisi} | ${item.tanggal}"
        binding.tvDeskripsi.text = item.deskripsi
        binding.tvPrioritasBadge.text = item.tingkatKepentingan

        when (item.tingkatKepentingan.lowercase()) {
            "darurat" -> {
                binding.tvPrioritasBadge.setBackgroundColor(Color.parseColor("#FFDADA"))
                binding.tvPrioritasBadge.setTextColor(Color.parseColor("#C00000"))
            }
            "tinggi" -> {
                binding.tvPrioritasBadge.setBackgroundColor(Color.parseColor("#FFF0CC"))
                binding.tvPrioritasBadge.setTextColor(Color.parseColor("#956000"))
            }
            "sedang" -> {
                binding.tvPrioritasBadge.setBackgroundColor(Color.parseColor("#E8EEF6"))
                binding.tvPrioritasBadge.setTextColor(Color.parseColor("#2B5C92"))
            }
            else -> {
                binding.tvPrioritasBadge.setBackgroundColor(Color.parseColor("#E7EAF0"))
                binding.tvPrioritasBadge.setTextColor(Color.parseColor("#505B6B"))
            }
        }

        // MODUL 10 Requirement: Glide Image Loader
        if (!item.photoPath.isNullOrEmpty()) {
            val imgFile = File(item.photoPath)
            if (imgFile.exists()) {
                binding.imgBuktiFoto.visibility = View.VISIBLE
                Glide.with(context)
                    .load(imgFile)
                    .placeholder(R.drawable.ic_laporan)
                    .error(R.drawable.ic_laporan)
                    .into(binding.imgBuktiFoto)
            } else {
                binding.imgBuktiFoto.visibility = View.GONE
            }
        } else {
            binding.imgBuktiFoto.visibility = View.GONE
        }

        binding.btnDetailItem.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle(item.namaPeralatan)
                .setMessage("Divisi: ${item.lokasiDivisi}\nTanggal: ${item.tanggal}\nPrioritas: ${item.tingkatKepentingan}\n\nDeskripsi:\n${item.deskripsi}")
                .setPositiveButton("Tutup", null)
                .show()
        }

        return view
    }
}
