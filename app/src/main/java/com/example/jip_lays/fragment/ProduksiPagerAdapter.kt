package com.example.jip_lays.fragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProduksiPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProduksiRingkasanFragment()
            1 -> ProduksiDaftarFragment()
            2 -> ProduksiGiziFragment()
            else -> ProduksiRingkasanFragment()
        }
    }
}
