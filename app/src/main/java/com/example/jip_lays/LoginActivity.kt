package com.example.jip_lays

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.jip_lays.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences untuk menyimpan status login
        val sharedPref = getSharedPreferences(
            "user_pref",
            MODE_PRIVATE
        )

        // Pilihan role
        val roles = arrayOf(
            "Penanggung Jawab Dapur",
            "Ahli Gizi",
            "Penanggung Jawab Sekolah"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            roles
        )

        binding.actRole.setAdapter(adapter)

        // Tombol Login
        binding.btnLogin.setOnClickListener {

            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val role = binding.actRole.text.toString().trim()

            // Validasi username
            if (username.isEmpty()) {
                binding.etUsername.error = "Username harus diisi"
                binding.etUsername.requestFocus()
                return@setOnClickListener
            }

            // Validasi password
            if (password.isEmpty()) {
                binding.etPassword.error = "Password harus diisi"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            // Validasi role
            if (role.isEmpty()) {
                Toast.makeText(
                    this,
                    "Silakan pilih role",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Autentikasi sesuai ketentuan Modul Pertemuan 6
            if (username == password) {

                // Simpan status login dan data pengguna
                sharedPref.edit()
                    .putBoolean("isLogin", true)
                    .putString("username", username)
                    .putString("role", role)
                    .apply()

                // Pindah ke DashboardActivity
                val intent = Intent(
                    this,
                    DashboardActivity::class.java
                )

                intent.putExtra("username", username)
                intent.putExtra("role", role)

                startActivity(intent)
                finish()

            } else {

                // Jika username dan password tidak sama
                AlertDialog.Builder(this)
                    .setTitle("Login Gagal")
                    .setMessage(
                        "Username dan password tidak sesuai. Silakan coba lagi."
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}
