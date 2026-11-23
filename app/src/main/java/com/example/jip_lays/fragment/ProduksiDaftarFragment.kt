package com.example.jip_lays.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.jip_lays.R
import com.example.jip_lays.data.model.NutrisiModel
import com.example.jip_lays.data.model.ProduksiModel
import com.example.jip_lays.data.model.SekolahPorsiModel
import com.example.jip_lays.data.repository.ProduksiRemoteRepository
import com.example.jip_lays.data.repository.StorageRepository
import com.example.jip_lays.databinding.DialogProduksiFormBinding
import com.example.jip_lays.databinding.FragmentProduksiDaftarBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ProduksiDaftarFragment : Fragment() {

    private var _binding: FragmentProduksiDaftarBinding? = null
    private val repository = ProduksiRemoteRepository()
    private val storageRepository = StorageRepository()
    private val productionList = mutableListOf<ProduksiModel>()
    private var searchQuery = ""
    private var selectedStatus = "Semua Status"
    private var activeForm: ProductionForm? = null

    private val statusOptions = listOf("Direncanakan", "Dalam Proses", "Selesai", "Didistribusikan")
    private val schools = listOf(
        "SD Negeri 001 Pekanbaru",
        "SD Negeri 002 Pekanbaru",
        "SD Negeri 003 Pekanbaru",
        "SMP Negeri 1 Pekanbaru",
        "SMP Negeri 2 Pekanbaru"
    )

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val form = activeForm ?: return@registerForActivityResult
        if (uri != null) {
            form.selectedImageUri = uri
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.lays)
                .error(R.drawable.lays)
                .into(form.binding.imgPreviewMenuDialog)
        }
    }

    private data class ProductionForm(
        val dialog: androidx.appcompat.app.AlertDialog,
        val binding: DialogProduksiFormBinding,
        val original: ProduksiModel?,
        var selectedImageUri: Uri? = null,
        var isSaving: Boolean = false
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProduksiDaftarBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = _binding ?: return

        val statusFilters = listOf("Semua Status") + statusOptions
        binding.actFilterStatusProduksi.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statusFilters)
        )
        binding.actFilterStatusProduksi.setText(statusFilters.first(), false)
        binding.actFilterStatusProduksi.setOnClickListener { binding.actFilterStatusProduksi.showDropDown() }
        binding.actFilterStatusProduksi.setOnItemClickListener { _, _, position, _ ->
            selectedStatus = statusFilters[position]
            renderProductionList()
        }
        binding.etSearchProduksi.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().trim().lowercase(Locale.ROOT)
                renderProductionList()
            }
            override fun afterTextChanged(s: android.text.Editable?) = Unit
        })
        binding.btnTambahProduksi.setOnClickListener { showProductionForm(null) }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) loadProductions()
    }

    private fun accessToken(): String? =
        requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
            .getString("access_token", null)

    private fun loadProductions() {
        val binding = _binding ?: return
        binding.progressBarProduksi.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = repository.getProduksi(accessToken())
            _binding?.progressBarProduksi?.visibility = View.GONE
            result.onSuccess {
                productionList.clear()
                productionList.addAll(it)
                renderProductionList()
            }.onFailure {
                showMessage("Gagal memuat produksi: ${it.localizedMessage ?: it.message}")
                renderProductionList()
            }
        }
    }

    private fun renderProductionList() {
        val binding = _binding ?: return
        binding.containerProduksi.removeAllViews()
        val filtered = productionList.filter { item ->
            val schoolsText = item.sekolahTujuan.orEmpty().joinToString(" ") { it.sekolah }
            val matchesQuery = searchQuery.isBlank() ||
                item.menu.contains(searchQuery, ignoreCase = true) ||
                schoolsText.contains(searchQuery, ignoreCase = true)
            val matchesStatus = selectedStatus == "Semua Status" || item.status == selectedStatus
            matchesQuery && matchesStatus
        }
        if (filtered.isEmpty()) {
            binding.containerProduksi.addView(TextView(requireContext()).apply {
                text = if (productionList.isEmpty()) "Belum ada data produksi." else "Tidak ada data produksi yang cocok."
                textSize = 13f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_body))
                setPadding(0, 16, 0, 16)
            })
            return
        }

        filtered.forEach { item ->
            val card = MaterialCardView(requireContext()).apply {
                radius = 14f * resources.displayMetrics.density
                cardElevation = 2f * resources.displayMetrics.density
                setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                strokeWidth = (resources.displayMetrics.density).toInt()
                strokeColor = ContextCompat.getColor(requireContext(), R.color.border)
            }
            val content = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
            }
            val image = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (150 * resources.displayMetrics.density).toInt()
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
                contentDescription = "Gambar menu ${item.menu}"
                setImageResource(R.drawable.lays)
            }
            if (!item.gambarUrl.isNullOrBlank()) {
                Glide.with(this)
                    .load(item.gambarUrl)
                    .placeholder(R.drawable.lays)
                    .error(R.drawable.lays)
                    .into(image)
            }
            content.addView(image)
            content.addView(label(item.menu, 16f, R.color.primary_navy, true))
            content.addView(label("Tanggal: ${item.tanggal} • ${item.porsi} porsi", 13f, R.color.text_body))
            content.addView(label("Status: ${item.status}", 13f, R.color.primary_blue, true))
            val destinations = item.sekolahTujuan.orEmpty()
                .joinToString("\n") { "${it.sekolah}: ${it.porsi} porsi" }
                .ifBlank { "Belum ada sekolah penerima" }
            content.addView(label("Sekolah tujuan:\n$destinations", 12f, R.color.text_body))

            val actions = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.END
            }
            actions.addView(MaterialButton(requireContext()).apply {
                text = "Edit"
                textSize = 11f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                setOnClickListener { showProductionForm(item) }
            })
            actions.addView(MaterialButton(requireContext()).apply {
                text = "Hapus"
                textSize = 11f
                setTextColor(android.graphics.Color.rgb(211, 47, 47))
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                setOnClickListener { confirmDelete(item) }
            })
            content.addView(actions)
            card.addView(content)
            binding.containerProduksi.addView(card, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = (12 * resources.displayMetrics.density).toInt() })
        }
    }

    private fun label(textValue: String, size: Float, color: Int, bold: Boolean = false) =
        TextView(requireContext()).apply {
            text = textValue
            textSize = size
            setTextColor(ContextCompat.getColor(requireContext(), color))
            if (bold) setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 8, 0, 0)
        }

    private fun showProductionForm(item: ProduksiModel?) {
        val formBinding = DialogProduksiFormBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (item == null) "Tambah Produksi" else "Edit Produksi")
            .setView(formBinding.root)
            .setPositiveButton(if (item == null) "Simpan" else "Simpan Perubahan", null)
            .setNegativeButton("Batal", null)
            .create()
        val form = ProductionForm(dialog, formBinding, item)
        activeForm = form

        formBinding.actStatusProduksiDialog.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statusOptions)
        )
        formBinding.actStatusProduksiDialog.setOnClickListener { formBinding.actStatusProduksiDialog.showDropDown() }
        formBinding.btnPilihGambarMenu.setOnClickListener { imagePicker.launch("image/*") }
        formBinding.etTanggalProduksiDialog.setOnClickListener { showDatePicker(formBinding.etTanggalProduksiDialog) }
        formBinding.etTanggalProduksiDialog.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) showDatePicker(formBinding.etTanggalProduksiDialog)
        }
        bindAllocationInfo(formBinding)

        if (item == null) {
            formBinding.etTanggalProduksiDialog.setText(todayIsoDate())
            formBinding.actStatusProduksiDialog.setText(statusOptions.first(), false)
            formBinding.imgPreviewMenuDialog.setImageResource(R.drawable.lays)
        } else {
            formBinding.etMenuDialog.setText(item.menu)
            formBinding.etPorsiDialog.setText(item.porsi.toString())
            formBinding.etTanggalProduksiDialog.setText(item.tanggal)
            formBinding.actStatusProduksiDialog.setText(item.status, false)
            val nutrition = item.gizi
            formBinding.etEnergi.setText(nutrition?.energi?.toString().orEmpty())
            formBinding.etProtein.setText(nutrition?.protein?.toString().orEmpty())
            formBinding.etKarbohidrat.setText(nutrition?.karbohidrat?.toString().orEmpty())
            formBinding.etLemak.setText(nutrition?.lemak?.toString().orEmpty())
            formBinding.etSerat.setText(nutrition?.serat?.toString().orEmpty())
            formBinding.etKalsium.setText(nutrition?.kalsium?.toString().orEmpty())
            formBinding.etZatBesi.setText(nutrition?.zatBesi?.toString().orEmpty())
            val allocations = item.sekolahTujuan.orEmpty().associate { it.sekolah to it.porsi }
            formBinding.etPorsiSdn001.setText(allocations[schools[0]]?.toString().orEmpty())
            formBinding.etPorsiSdn002.setText(allocations[schools[1]]?.toString().orEmpty())
            formBinding.etPorsiSdn003.setText(allocations[schools[2]]?.toString().orEmpty())
            formBinding.etPorsiSmpn1.setText(allocations[schools[3]]?.toString().orEmpty())
            formBinding.etPorsiSmpn2.setText(allocations[schools[4]]?.toString().orEmpty())
            if (!item.gambarUrl.isNullOrBlank()) {
                Glide.with(this).load(item.gambarUrl).placeholder(R.drawable.lays)
                    .error(R.drawable.lays).into(formBinding.imgPreviewMenuDialog)
            }
        }

        dialog.setOnDismissListener {
            if (activeForm === form) activeForm = null
        }
        dialog.show()
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            saveProduction(form)
        }
    }

    private fun bindAllocationInfo(binding: DialogProduksiFormBinding) {
        val allocationFields = listOf(
            binding.etPorsiSdn001, binding.etPorsiSdn002, binding.etPorsiSdn003,
            binding.etPorsiSmpn1, binding.etPorsiSmpn2
        )
        val refresh = {
            val production = binding.etPorsiDialog.text?.toString()?.toIntOrNull() ?: 0
            val allocated = allocationFields.sumOf { (it.text?.toString()?.toIntOrNull() ?: 0).toLong() }
            binding.tvAlokasiPorsiInfo.text =
                "Total alokasi: $allocated / $production porsi • Sisa: ${(production.toLong() - allocated).coerceAtLeast(0)}"
        }
        (listOf(binding.etPorsiDialog) + allocationFields).forEach { field ->
            field.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = refresh()
                override fun afterTextChanged(s: android.text.Editable?) = Unit
            })
        }
        refresh()
    }

    private fun saveProduction(form: ProductionForm) {
        if (form.isSaving) return
        val b = form.binding
        val menu = b.etMenuDialog.text?.toString()?.trim().orEmpty()
        val porsi = b.etPorsiDialog.text?.toString()?.toIntOrNull()
        val tanggal = b.etTanggalProduksiDialog.text?.toString()?.trim().orEmpty()
        val status = b.actStatusProduksiDialog.text?.toString()?.trim().orEmpty()
        val nutritionValues = listOf(
            b.etEnergi.text, b.etProtein.text, b.etKarbohidrat.text, b.etLemak.text,
            b.etSerat.text, b.etKalsium.text, b.etZatBesi.text
        ).map { it?.toString()?.trim()?.toDoubleOrNull() }
        val allocationFields = listOf(
            b.etPorsiSdn001, b.etPorsiSdn002, b.etPorsiSdn003, b.etPorsiSmpn1, b.etPorsiSmpn2
        )
        val allocationValues = allocationFields.map { field ->
            field.text?.toString()?.trim()?.let { if (it.isEmpty()) 0 else it.toIntOrNull() ?: -1 } ?: 0
        }

        val allocatedTotal = allocationValues.sumOf { it.toLong() }
        if (menu.isBlank() || porsi == null || porsi <= 0 ||
            runCatching { LocalDate.parse(tanggal) }.isFailure ||
            status !in statusOptions ||
            nutritionValues.any { it == null || !it.isFinite() || it < 0 } ||
            allocationValues.any { it < 0 } || allocatedTotal > (porsi ?: 0).toLong() ||
            allocationValues.none { it > 0 }
        ) {
            showMessage("Periksa data: menu, tanggal, status, gizi, dan alokasi sekolah harus valid. Total alokasi tidak boleh melebihi porsi produksi.")
            return
        }

        val allocations = schools.mapIndexedNotNull { index, school ->
            allocationValues[index].takeIf { it > 0 }?.let { SekolahPorsiModel(school, it) }
        }
        val values = nutritionValues.map { requireNotNull(it) }
        val model = ProduksiModel(
            id = form.original?.id,
            menu = menu,
            gambarUrl = form.original?.gambarUrl,
            tanggal = tanggal,
            porsi = porsi,
            status = status,
            gizi = NutrisiModel(
                energi = values[0],
                protein = values[1],
                karbohidrat = values[2],
                lemak = values[3],
                serat = values[4],
                kalsium = values[5],
                zatBesi = values[6]
            ),
            sekolahTujuan = allocations
        )

        form.isSaving = true
        form.dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).isEnabled = false
        form.dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).isEnabled = false
        _binding?.progressBarProduksi?.visibility = View.VISIBLE
        lifecycleScope.launch {
            var uploadedImageUrl: String? = null
            val uploaded = form.selectedImageUri?.let { uri ->
                storageRepository.uploadMenuImage(requireContext(), accessToken(), uri)
            }
            if (uploaded != null) {
                uploaded.onSuccess { uploadedImageUrl = it }
                    .onFailure { error ->
                        finishSaving(form)
                        showMessage("Gagal mengunggah gambar: ${error.localizedMessage ?: error.message}")
                        return@launch
                    }
            }
            val savedModel = model.copy(gambarUrl = uploadedImageUrl ?: model.gambarUrl)
            val result = if (form.original?.id == null) {
                repository.insertProduksi(accessToken(), savedModel)
            } else {
                repository.updateProduksi(accessToken(), form.original.id, savedModel)
            }
            result.onSuccess { saved ->
                form.dialog.dismiss()
                loadProductions()
                val oldUrl = form.original?.gambarUrl
                if (oldUrl != null && uploadedImageUrl != null && oldUrl != uploadedImageUrl) {
                    removeImageIfUnused(oldUrl, saved.id)
                }
                showMessage(if (form.original == null) "Produksi berhasil ditambahkan." else "Produksi berhasil diperbarui.")
            }.onFailure { error ->
                uploadedImageUrl?.let {
                    removeImageIfUnused(
                        it,
                        null,
                        "Gagal menyimpan produksi; penggunaan gambar baru tidak dapat diverifikasi, jadi gambar tidak dihapus."
                    )
                }
                finishSaving(form)
                showMessage("Gagal menyimpan produksi: ${error.localizedMessage ?: error.message}")
            }
        }
    }

    private fun finishSaving(form: ProductionForm) {
        form.isSaving = false
        _binding?.progressBarProduksi?.visibility = View.GONE
        if (form.dialog.isShowing) {
            form.dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).isEnabled = true
            form.dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).isEnabled = true
        }
    }

    private fun showDatePicker(field: android.widget.TextView) {
        val calendar = Calendar.getInstance()
        val current = field.text.toString()
        runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(current)
        }.getOrNull()?.let { calendar.time = it }
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                field.text = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun todayIsoDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("Asia/Jakarta") }
            .format(Calendar.getInstance().time)

    private fun confirmDelete(item: ProduksiModel) {
        if (item.id == null) return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Produksi")
            .setMessage("Hapus data produksi ${item.menu}?")
            .setPositiveButton("Hapus") { _, _ -> deleteProduction(item) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteProduction(item: ProduksiModel) {
        val id = item.id ?: return
        _binding?.progressBarProduksi?.visibility = View.VISIBLE
        lifecycleScope.launch {
            repository.deleteProduksi(accessToken(), id)
                .onSuccess {
                    loadProductions()
                    item.gambarUrl?.let { removeImageIfUnused(it, id) }
                    showMessage("Data produksi berhasil dihapus.")
                }
                .onFailure {
                    _binding?.progressBarProduksi?.visibility = View.GONE
                    showMessage("Gagal menghapus produksi: ${it.localizedMessage ?: it.message}")
                }
        }
    }

    private suspend fun removeImageIfUnused(
        imageUrl: String,
        removedId: Long?,
        verificationFailureMessage: String =
            "Data tersimpan, tetapi penggunaan gambar lama tidak dapat diverifikasi; gambar tidak dihapus."
    ) {
        val latest = repository.getProduksi(accessToken())
        latest.onSuccess { rows ->
            if (rows.none { it.id != removedId && it.gambarUrl == imageUrl }) {
                storageRepository.deleteMenuImage(accessToken(), imageUrl)
                    .onFailure { showMessage("Data tersimpan, tetapi gambar lama gagal dihapus dari Storage: ${it.localizedMessage ?: it.message}") }
            }
        }.onFailure {
            showMessage(verificationFailureMessage)
        }
    }

    private fun showMessage(message: String) {
        if (_binding != null) Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        activeForm?.dialog?.dismiss()
        activeForm = null
        _binding = null
        super.onDestroyView()
    }
}
