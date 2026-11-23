package com.example.jip_lays.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.jip_lays.R
import com.example.jip_lays.data.model.ProduksiModel
import com.example.jip_lays.data.repository.ProduksiRemoteRepository
import com.example.jip_lays.databinding.FragmentProduksiGiziBinding
import kotlinx.coroutines.launch

class ProduksiGiziFragment : Fragment() {

    private var _binding: FragmentProduksiGiziBinding? = null
    private val binding get() = _binding!!
    private val repository = ProduksiRemoteRepository()
    private var productionList = mutableListOf<ProduksiModel>()

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
        binding.cardGiziDetail.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) loadMenuGiziFromSupabase()
    }

    private fun getToken(): String? {
        return requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
            .getString("access_token", null)
    }

    private fun loadMenuGiziFromSupabase() {
        val currentBinding = _binding ?: return

        lifecycleScope.launch {
            val result = repository.getProduksi(getToken())

            result.onSuccess { data ->
                productionList.clear()
                productionList.addAll(data)

                if (productionList.isNotEmpty()) {
                    val menuTitles = productionList.map { "${it.menu} (${it.tanggal})" }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, menuTitles)
                    currentBinding.actPilihMenuGizi.setAdapter(adapter)
                    currentBinding.actPilihMenuGizi.setOnClickListener { currentBinding.actPilihMenuGizi.showDropDown() }

                    currentBinding.actPilihMenuGizi.setOnItemClickListener { _, _, position, _ ->
                        displayGiziDetail(productionList[position])
                    }

                    // Display first menu by default
                    currentBinding.actPilihMenuGizi.setText(menuTitles[0], false)
                    displayGiziDetail(productionList[0])
                    currentBinding.cardGiziDetail.visibility = View.VISIBLE
                } else {
                    currentBinding.actPilihMenuGizi.setText("", false)
                    currentBinding.cardGiziDetail.visibility = View.GONE
                }
            }.onFailure {
                currentBinding.cardGiziDetail.visibility = View.GONE
                android.widget.Toast.makeText(
                    requireContext(),
                    "Gagal memuat detail gizi: ${it.localizedMessage ?: it.message}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayGiziDetail(item: ProduksiModel) {
        val currentBinding = _binding ?: return

        currentBinding.tvGiziMenuTitle.text = item.menu
        currentBinding.tvGiziMenuTanggal.text = "Tanggal: ${item.tanggal} • Total: ${item.porsi} Porsi"
        currentBinding.tvGiziSekolah.text = item.sekolahTujuan.orEmpty()
            .joinToString("\n") { "${it.sekolah}: ${it.porsi} porsi" }
            .ifBlank { "Belum ada sekolah penerima" }

        val gizi = item.gizi
        currentBinding.tvGiziEnergi.text = "Energi:\n${gizi?.energi?.let { "$it kkal" } ?: "—"}"
        currentBinding.tvGiziProtein.text = "Protein:\n${gizi?.protein?.let { "$it g" } ?: "—"}"
        currentBinding.tvGiziKarbo.text = "Karbohidrat:\n${gizi?.karbohidrat?.let { "$it g" } ?: "—"}"
        currentBinding.tvGiziLemak.text = "Lemak:\n${gizi?.lemak?.let { "$it g" } ?: "—"}"
        currentBinding.tvGiziSerat.text = "Serat:\n${gizi?.serat?.let { "$it g" } ?: "—"}"
        currentBinding.tvGiziKalsium.text = "Kalsium:\n${gizi?.kalsium?.let { "$it mg" } ?: "—"}"
        currentBinding.tvGiziZatBesi.text = "Zat Besi: ${gizi?.zatBesi?.let { "$it mg" } ?: "—"}"

        if (!item.gambarUrl.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(item.gambarUrl)
                .placeholder(R.drawable.lays)
                .error(R.drawable.lays)
                .into(currentBinding.imgGiziPreview)
        } else {
            currentBinding.imgGiziPreview.setImageResource(R.drawable.lays)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
