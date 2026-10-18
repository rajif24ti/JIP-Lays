package com.example.jip_lays

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jip_lays.databinding.ActivityVerificationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class VerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nama = intent.getStringExtra("nama").orEmpty()
        val handphone = intent.getStringExtra("handphone").orEmpty()
        val username = intent.getStringExtra("username").orEmpty()
        val password = intent.getStringExtra("password").orEmpty()
        val role = intent.getStringExtra("role").orEmpty()

        binding.btnVerify.setOnClickListener {
            val otp = binding.etOtp.text.toString().trim()

            if (otp.isEmpty() || otp != handphone) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Verifikasi Gagal")
                    .setMessage(
                        if (otp.isEmpty()) {
                            "Kode OTP wajib diisi."
                        } else {
                            "Kode OTP tidak sesuai dengan nomor handphone."
                        }
                    )
                    .setPositiveButton("Coba Lagi", null)
                    .show()

                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences(
                "user_pref",
                MODE_PRIVATE
            )

            sharedPref.edit()
                .putString("nama", nama)
                .putString("handphone", handphone)
                .putString("username", username)
                .putString("password", password)
                .putString("role", role)
                .putBoolean("isRegistered", true)
                .apply()

            MaterialAlertDialogBuilder(this)
                .setTitle("Registrasi Berhasil")
                .setMessage("Akun berhasil dibuat. Silakan login.")
                .setCancelable(false)
                .setPositiveButton("Ke Login") { _, _ ->
                    val loginIntent = Intent(
                        this,
                        LoginActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }

                    startActivity(loginIntent)
                    finish()
                }
                .show()
        }
    }
}
