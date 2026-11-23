package com.example.jip_lays

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.jip_lays.data.repository.AuthRepository
import com.example.jip_lays.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        // Pilihan role pengguna
        val roles = arrayOf(
            "Penanggung Jawab Dapur",
            "Ahli Gizi",
            "Penanggung Jawab Sekolah"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        binding.actRole.setAdapter(adapter)
        binding.actRole.setOnClickListener { binding.actRole.showDropDown() }
        binding.actRole.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) binding.actRole.showDropDown() }

        binding.btnKeRegistrasi.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Tombol Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val role = binding.actRole.text.toString().trim()

            if (email.isEmpty()) {
                binding.etUsername.error = "Email harus diisi"
                binding.etUsername.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPassword.error = "Password harus diisi"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            if (role.isEmpty()) {
                Toast.makeText(this, "Silakan pilih role", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Supabase Auth Login
            binding.btnLogin.isEnabled = false
            binding.btnLogin.text = "Memuat..."

            lifecycleScope.launch {
                val result = authRepository.login(email, password)

                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = "Masuk"

                result.onSuccess { tokenResponse ->
                    val userMeta = tokenResponse.user?.userMetadata
                    val userRole = userMeta?.role ?: role
                    val userName = userMeta?.nama ?: email.substringBefore("@")

                    sharedPref.edit()
                        .putBoolean("isLogin", true)
                        .putString("access_token", tokenResponse.accessToken)
                        .putString("username", userName)
                        .putString("email", email)
                        .putString("role", userRole)
                        .apply()

                    Toast.makeText(this@LoginActivity, "Login Supabase Berhasil", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@LoginActivity, BaseActivity::class.java).apply {
                        putExtra("username", userName)
                        putExtra("role", userRole)
                    }
                    startActivity(intent)
                    finish()

                }.onFailure { exception ->
                    // Fallback to local admin login if offline or demo
                    val savedUsername = sharedPref.getString("username", "")
                    val savedPassword = sharedPref.getString("password", "")

                    if ((email == "admin" && password == "admin") || (email == savedUsername && password == savedPassword)) {
                        sharedPref.edit()
                            .putBoolean("isLogin", true)
                            .putString("username", email)
                            .putString("role", role)
                            .apply()

                        Toast.makeText(this@LoginActivity, "Login berhasil (Local)", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, BaseActivity::class.java).apply {
                            putExtra("username", email)
                            putExtra("role", role)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        AlertDialog.Builder(this@LoginActivity)
                            .setTitle("Login Gagal")
                            .setMessage("Gagal masuk ke Supabase: ${exception.localizedMessage ?: exception.message}")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
            }
        }
    }
}
