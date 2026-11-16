package com.example.jip_lays.fragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class KaryawanPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> KaryawanDaftarFragment()
            1 -> KaryawanAbsensiFragment()
            else -> KaryawanDaftarFragment()
        }
    }
}
