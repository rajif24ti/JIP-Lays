package com.example.jip_lays

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jip_lays.databinding.ActivityDashboardBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari Login
        val username = intent.getStringExtra("username")
        val role = intent.getStringExtra("role")

        if (!username.isNullOrEmpty()) {
            binding.tvNamaUser.text = username
        }

        if (!role.isNullOrEmpty()) {
            binding.tvRole.text = role
        }

        // Menu Absensi
        binding.cardAbsensi.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AbsensiActivity::class.java
                )
            )
        }

        // Menu Produksi
        binding.cardProduksi.setOnClickListener {
            // Halaman produksi dapat ditambahkan nanti
        }

        // Menu Karyawan
        binding.cardKaryawan.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AbsensiActivity::class.java
                )
            )
        }

        // Menu Gizi
        binding.cardGizi.setOnClickListener {
            // Halaman gizi dapat ditambahkan nanti
        }

        // Tombol profil / logout
        binding.navProfile.setOnClickListener {
            tampilkanDialogLogout()
        }
    }

    private fun tampilkanDialogLogout() {

        MaterialAlertDialogBuilder(this)
            .setTitle("Keluar dari Dashboard?")
            .setMessage(
                "Apakah Anda yakin ingin keluar dari akun?"
            )
            .setNegativeButton("Batal", null)
            .setPositiveButton("Keluar") { _, _ ->

                val intent = Intent(
                    this,
                    LoginActivity::class.java
                )

                // Menghapus halaman Dashboard dari stack
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)

                finish()
            }
            .show()
    }
}