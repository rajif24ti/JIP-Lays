package com.example.jip_lays.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.jip_lays.data.model.KaryawanModel
import com.example.jip_lays.data.repository.KaryawanRemoteRepository
import com.example.jip_lays.databinding.FragmentKaryawanAbsensiBinding
import kotlinx.coroutines.launch

class KaryawanAbsensiFragment : Fragment() {

    private var _binding: FragmentKaryawanAbsensiBinding? = null
    private val binding get() = _binding!!
    private val repository = KaryawanRemoteRepository()
    private var activeEmployees: List<KaryawanModel> = emptyList()
    private var selectedKaryawan: KaryawanModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKaryawanAbsensiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentBinding = _binding ?: return
        currentBinding.tvTanggal.text = KaryawanRepository.tanggalHariIni()
        currentBinding.actDivisi.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                KaryawanRepository.daftarDivisi
            )
        )
        currentBinding.actNamaKaryawan.setOnClickListener { currentBinding.actNamaKaryawan.showDropDown() }
        currentBinding.actNamaKaryawan.setOnItemClickListener { _, _, position, _ ->
            selectedKaryawan = activeEmployees.getOrNull(position)
            currentBinding.actDivisi.setText(selectedKaryawan?.divisi.orEmpty(), false)
            tampilkanAbsensi()
        }
        currentBinding.actDivisi.setOnClickListener { currentBinding.actDivisi.showDropDown() }
        currentBinding.actDivisi.setOnItemClickListener { _, _, _, _ -> validasiDivisiTerpilih() }
        currentBinding.btnCatatMasuk.setOnClickListener { catatAbsensi(isMasuk = true) }
        currentBinding.btnCatatPulang.setOnClickListener { catatAbsensi(isMasuk = false) }
        loadEmployees()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) loadEmployees()
    }

    private fun loadEmployees() {
        val currentBinding = _binding ?: return
        val token = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
            .getString("access_token", null)
        lifecycleScope.launch {
            repository.getKaryawan(token).onSuccess { rows ->
                activeEmployees = rows.filter { it.statusKeaktifan.equals("Aktif", ignoreCase = true) }
                currentBinding.actNamaKaryawan.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        activeEmployees.map { it.nama }
                    )
                )
                selectedKaryawan = selectedKaryawan?.let { selected ->
                    activeEmployees.firstOrNull { it.id == selected.id }
                }
                if (selectedKaryawan == null) {
                    currentBinding.actNamaKaryawan.setText("", false)
                    currentBinding.actDivisi.setText("", false)
                }
                tampilkanAbsensi()
            }.onFailure {
                Toast.makeText(
                    requireContext(),
                    "Gagal memuat karyawan: ${it.localizedMessage ?: it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun validasiDivisiTerpilih() {
        val selected = selectedKaryawan ?: return
        val currentBinding = _binding ?: return
        if (currentBinding.actDivisi.text.toString() != selected.divisi) {
            currentBinding.actDivisi.setText(selected.divisi, false)
            Toast.makeText(requireContext(), "Divisi mengikuti data karyawan terpilih", Toast.LENGTH_SHORT).show()
        }
    }

    private fun tampilkanAbsensi() {
        val currentBinding = _binding ?: return
        currentBinding.tvJamMasuk.text = "Jam masuk: —"
        currentBinding.tvJamPulang.text = "Jam pulang: —"
        val selected = selectedKaryawan ?: return
        val attendance = KaryawanRepository.absensiHariIni(requireContext(), attendanceKey(selected))
        currentBinding.tvJamMasuk.text = "Jam masuk: ${attendance.jamMasuk ?: "—"}"
        currentBinding.tvJamPulang.text = "Jam pulang: ${attendance.jamPulang ?: "—"}"
    }

    private fun catatAbsensi(isMasuk: Boolean) {
        val currentBinding = _binding ?: return
        val selected = selectedKaryawan
        if (selected == null || currentBinding.actNamaKaryawan.text.toString() != selected.nama) {
            Toast.makeText(requireContext(), "Pilih nama karyawan dari daftar", Toast.LENGTH_SHORT).show()
            return
        }
        if (currentBinding.actDivisi.text.toString() != selected.divisi) {
            Toast.makeText(requireContext(), "Pilih divisi yang sesuai dengan karyawan", Toast.LENGTH_SHORT).show()
            return
        }
        val key = attendanceKey(selected)
        if (!isMasuk && KaryawanRepository.absensiHariIni(requireContext(), key).jamMasuk == null) {
            Toast.makeText(requireContext(), "Catat jam masuk terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        val saved = if (isMasuk) {
            KaryawanRepository.catatJamMasuk(requireContext(), key)
        } else {
            KaryawanRepository.catatJamPulang(requireContext(), key)
        }
        if (saved) {
            tampilkanAbsensi()
            val label = if (isMasuk) "Jam masuk" else "Jam pulang"
            Toast.makeText(
                requireContext(),
                "$label tercatat pukul ${KaryawanRepository.waktuSekarang()}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val label = if (isMasuk) "Jam masuk" else "Jam pulang"
            Toast.makeText(requireContext(), "$label hari ini sudah tercatat", Toast.LENGTH_SHORT).show()
        }
    }

    private fun attendanceKey(karyawan: KaryawanModel): String = karyawan.id?.toString() ?: karyawan.nama

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
