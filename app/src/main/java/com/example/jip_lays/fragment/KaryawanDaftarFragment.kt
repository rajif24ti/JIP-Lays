package com.example.jip_lays.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import androidx.fragment.app.Fragment
import com.example.jip_lays.R
import com.example.jip_lays.databinding.FragmentKaryawanDaftarBinding
import java.util.ArrayList
import java.util.HashMap

class KaryawanDaftarFragment : Fragment() {

    private var _binding: FragmentKaryawanDaftarBinding? = null
    private val binding get() = _binding!!
    private var searchQuery = ""
    private var selectedDivisi = "Semua Divisi"
    private var simpleAdapter: SimpleAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKaryawanDaftarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pilihanDivisi = listOf("Semua Divisi") + KaryawanRepository.daftarDivisi
        binding.actFilterDivisi.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, pilihanDivisi)
        )
        binding.actFilterDivisi.setText("Semua Divisi", false)
        binding.actFilterDivisi.setOnClickListener { binding.actFilterDivisi.showDropDown() }
        binding.actFilterDivisi.setOnItemClickListener { _, _, position, _ ->
            selectedDivisi = pilihanDivisi[position]
            tampilkanDaftarKaryawan()
        }

        binding.etSearchKaryawan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().trim().lowercase()
                tampilkanDaftarKaryawan()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
        binding.listKaryawan.emptyView = binding.tvKaryawanKosong
        tampilkanDaftarKaryawan()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) tampilkanDaftarKaryawan()
    }

    private fun tampilkanDaftarKaryawan() {
        val currentBinding = _binding ?: return
        val filteredList = KaryawanRepository.daftarKaryawan.filter { karyawan ->
            (searchQuery.isEmpty() || karyawan.nama.lowercase().contains(searchQuery)) &&
                (selectedDivisi == "Semua Divisi" || karyawan.divisi == selectedDivisi)
        }

        currentBinding.tvJumlahKaryawan.text = "${filteredList.size} karyawan ditemukan"
        val dataKaryawan = ArrayList<HashMap<String, String>>(filteredList.size)
        filteredList.forEach { karyawan ->
            val attendance = KaryawanRepository.absensiHariIni(requireContext(), karyawan.nama)
            val status = when {
                attendance.jamPulang != null -> "Sudah Pulang"
                attendance.jamMasuk != null -> "Hadir"
                else -> "Belum Absen"
            }
            dataKaryawan.add(hashMapOf(
                KEY_NAMA to karyawan.nama,
                KEY_DIVISI to karyawan.divisi,
                KEY_STATUS to status,
                KEY_JAM to "Masuk: ${attendance.jamMasuk ?: "—"}  •  Pulang: ${attendance.jamPulang ?: "—"}"
            ))
        }

        simpleAdapter = SimpleAdapter(
            requireContext(),
            dataKaryawan,
            R.layout.item_karyawan_simple,
            arrayOf(KEY_NAMA, KEY_DIVISI, KEY_STATUS, KEY_JAM),
            intArrayOf(
                R.id.tvNamaKaryawanSimple,
                R.id.tvDivisiKaryawanSimple,
                R.id.tvStatusKaryawanSimple,
                R.id.tvJamKaryawanSimple
            )
        )
        currentBinding.listKaryawan.adapter = simpleAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        simpleAdapter = null
        _binding = null
    }

    private companion object {
        const val KEY_NAMA = "nama"
        const val KEY_DIVISI = "divisi"
        const val KEY_STATUS = "status"
        const val KEY_JAM = "jam"
    }
}
