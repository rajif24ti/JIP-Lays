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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.jip_lays.data.model.KaryawanModel
import com.example.jip_lays.data.repository.KaryawanRemoteRepository
import com.example.jip_lays.databinding.DialogKaryawanFormBinding
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
    private val statusKaryawan = listOf("Aktif", "Tidak Aktif")

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
        val formBinding = DialogKaryawanFormBinding.inflate(layoutInflater)
        val etNama = formBinding.etNamaKaryawanDialog
        val actDivisi = formBinding.actDivisiKaryawanDialog
        val etJabatan = formBinding.etJabatanKaryawanDialog
        val etTelepon = formBinding.etTeleponKaryawanDialog
        val etAlamat = formBinding.etAlamatKaryawanDialog
        val actStatus = formBinding.actStatusKeaktifanDialog

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, daftarDivisi)
        actDivisi.setAdapter(adapter)
        actDivisi.setOnClickListener { actDivisi.showDropDown() }
        actStatus.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statusKaryawan))
        actStatus.setText("Aktif", false)
        actStatus.setOnClickListener { actStatus.showDropDown() }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tambah Karyawan Baru")
            .setView(formBinding.root)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val nama = etNama.text.toString().trim()
                val divisi = actDivisi.text.toString().trim()
                val jabatan = etJabatan.text.toString().trim()
                val telepon = etTelepon.text.toString().trim()
                val alamat = etAlamat.text.toString().trim()
                val status = actStatus.text.toString().trim()

                if (nama.isEmpty() || divisi !in daftarDivisi || jabatan.isEmpty() ||
                    telepon.isEmpty() || alamat.isEmpty() || status !in statusKaryawan
                ) {
                    Toast.makeText(requireContext(), "Mohon lengkapi seluruh data", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                dialog.dismiss()
                tambahKaryawanSupabase(
                    KaryawanModel(
                        nama = nama,
                        divisi = divisi,
                        jabatan = jabatan,
                        telepon = telepon,
                        alamat = alamat,
                        statusKeaktifan = status
                    )
                )
            }
        }
        dialog.show()
    }

    private fun tambahKaryawanSupabase(karyawan: KaryawanModel) {
        val currentBinding = _binding ?: return
        currentBinding.progressBarKaryawan.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = repository.insertKaryawan(getToken(), karyawan)
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
        val formBinding = DialogKaryawanFormBinding.inflate(layoutInflater)
        val etNama = formBinding.etNamaKaryawanDialog
        val actDivisi = formBinding.actDivisiKaryawanDialog
        val etJabatan = formBinding.etJabatanKaryawanDialog
        val etTelepon = formBinding.etTeleponKaryawanDialog
        val etAlamat = formBinding.etAlamatKaryawanDialog
        val actStatus = formBinding.actStatusKeaktifanDialog

        etNama.setText(karyawan.nama)
        etJabatan.setText(karyawan.jabatan.orEmpty())
        etTelepon.setText(karyawan.telepon.orEmpty())
        etAlamat.setText(karyawan.alamat.orEmpty())
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, daftarDivisi)
        actDivisi.setAdapter(adapter)
        actDivisi.setText(karyawan.divisi, false)
        actDivisi.setOnClickListener { actDivisi.showDropDown() }
        actStatus.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statusKaryawan))
        actStatus.setText(karyawan.statusKeaktifan ?: "Aktif", false)
        actStatus.setOnClickListener { actStatus.showDropDown() }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Data Karyawan")
            .setView(formBinding.root)
            .setPositiveButton("Simpan Perubahan", null)
            .setNegativeButton("Batal", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val nama = etNama.text.toString().trim()
                val divisi = actDivisi.text.toString().trim()
                val jabatan = etJabatan.text.toString().trim()
                val telepon = etTelepon.text.toString().trim()
                val alamat = etAlamat.text.toString().trim()
                val status = actStatus.text.toString().trim()

                if (nama.isEmpty() || divisi !in daftarDivisi || jabatan.isEmpty() ||
                    telepon.isEmpty() || alamat.isEmpty() || status !in statusKaryawan ||
                    karyawan.id == null
                ) {
                    Toast.makeText(requireContext(), "Mohon lengkapi seluruh data", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                dialog.dismiss()
                editKaryawanSupabase(
                    karyawan.id,
                    KaryawanModel(
                        id = karyawan.id,
                        nama = nama,
                        divisi = divisi,
                        jabatan = jabatan,
                        telepon = telepon,
                        alamat = alamat,
                        statusKeaktifan = status
                    )
                )
            }
        }
        dialog.show()
    }

    private fun editKaryawanSupabase(id: Long, karyawan: KaryawanModel) {
        val currentBinding = _binding ?: return
        currentBinding.progressBarKaryawan.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = repository.updateKaryawan(getToken(), id, karyawan)
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
