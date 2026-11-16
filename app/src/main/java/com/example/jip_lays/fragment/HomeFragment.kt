package com.example.jip_lays.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.jip_lays.BaseActivity
import com.example.jip_lays.LoginActivity
import com.example.jip_lays.MenuGridAdapter
import com.example.jip_lays.MenuGridModel
import com.example.jip_lays.R
import com.example.jip_lays.WebViewActivity
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

        // Setup RecyclerView dengan GridLayoutManager (MODUL 11 Requirement)
        setupOperationalMenuGrid()

        // Tombol logout
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari akun?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya") { dialog, _ ->

                    val sharedPref = requireContext()
                        .getSharedPreferences(
                            "user_pref",
                            android.content.Context.MODE_PRIVATE
                        )

                    sharedPref.edit()
                        .putBoolean("isLogin", false)
                        .apply()

                    dialog.dismiss()

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
    }

    private fun setupOperationalMenuGrid() {
        val menuItems = listOf(
            MenuGridModel(
                "Absensi Karyawan",
                "Pencatatan absensi masuk & pulang karyawan.",
                R.drawable.ic_karyawan,
                "ABSENSI"
            ),
            MenuGridModel(
                "Menu Gizi & Produksi",
                "Pemantauan status produksi & menu harian.",
                R.drawable.ic_gizi,
                "PRODUKSI"
            ),
            MenuGridModel(
                "Laporan Kerusakan",
                "Form bukti foto kerusakan & ekspor PDF.",
                R.drawable.ic_laporan,
                "LAPORAN"
            ),
            MenuGridModel(
                "Portal Web MBG",
                "Akses informasi resmi portal MBG.",
                R.drawable.ic_language,
                "WEBSITE"
            )
        )

        val adapter = MenuGridAdapter(menuItems) { item ->
            when (item.actionType) {
                "ABSENSI" -> {
                    (activity as? BaseActivity)?.selectBottomMenuItem(R.id.menu_karyawan)
                }
                "PRODUKSI" -> {
                    (activity as? BaseActivity)?.selectBottomMenuItem(R.id.menu_produksi)
                }
                "LAPORAN" -> {
                    (activity as? BaseActivity)?.selectBottomMenuItem(R.id.menu_laporan)
                }
                "WEBSITE" -> {
                    startActivity(Intent(requireContext(), WebViewActivity::class.java))
                }
            }
        }

        binding.rvMenuGrid.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvMenuGrid.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
