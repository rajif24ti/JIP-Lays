package com.example.jip_lays

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jip_lays.databinding.FragmentProduksiBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProduksiFragment : Fragment() {

    private var _binding: FragmentProduksiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProduksiBinding.inflate(
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

        // Menampilkan tanggal hari ini
        val tanggal = SimpleDateFormat(
            "dd MMMM yyyy",
            Locale("id", "ID")
        ).format(Date())

        binding.tvTanggalProduksi.text = tanggal

        // Data ringkasan produksi contoh
        binding.tvTotalPorsi.text = "2.000"
        binding.tvTotalMenu.text = "3"
        binding.tvTotalSekolah.text = "5"

        // Data menu produksi contoh
        binding.tvMenuPertama.text = "Nasi, ayam, sayur, dan buah"
        binding.tvSekolahPertama.text = "SD Negeri 001"
        binding.tvPorsiPertama.text = "400 porsi"
        binding.tvStatusPertama.text = "Selesai"

        binding.tvMenuKedua.text = "Nasi, ikan, sayur, dan buah"
        binding.tvSekolahKedua.text = "SMP Negeri 001"
        binding.tvPorsiKedua.text = "350 porsi"
        binding.tvStatusKedua.text = "Diproses"

        binding.tvMenuKetiga.text = "Nasi, telur, sayur, dan buah"
        binding.tvSekolahKetiga.text = "SD Negeri 002"
        binding.tvPorsiKetiga.text = "300 porsi"
        binding.tvStatusKetiga.text = "Menunggu"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
