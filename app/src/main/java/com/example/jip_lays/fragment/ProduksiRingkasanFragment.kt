package com.example.jip_lays.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.jip_lays.data.repository.ProduksiRemoteRepository
import com.example.jip_lays.databinding.FragmentProduksiRingkasanBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ProduksiRingkasanFragment : Fragment() {

    private var _binding: FragmentProduksiRingkasanBinding? = null
    private val binding get() = _binding!!
    private val repository = ProduksiRemoteRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProduksiRingkasanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) calculateAndDisplaySummary()
    }

    private fun getToken(): String? {
        return requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
            .getString("access_token", null)
    }

    private fun calculateAndDisplaySummary() {
        val currentBinding = _binding ?: return

        lifecycleScope.launch {
            val result = repository.getProduksi(getToken())

            result.onSuccess { list ->
                val todaySdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                }
                val todayDateString = todaySdf.format(Date())

                val displayDateSdf = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID")).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                }
                val todayDisplay = displayDateSdf.format(Date())

                val todayItems = list.filter { item -> item.tanggal == todayDateString }

                val totalPorsi = todayItems.sumOf { it.porsi }
                val uniqueMenus = todayItems.map { it.menu.lowercase().trim() }.distinct().size

                val uniqueSchools = todayItems.flatMap { item ->
                    item.sekolahTujuan.orEmpty().map { it.sekolah }
                }.filter { it.isNotBlank() }.distinct().size

                currentBinding.tvRingkasanTotalPorsi.text = String.format(Locale.getDefault(), "%,d Porsi", totalPorsi).replace(',', '.')
                currentBinding.tvRingkasanTanggalInfo.text = "Data Produksi $todayDisplay (${todayItems.size} batch)"
                currentBinding.tvRingkasanTotalMenu.text = uniqueMenus.toString()
                currentBinding.tvRingkasanTotalSekolah.text = uniqueSchools.toString()
                currentBinding.tvRingkasanStatusInfo.text = if (todayItems.isEmpty()) {
                    "Belum ada produksi yang tercatat hari ini."
                } else {
                    val planned = todayItems.count { it.status == "Direncanakan" }
                    val inProgress = todayItems.count { it.status == "Dalam Proses" }
                    val completed = todayItems.count { it.status == "Selesai" }
                    val distributed = todayItems.count { it.status == "Didistribusikan" }
                    "Status operasional: $planned direncanakan, $inProgress dalam proses, $completed selesai, $distributed didistribusikan."
                }

            }.onFailure { exception ->
                currentBinding.tvRingkasanTotalPorsi.text = "0 Porsi"
                currentBinding.tvRingkasanTotalMenu.text = "0"
                currentBinding.tvRingkasanTotalSekolah.text = "0"
                currentBinding.tvRingkasanTanggalInfo.text = "Data produksi tidak dapat dimuat."
                currentBinding.tvRingkasanStatusInfo.text =
                    "Gagal memuat Supabase: ${exception.localizedMessage ?: exception.message}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
