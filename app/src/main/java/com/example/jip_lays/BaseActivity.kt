package com.example.jip_lays

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.jip_lays.databinding.ActivityBaseBinding
import com.example.jip_lays.fragment.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarBase)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // Navigasi menu bawah
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.menu_karyawan -> {
                    replaceFragment(KaryawanFragment())
                    true
                }
                R.id.menu_produksi -> {
                    replaceFragment(ProduksiFragment())
                    true
                }
                R.id.menu_laporan -> {
                    replaceFragment(LaporanFragment())
                    true
                }
                R.id.menu_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        binding.bottomNavView.selectedItemId = R.id.menu_home
    }

    fun selectBottomMenuItem(itemId: Int) {
        binding.bottomNavView.selectedItemId = itemId
    }

    // MODUL 11 Requirement: Option Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return try {
            menuInflater.inflate(R.menu.option_menu, menu)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return try {
            when (item.itemId) {
                R.id.option_home -> {
                    selectBottomMenuItem(R.id.menu_home)
                    true
                }
                R.id.option_kotak_saran -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, KotakSaranFragment())
                        .addToBackStack("kotak_saran")
                        .commit()
                    true
                }
                R.id.option_tutorial -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Tutorial & Panduan Aplikasi")
                        .setMessage("JIP-Lays (Sistem Informasi Manajemen Operasional Dapur MBG) dirancang untuk mempermudah pengelolaan dapur SPPG/MBG.\n\n" +
                                "• Menu Home: Ringkasan operasional & fitur cepat\n" +
                                "• Menu Karyawan: Pencatatan absensi & daftar karyawan\n" +
                                "• Menu Produksi: Pemantauan produksi & gizi makanan\n" +
                                "• Menu Laporan: Form kerusakan & ekspor PDF\n\n" +
                                "Klik tombol di bawah untuk melihat layar petunjuk interaktif (Onboarding).")
                        .setNegativeButton("Tutup", null)
                        .setPositiveButton("Lihat Onboarding") { dialog, _ ->
                            dialog.dismiss()
                            startActivity(Intent(this, OnboardingActivity::class.java))
                        }
                        .show()
                    true
                }
                R.id.option_about -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Tentang JIP-Lays SIMOD MBG")
                        .setMessage("JIP-Lays (Sistem Informasi Manajemen Operasional Dapur MBG) v1.0\n\nAplikasi ini digunakan untuk mengelola aktivitas operasional dapur SPPG/MBG, mulai dari absensi karyawan, pemantauan produksi, pencatatan kendala kerusakan peralatan dapur, hingga generasi laporan dokumen PDF.")
                        .setPositiveButton("Tutup", null)
                        .show()
                    true
                }
                R.id.option_help -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Bantuan & Layanan Support")
                        .setMessage("Apabila Anda mengalami kendala operasional, silakan hubungi tim IT Support MBG melalui email: support@jip-lays.com atau kunjungi portal resmi kami.")
                        .setPositiveButton("OK", null)
                        .show()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            super.onOptionsItemSelected(item)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }
}
