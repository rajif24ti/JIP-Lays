package com.example.jip_lays

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jip_lays.databinding.FragmentLaporanBinding

class LaporanFragment : Fragment() {

    private var _binding: FragmentLaporanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLaporanBinding.inflate(
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

        // Contoh pengisian data
        binding.tvLaporanPorsi.text = "2.000"
        binding.tvLaporanKaryawan.text = "8"
        binding.tvLaporanSekolah.text = "5"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}