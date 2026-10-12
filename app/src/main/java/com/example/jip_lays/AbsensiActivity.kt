package com.example.jip_lays

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.jip_lays.databinding.ActivityAbsensiBinding

class AbsensiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsensiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAbsensiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol kembali ke halaman sebelumnya
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Tombol tambah absensi
        binding.btnSimpan.setOnClickListener {
            Toast.makeText(
                this,
                "Form tambah absensi",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Kondisi awal: tab Masuk aktif
        setTabAktif(isMasuk = true)

        // Tab Masuk
        binding.tabMasuk.setOnClickListener {
            setTabAktif(isMasuk = true)

            Toast.makeText(
                this,
                "Absensi Masuk dipilih",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Tab Pulang
        binding.tabPulang.setOnClickListener {
            setTabAktif(isMasuk = false)

            Toast.makeText(
                this,
                "Absensi Pulang dipilih",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Mengatur tampilan tab yang sedang aktif
    private fun setTabAktif(isMasuk: Boolean) {
        val warnaAktif = ContextCompat.getColor(
            this,
            R.color.primary_blue
        )

        val warnaTeks = ContextCompat.getColor(
            this,
            R.color.text_body
        )

        val warnaPutih = ContextCompat.getColor(
            this,
            R.color.white
        )

        if (isMasuk) {
            binding.tabMasuk.setBackgroundColor(warnaAktif)
            binding.tabMasuk.setTextColor(warnaPutih)

            binding.tabPulang.setBackgroundColor(
                android.graphics.Color.TRANSPARENT
            )
            binding.tabPulang.setTextColor(warnaTeks)
        } else {
            binding.tabPulang.setBackgroundColor(warnaAktif)
            binding.tabPulang.setTextColor(warnaPutih)

            binding.tabMasuk.setBackgroundColor(
                android.graphics.Color.TRANSPARENT
            )
            binding.tabMasuk.setTextColor(warnaTeks)
        }
    }
}
