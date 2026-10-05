package com.example.jip_lays

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jip_lays.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            val username = binding.etUsername
                .text
                .toString()
                .trim()

            val password = binding.etPassword
                .text
                .toString()
                .trim()

            val role = binding.actRole
                .text
                .toString()
                .trim()

            if (username.isEmpty()) {

                binding.etUsername.error = "Username harus diisi"
                binding.etUsername.requestFocus()

            } else if (password.isEmpty()) {

                binding.etPassword.error = "Password harus diisi"
                binding.etPassword.requestFocus()

            } else if (role.isEmpty()) {

                Toast.makeText(
                    this,
                    "Silakan pilih role",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val intent = Intent(
                    this,
                    DashboardActivity::class.java
                )

                intent.putExtra("username", username)
                intent.putExtra("role", role)

                startActivity(intent)

                finish()
            }
        }
    }
}