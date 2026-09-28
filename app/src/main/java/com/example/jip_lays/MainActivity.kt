package com.example.jip_lays
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.jip_lays.MainActivityResult
import com.example.jip_lays.R
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.jip_lays.databinding.ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Inisialisasi komponen
        val btnKirim: Button = findViewById(R.id.btnKirim)
        binding.btnKirim.setOnClickListener {
            //Mengambil value dari inputNama dan menampilkan di Logcat
            val intent = Intent(this, MainActivityResult::class.java)
            startActivity(intent)
            Toast.makeText(this, "Anda berhasil login", Toast.LENGTH_SHORT).show()
        }
    }
}