package com.example.jip_lays.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jip_lays.databinding.FragmentKaryawanBinding
import com.google.android.material.tabs.TabLayoutMediator

class KaryawanFragment : Fragment() {

    private var _binding: FragmentKaryawanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKaryawanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val role = requireActivity()
            .getSharedPreferences("user_pref", android.content.Context.MODE_PRIVATE)
            .getString("role", "Penanggung Jawab Dapur")
        if (role != "Penanggung Jawab Dapur") {
            Toast.makeText(
                requireContext(),
                "Akses terbatas: Menu Karyawan dikelola Penanggung Jawab Dapur",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.viewPagerKaryawan.adapter = KaryawanPagerAdapter(this)
        TabLayoutMediator(binding.tabLayoutKaryawan, binding.viewPagerKaryawan) { tab, position ->
            tab.text = when (position) {
                0 -> "Karyawan"
                1 -> "Absensi"
                else -> "Karyawan"
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
