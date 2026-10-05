package com.example.jip_lays

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jip_lays.databinding.ActivityAbsensiBinding

class AbsensiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsensiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAbsensiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol kembali
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

        // Tab masuk
        binding.tabMasuk.setOnClickListener {

            binding.tabMasuk.setBackgroundColor(
                getColor(R.color.primary_blue)
            )

            binding.tabMasuk.setTextColor(
                getColor(R.color.white)
            )

            binding.tabPulang.setBackgroundColor(
                getColor(android.R.color.transparent)
            )

            binding.tabPulang.setTextColor(
                getColor(R.color.text_body)
            )
        }

        // Tab pulang
        binding.tabPulang.setOnClickListener {

            binding.tabPulang.setBackgroundColor(
                getColor(R.color.primary_blue)
            )

            binding.tabPulang.setTextColor(
                getColor(R.color.white)
            )

            binding.tabMasuk.setBackgroundColor(
                getColor(android.R.color.transparent)
            )

            binding.tabMasuk.setTextColor(
                getColor(R.color.text_body)
            )
        }
    }
}