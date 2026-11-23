package com.example.jip_lays

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.jip_lays.data.repository.AuthRepository
import com.example.jip_lays.databinding.ActivityRegisterBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val nama = binding.etNama.text.toString().trim()
            val handphone = binding.etHandphone.text.toString().trim()
            val email = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val role = binding.etRole.text.toString().trim()

            // Validasi semua input
            if (nama.isEmpty() || handphone.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi format email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etUsername.error = "Format email tidak valid"
                binding.etUsername.requestFocus()
                return@setOnClickListener
            }

            // Validasi panjang password
            if (password.length < 6) {
                binding.etPassword.error = "Password minimal 6 karakter"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            // Validasi nomor handphone
            if (handphone.any { !it.isDigit() }) {
                Toast.makeText(this, "Nomor handphone harus berupa angka", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi role
            val rolesValid = listOf(
                "Penanggung Jawab Dapur",
                "Ahli Gizi",
                "Penanggung Jawab Sekolah"
            )

            if (role !in rolesValid) {
                binding.etRole.error = "Masukkan salah satu role yang tersedia"
                binding.etRole.requestFocus()
                return@setOnClickListener
            }

            // Loading state
            binding.btnRegister.isEnabled = false
            binding.btnRegister.text = "Mendaftarkan..."

            lifecycleScope.launch {
                val result = authRepository.signUp(
                    email = email,
                    password = password,
                    nama = nama,
                    handphone = handphone,
                    role = role
                )

                binding.btnRegister.isEnabled = true
                binding.btnRegister.text = "Daftar"

                result.onSuccess {
                    MaterialAlertDialogBuilder(this@RegisterActivity)
                        .setTitle("Registrasi Berhasil")
                        .setMessage("Akun Supabase berhasil dibuat untuk $email. Silakan login.")
                        .setCancelable(false)
                        .setPositiveButton("Ke Halaman Login") { _, _ ->
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                            startActivity(intent)
                            finish()
                        }
                        .show()
                }.onFailure { exception ->
                    MaterialAlertDialogBuilder(this@RegisterActivity)
                        .setTitle("Registrasi Gagal")
                        .setMessage("Gagal membuat akun: ${exception.localizedMessage ?: exception.message}")
                        .setPositiveButton("Coba Lagi", null)
                        .show()
                }
            }
        }

        // Kembali ke halaman login
        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }
}
