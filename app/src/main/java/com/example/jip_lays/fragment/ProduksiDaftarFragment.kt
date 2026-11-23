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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.jip_lays.R
import com.example.jip_lays.data.model.ProduksiModel
import com.example.jip_lays.data.repository.ProduksiRemoteRepository
import com.example.jip_lays.databinding.FragmentProduksiDaftarBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ProduksiDaftarFragment : Fragment() {

    private var _binding: FragmentProduksiDaftarBinding? = null
    private val binding get() = _binding!!
    private val repository = ProduksiRemoteRepository()

    private var masterProduksiList = mutableListOf<ProduksiModel>()
    private var searchQuery = ""
    private var selectedStatus = "Semua Status"

    private val statusOptions = listOf("Selesai", "Diproses", "Menunggu")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProduksiDaftarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val statusListFilter = listOf("Semua Status") + statusOptions
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statusListFilter)
        binding.actFilterStatusProduksi.setAdapter(statusAdapter)
        binding.actFilterStatusProduksi.setText("Semua Status", false)
        binding.actFilterStatusProduksi.setOnClickListener { binding.actFilterStatusProduksi.showDropDown() }
        binding.actFilterStatusProduksi.setOnItemClickListener { _, _, position, _ ->
            selectedStatus = statusListFilter[position]
            filterAndDisplayList()
        }

        binding.etSearchProduksi.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().trim().lowercase()
                filterAndDisplayList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnTambahProduksi.setOnClickListener {
            showTambahProduksiDialog()
        }

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
        currentBinding.progressBarProduksi.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = repository.getProduksi(getToken())
            currentBinding.progressBarProduksi.visibility = View.GONE

            result.onSuccess { data ->
                masterProduksiList.clear()
                masterProduksiList.addAll(data)
                filterAndDisplayList()
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Gagal memuat Supabase: ${exception.localizedMessage ?: exception.message}", Toast.LENGTH_SHORT).show()
                filterAndDisplayList()
            }
        }
    }

    private fun filterAndDisplayList() {
        val currentBinding = _binding ?: return
        currentBinding.containerProduksi.removeAllViews()

        val filteredList = masterProduksiList.filter { item ->
            val matchesSearch = searchQuery.isEmpty() ||
                    item.sekolah.lowercase().contains(searchQuery) ||
                    item.menu.lowercase().contains(searchQuery)
            val matchesStatus = selectedStatus == "Semua Status" ||
                    item.status.equals(selectedStatus, ignoreCase = true)
            matchesSearch && matchesStatus
        }

        if (filteredList.isEmpty()) {
            val tvEmpty = TextView(requireContext()).apply {
                text = "Tidak ada data produksi yang cocok."
                textSize = 13f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_body))
                setPadding(0, 16, 0, 16)
            }
            currentBinding.containerProduksi.addView(tvEmpty)
            return
        }

        filteredList.forEach { item ->
            val card = MaterialCardView(requireContext()).apply {
                radius = 14f * resources.displayMetrics.density
                cardElevation = 2f * resources.displayMetrics.density
                setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                strokeWidth = 1
                strokeColor = ContextCompat.getColor(requireContext(), R.color.border)
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
                setPadding(0, 6, 0, 0)
            }

            val tvPorsi = TextView(requireContext()).apply {
                text = item.porsi
                textSize = 14f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 6, 0, 0)
            }

            val tvStatus = TextView(requireContext()).apply {
                text = item.status
                textSize = 12f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(20, 10, 20, 10)
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

            // Button actions
            val buttonContainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.END
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 12
                }
                layoutParams = params
            }

            val btnEdit = MaterialButton(requireContext()).apply {
                text = "Edit"
                textSize = 11f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                setOnClickListener { showEditProduksiDialog(item) }
            }

            val btnHapus = MaterialButton(requireContext()).apply {
                text = "Hapus"
                textSize = 11f
                setTextColor(android.graphics.Color.parseColor("#D32F2F"))
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                setOnClickListener { showHapusProduksiDialog(item) }
            }

            buttonContainer.addView(btnEdit)
            buttonContainer.addView(btnHapus)

            layout.addView(tvSekolah)
            layout.addView(tvMenu)
            layout.addView(tvPorsi)

            val statusParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 10
            }
            layout.addView(tvStatus, statusParams)
            layout.addView(buttonContainer)

            card.addView(layout)

            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
            currentBinding.containerProduksi.addView(card, cardParams)
        }
    }

    private fun showTambahProduksiDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_produksi_form, null)
        val etSekolah = dialogView.findViewById<EditText>(R.id.etSekolahDialog)
        val etMenu = dialogView.findViewById<EditText>(R.id.etMenuDialog)
        val etPorsi = dialogView.findViewById<EditText>(R.id.etPorsiDialog)
        val actStatus = dialogView.findViewById<AutoCompleteTextView>(R.id.actStatusProduksiDialog)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statusOptions)
        actStatus.setAdapter(adapter)
        actStatus.setText("Diproses", false)
        actStatus.setOnClickListener { actStatus.showDropDown() }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tambah Data Produksi")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                val sekolah = etSekolah.text.toString().trim()
                val menu = etMenu.text.toString().trim()
                val porsi = etPorsi.text.toString().trim()
                val status = actStatus.text.toString().trim()

                if (sekolah.isEmpty() || menu.isEmpty() || porsi.isEmpty() || status.isEmpty()) {
                    Toast.makeText(requireContext(), "Mohon lengkapi seluruh data", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                dialog.dismiss()
                tambahProduksiSupabase(sekolah, menu, porsi, status)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun tambahProduksiSupabase(sekolah: String, menu: String, porsi: String, status: String) {
        val currentBinding = _binding ?: return
        currentBinding.progressBarProduksi.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = repository.insertProduksi(
                getToken(),
                ProduksiModel(sekolah = sekolah, menu = menu, porsi = porsi, status = status)
            )
            currentBinding.progressBarProduksi.visibility = View.GONE

            result.onSuccess { newProduksi ->
                Toast.makeText(requireContext(), "Produksi ${newProduksi.sekolah} berhasil ditambah!", Toast.LENGTH_SHORT).show()
                loadDataFromSupabase()
            }.onFailure { e ->
                Toast.makeText(requireContext(), "Gagal menyimpan: ${e.localizedMessage ?: e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditProduksiDialog(produksi: ProduksiModel) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_produksi_form, null)
        val etSekolah = dialogView.findViewById<EditText>(R.id.etSekolahDialog)
        val etMenu = dialogView.findViewById<EditText>(R.id.etMenuDialog)
        val etPorsi = dialogView.findViewById<EditText>(R.id.etPorsiDialog)
        val actStatus = dialogView.findViewById<AutoCompleteTextView>(R.id.actStatusProduksiDialog)

        etSekolah.setText(produksi.sekolah)
        etMenu.setText(produksi.menu)
        etPorsi.setText(produksi.porsi)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statusOptions)
        actStatus.setAdapter(adapter)
        actStatus.setText(produksi.status, false)
        actStatus.setOnClickListener { actStatus.showDropDown() }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Data Produksi")
            .setView(dialogView)
            .setPositiveButton("Simpan Perubahan") { dialog, _ ->
                val sekolah = etSekolah.text.toString().trim()
                val menu = etMenu.text.toString().trim()
                val porsi = etPorsi.text.toString().trim()
                val status = actStatus.text.toString().trim()

                if (sekolah.isEmpty() || menu.isEmpty() || porsi.isEmpty() || status.isEmpty() || produksi.id == null) {
                    Toast.makeText(requireContext(), "Mohon lengkapi seluruh data", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                dialog.dismiss()
                editProduksiSupabase(produksi.id, sekolah, menu, porsi, status)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun editProduksiSupabase(id: Long, sekolah: String, menu: String, porsi: String, status: String) {
        val currentBinding = _binding ?: return
        currentBinding.progressBarProduksi.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = repository.updateProduksi(
                getToken(),
                id,
                ProduksiModel(id = id, sekolah = sekolah, menu = menu, porsi = porsi, status = status)
            )
            currentBinding.progressBarProduksi.visibility = View.GONE

            result.onSuccess { updated ->
                Toast.makeText(requireContext(), "Data ${updated.sekolah} berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                loadDataFromSupabase()
            }.onFailure { e ->
                Toast.makeText(requireContext(), "Gagal memperbarui: ${e.localizedMessage ?: e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showHapusProduksiDialog(produksi: ProduksiModel) {
        val id = produksi.id ?: return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Data Produksi")
            .setMessage("Apakah Anda yakin ingin menghapus data produksi ${produksi.sekolah}?")
            .setPositiveButton("Hapus") { dialog, _ ->
                dialog.dismiss()
                hapusProduksiSupabase(id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusProduksiSupabase(id: Long) {
        val currentBinding = _binding ?: return
        currentBinding.progressBarProduksi.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = repository.deleteProduksi(getToken(), id)
            currentBinding.progressBarProduksi.visibility = View.GONE

            result.onSuccess {
                Toast.makeText(requireContext(), "Data produksi berhasil dihapus!", Toast.LENGTH_SHORT).show()
                loadDataFromSupabase()
            }.onFailure { e ->
                Toast.makeText(requireContext(), "Gagal menghapus: ${e.localizedMessage ?: e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
