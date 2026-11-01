package com.example.jip_lays.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.jip_lays.R
import com.example.jip_lays.databinding.FragmentKaryawanBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KaryawanFragment : Fragment() {

    private var _binding: FragmentKaryawanBinding? = null
    private val binding get() = _binding!!

    private var jenisAbsensi = "Masuk"
    private var searchQuery = ""
    private var selectedDivisi = "Semua Divisi"

    private val daftarKaryawan = mutableListOf(
        "Siti Rahayu|Juru Masak|Hadir",
        "Andi Saputra|Petugas Persiapan|Hadir",
        "Dewi Lestari|Petugas Pengemasan|Belum Absen"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKaryawanBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check role access
        val role = requireActivity().getSharedPreferences("user_pref", android.content.Context.MODE_PRIVATE)
            .getString("role", "Penanggung Jawab Dapur") ?: "Penanggung Jawab Dapur"

        if (role != "Penanggung Jawab Dapur") {
            Toast.makeText(requireContext(), "Akses terbatas: Menu Karyawan dikelola Penanggung Jawab Dapur", Toast.LENGTH_SHORT).show()
        }

        // Menampilkan tanggal hari ini
        binding.tvTanggal.text = SimpleDateFormat(
            "dd MMMM yyyy",
            Locale.forLanguageTag("id-ID")
        ).format(Date())

        // Kondisi awal tab
        setTabAktif(true)

        // Pilih absensi Masuk
        binding.tabMasuk.setOnClickListener {
            jenisAbsensi = "Masuk"
            setTabAktif(true)
        }

        // Pilih absensi Pulang
        binding.tabPulang.setOnClickListener {
            jenisAbsensi = "Pulang"
            setTabAktif(false)
        }

        // Setup filter divisi dropdown
        val divisiList = arrayOf("Semua Divisi", "Juru Masak", "Petugas Persiapan", "Petugas Pengemasan")
        val divisiAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, divisiList)
        binding.actFilterDivisi.setAdapter(divisiAdapter)
        binding.actFilterDivisi.setText("Semua Divisi", false)
        binding.actFilterDivisi.setOnItemClickListener { _, _, position, _ ->
            selectedDivisi = divisiList[position]
            tampilkanDaftarKaryawan()
        }

        // Search listener
        binding.etSearchKaryawan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().trim().lowercase()
                tampilkanDaftarKaryawan()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Simpan data karyawan dan absensi
        binding.btnSimpan.setOnClickListener {
            val nama = binding.etNamaKaryawan.text
                .toString().trim()

            val jabatan = binding.etJabatan.text
                .toString().trim()

            if (nama.isEmpty() || jabatan.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Nama dan jabatan karyawan harus diisi",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val status = "Absensi $jenisAbsensi"

            daftarKaryawan.add(
                "$nama|$jabatan|$status"
            )

            tampilkanDaftarKaryawan()

            binding.etNamaKaryawan.text?.clear()
            binding.etJabatan.text?.clear()

            Toast.makeText(
                requireContext(),
                "Data karyawan berhasil ditambahkan",
                Toast.LENGTH_SHORT
            ).show()
        }

        tampilkanDaftarKaryawan()
    }

    private fun setTabAktif(isMasuk: Boolean) {
        val warnaAktif = ContextCompat.getColor(
            requireContext(),
            R.color.primary_blue
        )

        val warnaTeks = ContextCompat.getColor(
            requireContext(),
            R.color.text_body
        )

        val warnaPutih = ContextCompat.getColor(
            requireContext(),
            R.color.white
        )

        if (isMasuk) {
            binding.tabMasuk.setBackgroundColor(warnaAktif)
            binding.tabMasuk.setTextColor(warnaPutih)

            binding.tabPulang.setBackgroundColor(Color.TRANSPARENT)
            binding.tabPulang.setTextColor(warnaTeks)
        } else {
            binding.tabPulang.setBackgroundColor(warnaAktif)
            binding.tabPulang.setTextColor(warnaPutih)

            binding.tabMasuk.setBackgroundColor(Color.TRANSPARENT)
            binding.tabMasuk.setTextColor(warnaTeks)
        }
    }

    private fun tampilkanDaftarKaryawan() {
        binding.containerKaryawan.removeAllViews()

        val filteredList = daftarKaryawan.filter { data ->
            val bagian = data.split("|")
            val nama = bagian[0]
            val jabatan = bagian[1]

            val matchesSearch = searchQuery.isEmpty() || nama.lowercase().contains(searchQuery)
            val matchesDivisi = selectedDivisi == "Semua Divisi" || jabatan.lowercase().contains(selectedDivisi.lowercase())

            matchesSearch && matchesDivisi
        }

        binding.tvJumlahKaryawan.text =
            "${filteredList.size} data karyawan ditemukan"

        if (filteredList.isEmpty()) {
            val tvEmpty = TextView(requireContext()).apply {
                text = "Tidak ada data karyawan yang cocok."
                textSize = 13f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_body))
                setPadding(0, 16, 0, 16)
            }
            binding.containerKaryawan.addView(tvEmpty)
            return
        }

        filteredList.forEachIndexed { index, data ->
            val bagian = data.split("|")
            val nama = bagian[0]
            val jabatan = bagian[1]
            val status = bagian[2]

            val kartu = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 14, 16, 14)
                setBackgroundResource(R.drawable.bg_card_karyawan)
            }

            val tvNama = TextView(requireContext()).apply {
                text = "${index + 1}. $nama"
                textSize = 15f
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_navy
                    )
                )
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val tvJabatan = TextView(requireContext()).apply {
                text = "Jabatan: $jabatan"
                textSize = 12f
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_body
                    )
                )
            }

            val tvStatus = TextView(requireContext()).apply {
                text = status
                textSize = 12f
                setPadding(0, 6, 0, 0)
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_blue
                    )
                )
            }

            kartu.addView(tvNama)
            kartu.addView(tvJabatan)
            kartu.addView(tvStatus)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }

            binding.containerKaryawan.addView(kartu, params)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
