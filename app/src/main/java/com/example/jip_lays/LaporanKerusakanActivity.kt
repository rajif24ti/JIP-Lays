package com.example.jip_lays

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.jip_lays.databinding.ActivityLaporanKerusakanBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LaporanKerusakanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaporanKerusakanBinding
    private var photoUri: Uri? = null
    private var currentPhotoPath: String? = null

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            binding.imgBukti.setImageURI(photoUri)
            Toast.makeText(this, "Foto kamera berhasil diambil", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Pengambilan foto kamera dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
            val localFile = saveUriToInternalFile(uri)
            currentPhotoPath = localFile.absolutePath
            binding.imgBukti.setImageURI(uri)
            Toast.makeText(this, "Foto galeri berhasil dipilih", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Pemilihan foto dari galeri dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaporanKerusakanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore saved state if activity was recreated during camera capture
        if (savedInstanceState != null) {
            photoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState.getParcelable("photoUri", Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                savedInstanceState.getParcelable("photoUri")
            }
            currentPhotoPath = savedInstanceState.getString("currentPhotoPath")
            photoUri?.let {
                binding.imgBukti.setImageURI(it)
            }
        }

        // Check role: Only Penanggung Jawab Dapur
        val role = getSharedPreferences("user_pref", MODE_PRIVATE)
            .getString("role", "Penanggung Jawab Dapur") ?: "Penanggung Jawab Dapur"

        if (role != "Penanggung Jawab Dapur") {
            Toast.makeText(this, "Akses ditolak: Fitur ini khusus Penanggung Jawab Dapur", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setSupportActionBar(binding.toolbarKerusakan)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbarKerusakan.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Setup dropdown for priority
        val priorities = arrayOf("Rendah", "Sedang", "Tinggi", "Darurat")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, priorities)
        binding.actTingkatKepentingan.setAdapter(adapter)

        // Take photo from camera
        binding.btnAmbilFoto.setOnClickListener {
            dispatchTakePictureIntent()
        }

        // Pick photo from gallery
        binding.btnPilihGaleri.setOnClickListener {
            pickGalleryLauncher.launch("image/*")
        }

        // Retake / change photo
        binding.btnAmbilUlang.setOnClickListener {
            dispatchTakePictureIntent()
        }

        // Save report button
        binding.btnSimpanLaporan.setOnClickListener {
            val namaPeralatan = binding.etNamaPeralatan.text.toString().trim()
            val lokasiDivisi = binding.etLokasiDivisi.text.toString().trim()
            val deskripsi = binding.etDeskripsi.text.toString().trim()
            val tingkatKepentingan = binding.actTingkatKepentingan.text.toString().trim()

            if (namaPeralatan.isEmpty() || lokasiDivisi.isEmpty() || deskripsi.isEmpty() || tingkatKepentingan.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi semua data formulir laporan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tanggalHariIni = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date())

            val newReport = KerusakanModel(
                namaPeralatan = namaPeralatan,
                lokasiDivisi = lokasiDivisi,
                deskripsi = deskripsi,
                tingkatKepentingan = tingkatKepentingan,
                photoPath = currentPhotoPath,
                tanggal = tanggalHariIni
            )

            KerusakanRepository.daftarKerusakan.add(0, newReport)

            Toast.makeText(this, "Laporan kerusakan berhasil disimpan", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        photoUri?.let { outState.putParcelable("photoUri", it) }
        currentPhotoPath?.let { outState.putString("currentPhotoPath", it) }
    }

    private fun dispatchTakePictureIntent() {
        try {
            val photoFile = createImageFile()
            currentPhotoPath = photoFile.absolutePath
            photoUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                photoFile
            )
            photoUri?.let { takePictureLauncher.launch(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal membuka kamera: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = filesDir
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun saveUriToInternalFile(uri: Uri): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val destFile = File(filesDir, "JPEG_${timeStamp}_gallery.jpg")
        contentResolver.openInputStream(uri)?.use { inputStream ->
            destFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return destFile
    }
}
