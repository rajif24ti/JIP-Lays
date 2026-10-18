package com.example.jip_lays

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.jip_lays.databinding.ActivityBaseBinding

class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Menu Dashboard aktif saat aplikasi dibuka
        binding.bottomNavView.selectedItemId = R.id.menu_home
    }

    // Mengganti fragment pada container utama
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }
}
