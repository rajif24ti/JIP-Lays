package com.example.jip_lays

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jip_lays.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengambil data pengguna dari Activity
        val role = requireActivity().intent
            .getStringExtra("role") ?: "Penanggung Jawab Dapur"

        // Menampilkan role pengguna
        binding.tvWelcome.text = role

        // Tombol logout
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari akun?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya") { dialog, _ ->

                    // Menghapus sesi login
                    val sharedPref = requireContext()
                        .getSharedPreferences(
                            "user_pref",
                            android.content.Context.MODE_PRIVATE
                        )

                    sharedPref.edit()
                        .clear()
                        .apply()

                    dialog.dismiss()

                    // Kembali ke LoginActivity
                    val logoutIntent = Intent(
                        requireContext(),
                        LoginActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }

                    startActivity(logoutIntent)
                }
                .show()
        }

        // Menu cepat: Absensi
        binding.cardAbsensi.setOnClickListener {
            startActivity(
                Intent(requireContext(), AbsensiActivity::class.java)
            )
        }

        // Menu cepat: Gizi
        binding.cardGizi.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Menu Gizi dipilih",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
