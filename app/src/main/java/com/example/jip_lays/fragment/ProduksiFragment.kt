package com.example.jip_lays.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jip_lays.databinding.FragmentProduksiBinding
import com.google.android.material.tabs.TabLayoutMediator

class ProduksiFragment : Fragment() {

    private var _binding: FragmentProduksiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProduksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup ViewPager2 and TabLayout (MODUL 11 Requirement)
        val adapter = ProduksiPagerAdapter(this)
        binding.viewPagerProduksi.adapter = adapter

        TabLayoutMediator(binding.tabLayoutProduksi, binding.viewPagerProduksi) { tab, position ->
            tab.text = when (position) {
                0 -> "Ringkasan"
                1 -> "Daftar Produksi"
                2 -> "Detail Gizi"
                else -> "Tab"
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
