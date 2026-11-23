package com.example.jip_lays.data.model

import com.google.gson.Gson
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SupabaseModelMappingTest {

    private val gson = Gson()

    @Test
    fun productionMapsLiveSupabaseColumnNamesAndSchoolAllocations() {
        val json = """
            {
              "id": 7,
              "nama_menu": "Menu contoh",
              "gambar_url": null,
              "tanggal_produksi": "2026-11-23",
              "jumlah_porsi": 120,
              "status": "Direncanakan",
              "detail_gizi": {"energi": 450, "catatan": "Uji"},
              "sekolah_tujuan": [{"nama_sekolah": "Sekolah contoh", "jumlah_porsi": 120}]
            }
        """.trimIndent()

        val production = gson.fromJson(json, ProduksiModel::class.java)

        assertEquals("Menu contoh", production.menu)
        assertEquals("2026-11-23", production.tanggal)
        assertEquals(120, production.porsi)
        assertEquals(450.0, production.gizi?.energi)
        assertEquals("Uji", production.gizi?.catatan)
        assertEquals("Sekolah contoh", production.sekolahTujuan?.single()?.sekolah)
        assertEquals(120, production.sekolahTujuan?.single()?.porsi)
    }

    @Test
    fun productionWritesCanonicalSupabaseColumnNames() {
        val production = ProduksiModel(
            menu = "Menu contoh",
            gambarUrl = null,
            tanggal = "2026-11-23",
            porsi = 120,
            status = "Direncanakan",
            gizi = NutrisiModel(catatan = "Uji"),
            sekolahTujuan = listOf(SekolahPorsiModel("Sekolah contoh", 120))
        )

        val json = JsonParser.parseString(gson.toJson(production)).asJsonObject
        val school = json.getAsJsonArray("sekolah_tujuan").single().asJsonObject

        assertTrue(json.has("nama_menu"))
        assertTrue(json.has("tanggal_produksi"))
        assertTrue(json.has("jumlah_porsi"))
        assertTrue(json.has("detail_gizi"))
        assertFalse(json.has("menu"))
        assertFalse(json.has("tanggal"))
        assertFalse(json.has("porsi"))
        assertEquals("Sekolah contoh", school.get("nama_sekolah").asString)
        assertEquals(120, school.get("jumlah_porsi").asInt)
    }

    @Test
    fun employeeMapsLiveContactAndStatusColumnNames() {
        val json = """
            {
              "id": 3,
              "nama": "Karyawan contoh",
              "divisi": "Divisi Produksi",
              "no_telepon": "081234567890",
              "status": "Aktif",
              "created_at": "2026-11-23T00:00:00Z",
              "updated_at": "2026-11-23T00:00:00Z"
            }
        """.trimIndent()

        val employee = gson.fromJson(json, KaryawanModel::class.java)

        assertEquals("081234567890", employee.telepon)
        assertEquals("Aktif", employee.statusKeaktifan)
        assertEquals("2026-11-23T00:00:00Z", employee.updatedAt)
    }
}
