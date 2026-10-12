package com.example.jip_lays

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jip_lays.databinding.ActivityDashboardBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil username dan role dari LoginActivity
        val username = intent.getStringExtra("username") ?: "Pengguna"
        val role = intent.getStringExtra("role") ?: "Pengguna SIMOD MBG"

        binding.tvWelcome.text = role

        // Tombol logout
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari akun?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya") { dialog, _ ->

                    // Hapus sesi login
                    val sharedPref =
                        getSharedPreferences("user_pref", MODE_PRIVATE)

                    sharedPref.edit()
                        .clear()
                        .apply()

                    dialog.dismiss()

                    // Kembali ke LoginActivity
                    val logoutIntent = Intent(
                        this,
                        LoginActivity::class.java
                    )

                    logoutIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(logoutIntent)
                    finish()
                }
                .show()
        }

        // Menu cepat: Absensi
        binding.cardAbsensi.setOnClickListener {
            startActivity(
                Intent(this, AbsensiActivity::class.java)
            )
        }

        // Menu cepat: Gizi
        binding.cardGizi.setOnClickListener {
            Toast.makeText(
                this,
                "Menu Gizi dipilih",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Navigasi bawah
        binding.bottomNavigation.selectedItemId = R.id.menu_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.menu_home -> {
                    true
                }

                R.id.menu_karyawan -> {
                    startActivity(
                        Intent(this, AbsensiActivity::class.java)
                    )
                    true
                }

                R.id.menu_produksi -> {
                    Toast.makeText(
                        this,
                        "Menu Gizi dipilih",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                R.id.menu_profile -> {
                    val profileIntent = Intent(
                        this,
                        ProfileActivity::class.java
                    )
                    profileIntent.putExtra("username", username)
                    profileIntent.putExtra("role", role)
                    startActivity(profileIntent)
                    true
                }

                else -> false
            }
        }
    }
}
