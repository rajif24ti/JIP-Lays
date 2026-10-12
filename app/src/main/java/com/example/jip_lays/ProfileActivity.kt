package com.example.jip_lays

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jip_lays.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Isi profil contoh, ganti dengan data developer sebenarnya
        binding.tvDeveloperName.text = "M. Rajif Dauta Abimanyu"
        binding.tvDeveloperRole.text = "Mahasiswa Teknologi Informasi"
        binding.tvAbout.text =
            "Saya adalah seorang yang suka aktif dalam " +
                    "berkegiatan dikampus maupun di luar kampus, " +
                    "gampang tidur susah bangun, saya juga penggemar " +
                    "fanatik Manchester United, dan pemain badminton yang terkuat saat ini di PCR."
        binding.tvHobby.text =
            "Olahraga, design, dan fotography/videography."
        binding.tvStrength.text =
            "Mampu belajar hal baru, mudah beradaptasi, bisa memimpin suatu kelompok, " +
                    "dan memiliki problem solving tersendiri."
        binding.tvInstagram.text = "Instagram: @mda_rajif"
        binding.tvGithub.text = "GitHub: github.com/rajif24ti"

        // Tandai menu Profil sebagai menu aktif
        binding.bottomNavigation.selectedItemId = R.id.menu_profile

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.menu_home -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.menu_karyawan -> {
                    Toast.makeText(
                        this,
                        "Hubungkan ke halaman Karyawan",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                R.id.menu_produksi -> {
                    Toast.makeText(
                        this,
                        "Hubungkan ke halaman Produksi",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                R.id.menu_profile -> true

                else -> false
            }
        }
    }
}
