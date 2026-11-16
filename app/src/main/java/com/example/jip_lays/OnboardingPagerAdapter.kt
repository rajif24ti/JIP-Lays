package com.example.jip_lays

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val pages = listOf(
        Triple(
            "Kelola Operasional Dapur",
            "Selamat datang di JIP-Lays SIMOD MBG. Aplikasi terbaik untuk membantu pengelolaan operasional dapur MBG secara efisien dan terstruktur.",
            R.drawable.ic_onboarding_1
        ),
        Triple(
            "Pantau Karyawan & Produksi",
            "Kelola kehadiran karyawan, absensi masuk & pulang, serta pantau status produksi makanan harian per sekolah penerima secara real-time.",
            R.drawable.ic_onboarding_2
        ),
        Triple(
            "Laporan & Bukti Kerusakan",
            "Catat laporan kendala peralatan dapur dengan bukti foto kamera atau galeri, lalu ekspor laporan operasional ke format dokumen PDF resmi.",
            R.drawable.ic_onboarding_3
        )
    )

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment {
        val page = pages[position]
        return OnboardingPageFragment.newInstance(page.first, page.second, page.third)
    }
}
