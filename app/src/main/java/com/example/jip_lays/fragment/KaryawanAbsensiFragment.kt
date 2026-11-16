package com.example.jip_lays.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jip_lays.databinding.FragmentKaryawanAbsensiBinding

class KaryawanAbsensiFragment : Fragment() {

    private var _binding: FragmentKaryawanAbsensiBinding? = null
    private val binding get() = _binding!!
    private var selectedNama: String? = null

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

        binding.tvTanggal.text = KaryawanRepository.tanggalHariIni()
        binding.actNamaKaryawan.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                KaryawanRepository.daftarKaryawan.map { it.nama }
            )
        )
        binding.actNamaKaryawan.setOnClickListener { binding.actNamaKaryawan.showDropDown() }
        binding.actNamaKaryawan.setOnItemClickListener { _, _, position, _ ->
            val karyawan = KaryawanRepository.daftarKaryawan[position]
            selectedNama = karyawan.nama
            binding.actDivisi.setText(karyawan.divisi, false)
            tampilkanAbsensi()
        }

        binding.actDivisi.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                KaryawanRepository.daftarDivisi
            )
        )
        binding.actDivisi.setOnClickListener { binding.actDivisi.showDropDown() }
        binding.actDivisi.setOnItemClickListener { _, _, _, _ -> validasiDivisiTerpilih() }

        binding.btnCatatMasuk.setOnClickListener { catatAbsensi(isMasuk = true) }
        binding.btnCatatPulang.setOnClickListener { catatAbsensi(isMasuk = false) }
        tampilkanAbsensi()
    }

    private fun validasiDivisiTerpilih() {
        val nama = selectedNama ?: return
        val karyawan = KaryawanRepository.daftarKaryawan.firstOrNull { it.nama == nama } ?: return
        if (binding.actDivisi.text.toString() != karyawan.divisi) {
            binding.actDivisi.setText(karyawan.divisi, false)
            Toast.makeText(requireContext(), "Divisi mengikuti data karyawan terpilih", Toast.LENGTH_SHORT).show()
        }
    }

    private fun tampilkanAbsensi() {
        val currentBinding = _binding ?: return
        val nama = selectedNama
        currentBinding.tvJamMasuk.text = "Jam masuk: —"
        currentBinding.tvJamPulang.text = "Jam pulang: —"
        if (nama == null) return

        val attendance = KaryawanRepository.absensiHariIni(requireContext(), nama)
        currentBinding.tvJamMasuk.text = "Jam masuk: ${attendance.jamMasuk ?: "—"}"
        currentBinding.tvJamPulang.text = "Jam pulang: ${attendance.jamPulang ?: "—"}"
    }

    private fun catatAbsensi(isMasuk: Boolean) {
        val nama = selectedNama
        val karyawan = KaryawanRepository.daftarKaryawan.firstOrNull { it.nama == nama }
        if (nama == null || karyawan == null || binding.actNamaKaryawan.text.toString() != nama) {
            Toast.makeText(requireContext(), "Pilih nama karyawan dari daftar", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.actDivisi.text.toString() != karyawan.divisi) {
            Toast.makeText(requireContext(), "Pilih divisi yang sesuai dengan karyawan", Toast.LENGTH_SHORT).show()
            return
        }
        if (!isMasuk && KaryawanRepository.absensiHariIni(requireContext(), nama).jamMasuk == null) {
            Toast.makeText(requireContext(), "Catat jam masuk terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val berhasil = if (isMasuk) {
            KaryawanRepository.catatJamMasuk(requireContext(), nama)
        } else {
            KaryawanRepository.catatJamPulang(requireContext(), nama)
        }
        if (berhasil) {
            tampilkanAbsensi()
            val keterangan = if (isMasuk) "Jam masuk" else "Jam pulang"
            Toast.makeText(
                requireContext(),
                "$keterangan tercatat pukul ${KaryawanRepository.waktuSekarang()}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val keterangan = if (isMasuk) "Jam masuk" else "Jam pulang"
            Toast.makeText(requireContext(), "$keterangan hari ini sudah tercatat", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
