package com.example.jip_lays.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jip_lays.R
import com.example.jip_lays.databinding.FragmentKotakSaranBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayoutMediator

data class SaranSekolah(
    val nama: String,
    val jenjang: String,
    val saran: String
)

object KotakSaranData {
    val daftarSekolah = listOf(
        SaranSekolah(
            "SD Negeri 37 Pekanbaru",
            "Sekolah Dasar (SD)",
            "Mohon variasi lauk dan sayuran dibuat lebih beragam agar anak-anak semakin semangat menikmati makanan."
        ),
        SaranSekolah(
            "SD Negeri 136 Pekanbaru",
            "Sekolah Dasar (SD)",
            "Porsi makanan untuk siswa kelas kecil dan kelas besar diharapkan dapat disesuaikan."
        ),
        SaranSekolah(
            "SMP Negeri 20 Pekanbaru",
            "Sekolah Menengah Pertama (SMP)",
            "Informasi jadwal pengantaran makanan akan sangat membantu sekolah menyiapkan pembagian."
        ),
        SaranSekolah(
            "SMP Negeri 21 Pekanbaru",
            "Sekolah Menengah Pertama (SMP)",
            "Mohon pilihan menu tetap memperhatikan keseimbangan lauk, sayuran, dan buah."
        ),
        SaranSekolah(
            "SMA Negeri 12 Pekanbaru",
            "Sekolah Menengah Atas (SMA)",
            "Kemasan makanan diharapkan tetap rapi dan menjaga suhu makanan sampai waktu makan."
        )
    )
}

class KotakSaranFragment : Fragment() {

    private var _binding: FragmentKotakSaranBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKotakSaranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPagerSaran.adapter = KotakSaranPagerAdapter(this)
        TabLayoutMediator(binding.tabSekolah, binding.viewPagerSaran) { tab, position ->
            tab.text = if (position == 0) "Kotak Saran" else "Sekolah"
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class KotakSaranPagerAdapter(fragment: Fragment) :
    androidx.viewpager2.adapter.FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> DaftarSaranFragment()
        1 -> DaftarSekolahFragment()
        else -> DaftarSaranFragment()
    }
}

class DaftarSaranFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = RecyclerView(requireContext()).apply {
        layoutManager = LinearLayoutManager(requireContext())
        setPadding(
            (16 * resources.displayMetrics.density).toInt(),
            (12 * resources.displayMetrics.density).toInt(),
            (16 * resources.displayMetrics.density).toInt(),
            (16 * resources.displayMetrics.density).toInt()
        )
        clipToPadding = false
        adapter = SaranAdapter(KotakSaranData.daftarSekolah)
    }
}

class DaftarSekolahFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = RecyclerView(requireContext()).apply {
        layoutManager = LinearLayoutManager(requireContext())
        setPadding(
            (16 * resources.displayMetrics.density).toInt(),
            (12 * resources.displayMetrics.density).toInt(),
            (16 * resources.displayMetrics.density).toInt(),
            (16 * resources.displayMetrics.density).toInt()
        )
        clipToPadding = false
        adapter = SekolahAdapter(KotakSaranData.daftarSekolah)
    }
}

private class SaranAdapter(
    private val daftarSaran: List<SaranSekolah>
) : RecyclerView.Adapter<SaranAdapter.ViewHolder>() {

    class ViewHolder(val card: MaterialCardView) : RecyclerView.ViewHolder(card)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val card = MaterialCardView(parent.context).apply {
            radius = 16f * resources.displayMetrics.density
            cardElevation = 2f * resources.displayMetrics.density
            strokeWidth = (1 * resources.displayMetrics.density).toInt()
            setStrokeColor(parent.context.getColor(R.color.border))
            setCardBackgroundColor(parent.context.getColor(R.color.white))
            val padding = (16 * resources.displayMetrics.density).toInt()
            setContentPadding(padding, padding, padding, padding)
        }
        return ViewHolder(card)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = daftarSaran[position]
        val context = holder.card.context
        val content = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
        }
        content.addView(TextView(context).apply {
            text = item.nama
            textSize = 16f
            setTextColor(context.getColor(R.color.primary_navy))
            setTypeface(null, android.graphics.Typeface.BOLD)
        })
        content.addView(TextView(context).apply {
            text = item.jenjang
            textSize = 12f
            setPadding(0, (4 * resources.displayMetrics.density).toInt(), 0, 0)
            setTextColor(context.getColor(R.color.primary_blue))
        })
        content.addView(TextView(context).apply {
            text = item.saran
            textSize = 14f
            setPadding(0, (12 * resources.displayMetrics.density).toInt(), 0, 0)
            setTextColor(context.getColor(R.color.text_body))
        })
        holder.card.removeAllViews()
        holder.card.addView(content)
        holder.card.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = (12 * context.resources.displayMetrics.density).toInt()
        }
    }

    override fun getItemCount(): Int = daftarSaran.size
}

private class SekolahAdapter(
    private val daftarSekolah: List<SaranSekolah>
) : RecyclerView.Adapter<SekolahAdapter.ViewHolder>() {

    class ViewHolder(val card: MaterialCardView) : RecyclerView.ViewHolder(card)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val card = MaterialCardView(parent.context).apply {
            radius = 14f * resources.displayMetrics.density
            cardElevation = 1f * resources.displayMetrics.density
            strokeWidth = (1 * resources.displayMetrics.density).toInt()
            setStrokeColor(parent.context.getColor(R.color.border))
            setCardBackgroundColor(parent.context.getColor(R.color.white))
            val padding = (16 * resources.displayMetrics.density).toInt()
            setContentPadding(padding, padding, padding, padding)
        }
        return ViewHolder(card)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = daftarSekolah[position]
        val context = holder.card.context
        val content = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
        }
        content.addView(TextView(context).apply {
            text = item.nama
            textSize = 15f
            setTextColor(context.getColor(R.color.primary_navy))
            setTypeface(null, android.graphics.Typeface.BOLD)
        })
        content.addView(TextView(context).apply {
            text = "${item.jenjang} • Panam, Pekanbaru"
            textSize = 12f
            setPadding(0, (5 * resources.displayMetrics.density).toInt(), 0, 0)
            setTextColor(context.getColor(R.color.text_body))
        })
        holder.card.removeAllViews()
        holder.card.addView(content)
        holder.card.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = (10 * context.resources.displayMetrics.density).toInt()
        }
    }

    override fun getItemCount(): Int = daftarSekolah.size
}
