package com.example.jip_lays.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.jip_lays.LoginActivity
import com.example.jip_lays.R
import com.example.jip_lays.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // MODUL 10 Requirement: Glide Image Loading
        Glide.with(this)
            .load(R.drawable.logo_apk)
            .circleCrop()
            .placeholder(R.drawable.ic_person)
            .into(binding.imgProfile)

        // Informasi developer
        binding.tvDeveloperName.text = "M. Rajif Dauta Abimanyu"
        binding.tvDeveloperRole.text = "Mahasiswa Teknologi Informasi"

        binding.tvAbout.text =
            "Saya adalah seorang yang suka aktif dalam " +
                    "berkegiatan di kampus maupun di luar kampus, " +
                    "gampang tidur susah bangun, saya juga penggemar " +
                    "fanatik Manchester United, dan pemain badminton " +
                    "yang terkuat saat ini di PCR."

        binding.tvHobby.text =
            "Olahraga, desain, dan fotografi/videografi."

        binding.tvStrength.text =
            "Mampu belajar hal baru, mudah beradaptasi, " +
                    "bisa memimpin suatu kelompok, dan memiliki " +
                    "kemampuan problem solving tersendiri."

        binding.tvInstagram.text = "Instagram: @mda_rajif"
        binding.tvGithub.text = "GitHub: github.com/rajif24ti"

        // Logout listener
        binding.btnLogoutProfile.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari akun?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya") { dialog, _ ->
                    val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
                    sharedPref.edit().putBoolean("isLogin", false).apply()
                    dialog.dismiss()

                    val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
