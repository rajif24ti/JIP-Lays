package com.example.jip_lays

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jip_lays.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val nama = binding.etNama.text.toString().trim()
            val handphone = binding.etHandphone.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val role = binding.etRole.text.toString().trim()

            // Validasi semua input
            if (
                nama.isEmpty() ||
                handphone.isEmpty() ||
                username.isEmpty() ||
                password.isEmpty() ||
                role.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Semua kolom wajib diisi",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validasi nomor handphone
            if (handphone.any { !it.isDigit() }) {
                Toast.makeText(
                    this,
                    "Nomor handphone harus berupa angka",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validasi role
            val rolesValid = listOf(
                "Penanggung Jawab Dapur",
                "Ahli Gizi",
                "Penanggung Jawab Sekolah"
            )

            if (role !in rolesValid) {
                binding.etRole.error =
                    "Masukkan salah satu role yang tersedia"
                binding.etRole.requestFocus()
                return@setOnClickListener
            }

            // Kirim data registrasi ke halaman verifikasi
            val intent = Intent(
                this,
                VerificationActivity::class.java
            ).apply {
                putExtra("nama", nama)
                putExtra("handphone", handphone)
                putExtra("username", username)
                putExtra("password", password)
                putExtra("role", role)
            }

            startActivity(intent)
        }

        // Kembali ke halaman login
        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }
}
