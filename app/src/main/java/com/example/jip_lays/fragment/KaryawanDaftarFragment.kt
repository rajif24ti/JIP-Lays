package com.example.jip_lays.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.jip_lays.R
import com.example.jip_lays.data.model.KaryawanModel
import com.example.jip_lays.data.repository.KaryawanRemoteRepository
import com.example.jip_lays.databinding.FragmentKaryawanDaftarBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class KaryawanDaftarFragment : Fragment() {

    private var _binding: FragmentKaryawanDaftarBinding? = null
    private val binding get() = _binding!!
    private val repository = KaryawanRemoteRepository()

    private var masterKaryawanList = mutableListOf<KaryawanModel>()
    private var searchQuery = ""
    private var selectedDivisi = "Semua Divisi"
    private var listAdapter: KaryawanListAdapter? = null

    private val daftarDivisi = listOf(
        "Divisi Produksi",
        "Divisi Pengemasan & Pemorsian",
        "Divisi Distribusi",
        "Divisi Kebersihan",
        "Divisi Logistik/Persiapan"
    )

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

        val pilihanDivisiFilter = listOf("Semua Divisi") + daftarDivisi
        val filterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, pilihanDivisiFilter)
        binding.actFilterDivisi.setAdapter(filterAdapter)
        binding.actFilterDivisi.setText("Semua Divisi", false)
        binding.actFilterDivisi.setOnClickListener { binding.actFilterDivisi.showDropDown() }
        binding.actFilterDivisi.setOnItemClickListener { _, _, position, _ ->
            selectedDivisi = pilihanDivisiFilter[position]
            filterAndDisplayList()
        }

        binding.etSearchKaryawan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().trim().lowercase()
                filterAndDisplayList()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })

        binding.btnTambahKaryawan.setOnClickListener {
            showTambahKaryawanDialog()
        }

        binding.listKaryawan.emptyView = binding.tvKaryawanKosong

        loadDataFromSupabase()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) loadDataFromSupabase()
    }

    private fun getToken(): String? {
        return requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
            .getString("access_token", null)
    }

    private fun loadDataFromSupabase() {
        val currentBinding = _binding ?: return
        currentBinding.progressBarKaryawan.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = repository.getKaryawan(getToken())
            currentBinding.progressBarKaryawan.visibility = View.GONE

            result.onSuccess { data ->
                masterKaryawanList.clear()
                masterKaryawanList.addAll(data)
                filterAndDisplayList()
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Gagal memuat Supabase: ${exception.localizedMessage ?: exception.message}", Toast.LENGTH_SHORT).show()
                filterAndDisplayList()
            }
        }
    }

    private fun filterAndDisplayList() {
        val currentBinding = _binding ?: return

        val filteredList = masterKaryawanList.filter { karyawan ->
            (searchQuery.isEmpty() || karyawan.nama.lowercase().contains(searchQuery)) &&
                    (selectedDivisi == "Semua Divisi" || karyawan.divisi == selectedDivisi)
        }

        currentBinding.tvJumlahKaryawan.text = "${filteredList.size} karyawan ditemukan"

        if (listAdapter == null) {
            listAdapter = KaryawanListAdapter(
                requireContext(),
                filteredList,
                onEditClick = { item -> showEditKaryawanDialog(item) },
                onDeleteClick = { item -> showHapusKaryawanDialog(item) }
            )
            currentBinding.listKaryawan.adapter = listAdapter
        } else {
            listAdapter?.updateData(filteredList)
        }
    }

    private fun showTambahKaryawanDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_karyawan_form, null)
        val etNama = dialogView.findViewById<EditText>(R.id.etNamaKaryawanDialog)
        val actDivisi = dialogView.findViewById<AutoCompleteTextView>(R.id.actDivisiKaryawanDialog)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, daftarDivisi)
        actDivisi.setAdapter(adapter)
        actDivisi.setOnClickListener { actDivisi.showDropDown() }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tambah Karyawan Baru")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                val nama = etNama.text.toString().trim()
                val divisi = actDivisi.text.toString().trim()

                if (nama.isEmpty() || divisi.isEmpty()) {
                    Toast.makeText(requireContext(), "Mohon lengkapi seluruh data", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                dialog.dismiss()
                tambahKaryawanSupabase(nama, divisi)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun tambahKaryawanSupabase(nama: String, divisi: String) {
        val currentBinding = _binding ?: return
        currentBinding.progressBarKaryawan.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = repository.insertKaryawan(getToken(), KaryawanModel(nama = nama, divisi = divisi))
            currentBinding.progressBarKaryawan.visibility = View.GONE

            result.onSuccess { newKaryawan ->
                Toast.makeText(requireContext(), "Karyawan ${newKaryawan.nama} berhasil ditambah!", Toast.LENGTH_SHORT).show()
                loadDataFromSupabase()
            }.onFailure { e ->
                Toast.makeText(requireContext(), "Gagal menyimpan: ${e.localizedMessage ?: e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditKaryawanDialog(karyawan: KaryawanModel) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_karyawan_form, null)
        val etNama = dialogView.findViewById<EditText>(R.id.etNamaKaryawanDialog)
        val actDivisi = dialogView.findViewById<AutoCompleteTextView>(R.id.actDivisiKaryawanDialog)

        etNama.setText(karyawan.nama)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, daftarDivisi)
        actDivisi.setAdapter(adapter)
        actDivisi.setText(karyawan.divisi, false)
        actDivisi.setOnClickListener { actDivisi.showDropDown() }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Data Karyawan")
            .setView(dialogView)
            .setPositiveButton("Simpan Perubahan") { dialog, _ ->
                val nama = etNama.text.toString().trim()
                val divisi = actDivisi.text.toString().trim()

                if (nama.isEmpty() || divisi.isEmpty() || karyawan.id == null) {
                    Toast.makeText(requireContext(), "Mohon lengkapi seluruh data", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                dialog.dismiss()
                editKaryawanSupabase(karyawan.id, nama, divisi)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun editKaryawanSupabase(id: Long, nama: String, divisi: String) {
        val currentBinding = _binding ?: return
        currentBinding.progressBarKaryawan.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = repository.updateKaryawan(getToken(), id, KaryawanModel(id = id, nama = nama, divisi = divisi))
            currentBinding.progressBarKaryawan.visibility = View.GONE

            result.onSuccess { updated ->
                Toast.makeText(requireContext(), "Data ${updated.nama} berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                loadDataFromSupabase()
            }.onFailure { e ->
                Toast.makeText(requireContext(), "Gagal memperbarui: ${e.localizedMessage ?: e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showHapusKaryawanDialog(karyawan: KaryawanModel) {
        val id = karyawan.id ?: return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Karyawan")
            .setMessage("Apakah Anda yakin ingin menghapus data karyawan ${karyawan.nama}?")
            .setPositiveButton("Hapus") { dialog, _ ->
                dialog.dismiss()
                hapusKaryawanSupabase(id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusKaryawanSupabase(id: Long) {
        val currentBinding = _binding ?: return
        currentBinding.progressBarKaryawan.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = repository.deleteKaryawan(getToken(), id)
            currentBinding.progressBarKaryawan.visibility = View.GONE

            result.onSuccess {
                Toast.makeText(requireContext(), "Data karyawan berhasil dihapus!", Toast.LENGTH_SHORT).show()
                loadDataFromSupabase()
            }.onFailure { e ->
                Toast.makeText(requireContext(), "Gagal menghapus: ${e.localizedMessage ?: e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listAdapter = null
        _binding = null
    }
}
