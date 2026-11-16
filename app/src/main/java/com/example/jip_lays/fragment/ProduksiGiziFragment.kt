package com.example.jip_lays.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.jip_lays.databinding.FragmentProduksiGiziBinding

class ProduksiGiziFragment : Fragment() {

    private var _binding: FragmentProduksiGiziBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProduksiGiziBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewMenuGizi.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewMenuGizi.adapter = MenuGiziAdapter(
            listOf(
                MenuGizi(
                    nama = "Opor Ayam",
                    gambar = com.example.jip_lays.R.drawable.oporayam,
                    kalori = "320 kcal",
                    protein = "24 g",
                    lemak = "22 g",
                    karbohidrat = "8 g",
                    serat = "1 g"
                ),
                MenuGizi(
                    nama = "Ayam Gulai",
                    gambar = com.example.jip_lays.R.drawable.gulaiayam,
                    kalori = "290 kcal",
                    protein = "25 g",
                    lemak = "19 g",
                    karbohidrat = "6 g",
                    serat = "1 g"
                ),
                MenuGizi(
                    nama = "Ikan Bakar",
                    gambar = com.example.jip_lays.R.drawable.ikanbakar,
                    kalori = "210 kcal",
                    protein = "27 g",
                    lemak = "10 g",
                    karbohidrat = "2 g",
                    serat = "0 g"
                ),
                MenuGizi(
                    nama = "Telur Barendo",
                    gambar = com.example.jip_lays.R.drawable.telorbarendo,
                    kalori = "260 kcal",
                    protein = "14 g",
                    lemak = "20 g",
                    karbohidrat = "8 g",
                    serat = "1 g"
                ),
                MenuGizi(
                    nama = "Ayam Goreng",
                    gambar = com.example.jip_lays.R.drawable.ayamgoreng,
                    kalori = "280 kcal",
                    protein = "26 g",
                    lemak = "18 g",
                    karbohidrat = "5 g",
                    serat = "0 g"
                )
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
