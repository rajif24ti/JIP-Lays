
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

        // Pilihan role pengguna
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

        binding.btnKeRegistrasi.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }

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

            val savedUsername =
                sharedPref.getString("username", "")

            val savedPassword =
                sharedPref.getString("password", "")

            val savedRole =
                sharedPref.getString("role", "")

            val loginValid = username == password ||
                    (
                            username == savedUsername &&
                                    password == savedPassword
                            )

            if (loginValid) {

                // Simpan status dan data sesi pengguna
                sharedPref.edit()
                    .putBoolean("isLogin", true)
                    .putString("username", username)
                    .putString(
                        "role",
                        if (
                            username == savedUsername &&
                            password == savedPassword &&
                            !savedRole.isNullOrEmpty()
                        ) {
                            savedRole
                        } else {
                            role
                        }
                    )
                    .apply()

                val roleLogin =
                    sharedPref.getString("role", role) ?: role

                Toast.makeText(
                    this,
                    "Login berhasil",
                    Toast.LENGTH_SHORT
                ).show()

                // Arahkan ke BaseActivity yang memiliki Bottom Navigation
                val intent = Intent(
                    this,
                    BaseActivity::class.java
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
