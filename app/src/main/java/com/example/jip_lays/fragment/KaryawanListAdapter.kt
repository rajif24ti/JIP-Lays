package com.example.jip_lays.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.jip_lays.data.model.KaryawanModel
import com.example.jip_lays.databinding.ItemKaryawanSimpleBinding

class KaryawanListAdapter(
    private val context: Context,
    private var items: List<KaryawanModel>,
    private val onEditClick: (KaryawanModel) -> Unit,
    private val onDeleteClick: (KaryawanModel) -> Unit
) : BaseAdapter() {

    fun updateData(newItems: List<KaryawanModel>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = items[position].id ?: position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ItemKaryawanSimpleBinding
        val view: View

        if (convertView == null) {
            binding = ItemKaryawanSimpleBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemKaryawanSimpleBinding
            view = convertView
        }

        val item = items[position]

        binding.tvNamaKaryawanSimple.text = item.nama
        binding.tvDivisiKaryawanSimple.text = "Divisi: ${item.divisi}"

        val attendance = KaryawanRepository.absensiHariIni(context, item.nama)
        val statusText = when {
            attendance.jamPulang != null -> "Sudah Pulang"
            attendance.jamMasuk != null -> "Hadir"
            else -> "Belum Absen"
        }
        binding.tvStatusKaryawanSimple.text = statusText
        binding.tvJamKaryawanSimple.text = "Masuk: ${attendance.jamMasuk ?: "—"}  •  Pulang: ${attendance.jamPulang ?: "—"}"

        binding.btnEditKaryawan.setOnClickListener {
            onEditClick(item)
        }

        binding.btnHapusKaryawan.setOnClickListener {
            onDeleteClick(item)
        }

        return view
    }
}
