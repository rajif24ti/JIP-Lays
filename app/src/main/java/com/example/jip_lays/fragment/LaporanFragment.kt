package com.example.jip_lays.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.jip_lays.LaporanKerusakanActivity
import com.example.jip_lays.KerusakanRepository
import com.example.jip_lays.R
import com.example.jip_lays.databinding.FragmentLaporanBinding
import com.google.android.material.card.MaterialCardView
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LaporanFragment : Fragment() {

    private var _binding: FragmentLaporanBinding? = null
    private val binding get() = _binding!!

    private var searchQuery = ""
    private var selectedPrioritas = "Semua Prioritas"

    private val createPdfLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        if (uri != null) {
            exportPdfToUri(uri)
        } else {
            Toast.makeText(requireContext(), "Ekspor PDF dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    private val formKerusakanLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            tampilkanDaftarKerusakan()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLaporanBinding.inflate(
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

        val tanggal = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date())
        binding.tvTanggalLaporan.text = tanggal

        // Ringkasan data
        binding.tvLaporanPorsi.text = "2.000"
        binding.tvLaporanKaryawan.text = "8"
        binding.tvLaporanSekolah.text = "5"

        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val role = sharedPref.getString("role", "Penanggung Jawab Dapur") ?: "Penanggung Jawab Dapur"

        // Tombol Buat Laporan
        binding.btnBuatLaporan.setOnClickListener {
            if (role == "Penanggung Jawab Dapur") {
                val intent = Intent(requireContext(), LaporanKerusakanActivity::class.java)
                formKerusakanLauncher.launch(intent)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Fitur ini khusus untuk Penanggung Jawab Dapur",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Tombol Export PDF
        binding.btnExportPdf.setOnClickListener {
            if (role == "Penanggung Jawab Dapur") {
                val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                createPdfLauncher.launch("Laporan_Kerusakan_$dateStr.pdf")
            } else {
                Toast.makeText(
                    requireContext(),
                    "Fitur ekspor PDF khusus untuk Penanggung Jawab Dapur",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Setup filter prioritas dropdown
        val prioritasList = arrayOf("Semua Prioritas", "Rendah", "Sedang", "Tinggi", "Darurat")
        val prioritasAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, prioritasList)
        binding.actFilterPrioritas.setAdapter(prioritasAdapter)
        binding.actFilterPrioritas.setText("Semua Prioritas", false)
        binding.actFilterPrioritas.setOnItemClickListener { _, _, position, _ ->
            selectedPrioritas = prioritasList[position]
            tampilkanDaftarKerusakan()
        }

        // Search listener
        binding.etSearchLaporan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().trim().lowercase()
                tampilkanDaftarKerusakan()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        tampilkanDaftarKerusakan()
    }

    override fun onResume() {
        super.onResume()
        tampilkanDaftarKerusakan()
    }

    private fun tampilkanDaftarKerusakan() {
        binding.containerKerusakan.removeAllViews()

        val filteredList = KerusakanRepository.daftarKerusakan.filter { item ->
            val matchesSearch = searchQuery.isEmpty() ||
                    item.namaPeralatan.lowercase().contains(searchQuery) ||
                    item.lokasiDivisi.lowercase().contains(searchQuery) ||
                    item.deskripsi.lowercase().contains(searchQuery)
            val matchesPrioritas = selectedPrioritas == "Semua Prioritas" ||
                    item.tingkatKepentingan.equals(selectedPrioritas, ignoreCase = true)
            matchesSearch && matchesPrioritas
        }

        if (filteredList.isEmpty()) {
            val tvEmpty = TextView(requireContext()).apply {
                text = "Tidak ada laporan kerusakan yang cocok."
                textSize = 13f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_body))
                setPadding(0, 16, 0, 16)
            }
            binding.containerKerusakan.addView(tvEmpty)
            return
        }

        filteredList.forEach { item ->
            val card = MaterialCardView(requireContext()).apply {
                radius = 14f * resources.displayMetrics.density
                cardElevation = 2f * resources.displayMetrics.density
                setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                strokeWidth = 1
                strokeColor = Color.parseColor("#DFE6EF")
            }

            val layout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
            }

            val tvNama = TextView(requireContext()).apply {
                text = item.namaPeralatan
                textSize = 16f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_navy))
                setTypeface(null, Typeface.BOLD)
            }

            val tvDivisi = TextView(requireContext()).apply {
                text = "Lokasi: ${item.lokasiDivisi} | ${item.tanggal}"
                textSize = 12f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_body))
                setPadding(0, 4, 0, 0)
            }

            val tvDeskripsi = TextView(requireContext()).apply {
                text = item.deskripsi
                textSize = 13f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_body))
                setPadding(0, 8, 0, 0)
            }

            val tvPrioritas = TextView(requireContext()).apply {
                text = "Prioritas: ${item.tingkatKepentingan}"
                textSize = 12f
                setTypeface(null, Typeface.BOLD)
                setPadding(20, 10, 20, 10)
                when (item.tingkatKepentingan.lowercase()) {
                    "darurat" -> {
                        setBackgroundColor(Color.parseColor("#FFDADA"))
                        setTextColor(Color.parseColor("#C00000"))
                    }
                    "tinggi" -> {
                        setBackgroundColor(Color.parseColor("#FFF0CC"))
                        setTextColor(Color.parseColor("#956000"))
                    }
                    "sedang" -> {
                        setBackgroundColor(Color.parseColor("#E8EEF6"))
                        setTextColor(Color.parseColor("#2B5C92"))
                    }
                    else -> {
                        setBackgroundColor(Color.parseColor("#E7EAF0"))
                        setTextColor(Color.parseColor("#505B6B"))
                    }
                }
            }

            layout.addView(tvNama)
            layout.addView(tvDivisi)
            layout.addView(tvDeskripsi)

            val prioritasParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 12
            }
            layout.addView(tvPrioritas, prioritasParams)

            // If photo path exists and file exists, render photo preview
            if (!item.photoPath.isNullOrEmpty()) {
                val imgFile = File(item.photoPath)
                if (imgFile.exists()) {
                    val imgPreview = ImageView(requireContext()).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        setImageBitmap(bitmap)
                    }
                    val imgParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (140 * resources.displayMetrics.density).toInt()
                    ).apply {
                        topMargin = 12
                    }
                    layout.addView(imgPreview, imgParams)
                }
            }

            card.addView(layout)

            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
            binding.containerKerusakan.addView(card, cardParams)
        }
    }

    private fun exportPdfToUri(uri: Uri) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (595x842 pt)
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val paintHeader = Paint().apply {
                color = Color.parseColor("#0C1446") // primary_navy
                textSize = 18f
                isFakeBoldText = true
            }

            val paintSubHeader = Paint().apply {
                color = Color.parseColor("#2B5C92") // primary_blue
                textSize = 12f
            }

            val paintBodyBold = Paint().apply {
                color = Color.BLACK
                textSize = 11f
                isFakeBoldText = true
            }

            val paintBody = Paint().apply {
                color = Color.DKGRAY
                textSize = 10f
            }

            val paintLine = Paint().apply {
                color = Color.LTGRAY
                strokeWidth = 1f
            }

            var y = 40f

            // Title Header
            canvas.drawText("LAPORAN KERUSAKAN PERALATAN DAPUR", 40f, y, paintHeader)
            y += 20f
            canvas.drawText("JIP-Lays (SIMOD MBG - Sistem Informasi Manajemen Operasional Dapur)", 40f, y, paintSubHeader)
            y += 15f

            val tanggalStr = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.forLanguageTag("id-ID")).format(Date())
            canvas.drawText("Tanggal Ekspor: $tanggalStr", 40f, y, paintBody)
            y += 15f

            canvas.drawLine(40f, y, 555f, y, paintLine)
            y += 25f

            // Section title
            canvas.drawText("DAFTAR KERUSAKAN PERALATAN DAPUR", 40f, y, paintBodyBold)
            y += 20f

            val reportsToExport = KerusakanRepository.daftarKerusakan

            if (reportsToExport.isEmpty()) {
                canvas.drawText("Tidak ada data laporan kerusakan.", 40f, y, paintBody)
            } else {
                reportsToExport.forEachIndexed { index, item ->
                    if (y > 760f) {
                        // Footer page number
                        canvas.drawText("Halaman 1 - SIMOD MBG", 40f, 810f, paintBody)
                        pdfDocument.finishPage(page)
                        return@forEachIndexed
                    }

                    canvas.drawText("${index + 1}. ${item.namaPeralatan} [${item.tingkatKepentingan.uppercase()}]", 40f, y, paintBodyBold)
                    y += 14f
                    canvas.drawText("    Lokasi/Divisi: ${item.lokasiDivisi} | Tanggal: ${item.tanggal}", 40f, y, paintBody)
                    y += 14f
                    canvas.drawText("    Deskripsi: ${item.deskripsi}", 40f, y, paintBody)
                    y += 16f

                    // If photo exists, draw thumbnail
                    if (!item.photoPath.isNullOrEmpty()) {
                        val imgFile = File(item.photoPath)
                        if (imgFile.exists()) {
                            try {
                                val originalBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                                if (originalBitmap != null) {
                                    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 120, 80, true)
                                    canvas.drawBitmap(scaledBitmap, 50f, y, null)
                                    y += 90f
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    canvas.drawLine(40f, y, 555f, y, paintLine)
                    y += 15f
                }
            }

            // Footer
            canvas.drawText("Dokumen ini diterbitkan secara otomatis oleh Aplikasi JIP-Lays SIMOD MBG", 40f, 810f, paintBody)

            pdfDocument.finishPage(page)

            requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()

            Toast.makeText(requireContext(), "Laporan PDF berhasil disimpan!", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Gagal mengekspor PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
