package com.example.jip_lays.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.jip_lays.R
import com.example.jip_lays.databinding.FragmentProduksiBinding
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProduksiFragment : Fragment() {

    private var _binding: FragmentProduksiBinding? = null
    private val binding get() = _binding!!

    private var searchQuery = ""
    private var selectedStatus = "Semua Status"

    private data class ProduksiData(
        val sekolah: String,
        val menu: String,
        val porsi: String,
        val status: String
    )

    private val daftarProduksi = listOf(
        ProduksiData("SD Negeri 001", "Nasi, ayam, sayur, dan buah", "400 porsi", "Selesai"),
        ProduksiData("SMP Negeri 001", "Nasi, ikan, sayur, dan buah", "350 porsi", "Diproses"),
        ProduksiData("SD Negeri 002", "Nasi, telur, sayur, dan buah", "300 porsi", "Menunggu"),
        ProduksiData("SD Negeri 003", "Nasi, daging, sayur, dan buah", "450 porsi", "Selesai"),
        ProduksiData("SMP Negeri 002", "Nasi, tempe, sayur, dan buah", "500 porsi", "Diproses")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProduksiBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Menampilkan tanggal hari ini
        val tanggal = SimpleDateFormat(
            "dd MMMM yyyy",
            Locale.forLanguageTag("id-ID")
        ).format(Date())

        binding.tvTanggalProduksi.text = tanggal

        // Data ringkasan produksi
        binding.tvTotalPorsi.text = "2.000"
        binding.tvTotalMenu.text = "3"
        binding.tvTotalSekolah.text = "5"

        // Setup filter status dropdown
        val statusList = arrayOf("Semua Status", "Selesai", "Diproses", "Menunggu")
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statusList)
        binding.actFilterStatusProduksi.setAdapter(statusAdapter)
        binding.actFilterStatusProduksi.setText("Semua Status", false)
        binding.actFilterStatusProduksi.setOnItemClickListener { _, _, position, _ ->
            selectedStatus = statusList[position]
            tampilkanDaftarProduksi()
        }

        // Search listener
        binding.etSearchProduksi.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().trim().lowercase()
                tampilkanDaftarProduksi()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        tampilkanDaftarProduksi()
    }

    private fun tampilkanDaftarProduksi() {
        binding.containerProduksi.removeAllViews()

        val filteredList = daftarProduksi.filter { item ->
            val matchesSearch = searchQuery.isEmpty() || item.sekolah.lowercase().contains(searchQuery) || item.menu.lowercase().contains(searchQuery)
            val matchesStatus = selectedStatus == "Semua Status" || item.status.equals(selectedStatus, ignoreCase = true)
            matchesSearch && matchesStatus
        }

        if (filteredList.isEmpty()) {
            val tvEmpty = TextView(requireContext()).apply {
                text = "Tidak ada data produksi yang cocok."
                textSize = 13f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_body))
                setPadding(0, 16, 0, 16)
            }
            binding.containerProduksi.addView(tvEmpty)
            return
        }

        filteredList.forEach { item ->
            val card = MaterialCardView(requireContext()).apply {
                radius = 14f * resources.displayMetrics.density
                cardElevation = 2f * resources.displayMetrics.density
                setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                strokeWidth = 1
                strokeColor = android.graphics.Color.parseColor("#DFE6EF")
            }

            val layout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
            }

            val tvSekolah = TextView(requireContext()).apply {
                text = item.sekolah
                textSize = 16f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_navy))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val tvMenu = TextView(requireContext()).apply {
                text = item.menu
                textSize = 14f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_body))
                setPadding(0, 8, 0, 0)
            }

            val tvPorsi = TextView(requireContext()).apply {
                text = item.porsi
                textSize = 14f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 8, 0, 0)
            }

            val tvStatus = TextView(requireContext()).apply {
                text = item.status
                textSize = 12f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(24, 12, 24, 12)
                when (item.status) {
                    "Selesai" -> {
                        setBackgroundColor(android.graphics.Color.parseColor("#DDF5E5"))
                        setTextColor(android.graphics.Color.parseColor("#207544"))
                    }
                    "Diproses" -> {
                        setBackgroundColor(android.graphics.Color.parseColor("#FFF0CC"))
                        setTextColor(android.graphics.Color.parseColor("#956000"))
                    }
                    else -> {
                        setBackgroundColor(android.graphics.Color.parseColor("#E7EAF0"))
                        setTextColor(android.graphics.Color.parseColor("#505B6B"))
                    }
                }
            }

            layout.addView(tvSekolah)
            layout.addView(tvMenu)
            layout.addView(tvPorsi)

            val statusParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 12
            }
            layout.addView(tvStatus, statusParams)

            card.addView(layout)

            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
            binding.containerProduksi.addView(card, cardParams)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
