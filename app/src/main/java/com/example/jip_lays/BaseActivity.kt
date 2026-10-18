
package com.example.jip_lays

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.jip_lays.databinding.ActivityBaseBinding
import com.example.jip_lays.fragment.HomeFragment
import com.example.jip_lays.fragment.KaryawanFragment
import com.example.jip_lays.fragment.ProduksiFragment
import com.example.jip_lays.fragment.ProfileFragment

class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

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

                R.id.menu_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }

        binding.bottomNavView.selectedItemId = R.id.menu_home
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }
}
