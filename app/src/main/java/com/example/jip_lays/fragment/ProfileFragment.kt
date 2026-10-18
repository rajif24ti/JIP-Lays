package com.example.jip_lays

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jip_lays.databinding.FragmentProfileBinding

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
