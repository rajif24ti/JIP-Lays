# DOKUMENTASI ARSITEKTUR & ALUR DIAGRAM TLDRAW
## Project: JIP-Lays (SIMOD MBG - Integrasi Supabase REST API)

Dokumen ini dirancang untuk mempermudah pemetaan visual diagram pada tldraw.com.

---

## 1. KOMPONEN UTAMA DIAGRAM

### A. UI Layer (Android Client)
1. **RegisterActivity**: Input Email, Password, Nama, Handphone, Role.
2. **LoginActivity**: Input Email, Password, Role.
3. **Fragment Karyawan (`KaryawanDaftarFragment`)**: Form Tambah/Edit Karyawan, List Karyawan, Tombol Hapus.
4. **Fragment Produksi (`ProduksiDaftarFragment`)**: Form Tambah/Edit Produksi, List Produksi per Sekolah, Tombol Hapus.

### B. Network & Data Layer
1. **Models**:
   - `AuthModels` (`SignUpRequest`, `TokenRequest`, `TokenResponse`, `UserMetadata`)
   - `UserModel` (`id`, `email`, `nama`, `handphone`, `role`)
   - `KaryawanModel` (`id`, `nama`, `divisi`)
   - `ProduksiModel` (`id`, `sekolah`, `menu`, `porsi`, `status`)
2. **Retrofit API Services**:
   - `AuthService` (`/auth/v1/signup`, `/auth/v1/token?grant_type=password`)
   - `UserService` (`/rest/v1/users`)
   - `KaryawanService` (`/rest/v1/karyawan`)
   - `ProduksiService` (`/rest/v1/produksi`)
3. **Repositories**:
   - `AuthRepository`
   - `KaryawanRemoteRepository`
   - `ProduksiRemoteRepository`

### C. Supabase Backend (Schema: `mobile`)
1. **Supabase Auth**: Layanan autentikasi pengguna (`/auth/v1`).
2. **Database Trigger**: `mobile.handle_new_user()` (Otomatis menyimpan profil baru ke `mobile.users`).
3. **Tabel Database**:
   - `mobile.users`: Menyimpan profil pengguna.
   - `mobile.karyawan`: Menyimpan data karyawan.
   - `mobile.produksi`: Menyimpan data produksi harian sekolah.

---

## 2. PEMETAAN ALUR KONEKSI (RELASI DIAGRAM TLDRAW)

### Alur 1: Registrasi Pengguna
```
[RegisterActivity]
       │
       ▼ (Input Email, Password, Nama, Handphone, Role)
[AuthRepository.signUp()]
       │
       ▼ (POST /auth/v1/signup dengan Header apikey)
[Supabase Auth]
       │
       ├─► (Simpan Akun Kredensial Email & Password)
       └─► [Trigger PostgreSQL: mobile.handle_new_user()]
                  │
                  ▼ (Insert Profil Metadata)
            [Tabel: mobile.users]
```

### Alur 2: Autentikasi Login
```
[LoginActivity]
       │
       ▼ (Input Email & Password)
[AuthRepository.login()]
       │
       ▼ (POST /auth/v1/token?grant_type=password)
[Supabase Auth]
       │
       ▼ (Return Access Token & User Metadata)
[SharedPreferences: user_pref] ──► (Simpan access_token, email, role)
       │
       ▼
[BaseActivity]
```

### Alur 3: CRUD Karyawan
```
[KaryawanDaftarFragment]
       │
       ├─► READ   : GET /rest/v1/karyawan (Header Accept-Profile: mobile)
       ├─► CREATE : POST /rest/v1/karyawan (Header Content-Profile: mobile)
       ├─► UPDATE : PATCH /rest/v1/karyawan?id=eq.ID (Header Content-Profile: mobile)
       └─► DELETE : DELETE /rest/v1/karyawan?id=eq.ID (Header Content-Profile: mobile)
       │
       ▼
[KaryawanRemoteRepository] ──► [KaryawanService] ──► [Tabel: mobile.karyawan]
```

### Alur 4: CRUD Produksi
```
[ProduksiDaftarFragment]
       │
       ├─► READ   : GET /rest/v1/produksi (Header Accept-Profile: mobile)
       ├─► CREATE : POST /rest/v1/produksi (Header Content-Profile: mobile)
       ├─► UPDATE : PATCH /rest/v1/produksi?id=eq.ID (Header Content-Profile: mobile)
       └─► DELETE : DELETE /rest/v1/produksi?id=eq.ID (Header Content-Profile: mobile)
       │
       ▼
[ProduksiRemoteRepository] ──► [ProduksiService] ──► [Tabel: mobile.produksi]
```

---

## 3. INSTRUKSI TAMPILAN KOTAK DI TLDRAW

1. **Kotak Warna Biru Muda (UI Client)**:
   - Group `Android JIP-Lays`: `RegisterActivity`, `LoginActivity`, `Fragment Karyawan`, `Fragment Produksi`.
2. **Kotak Warna Hijau (Data Layer)**:
   - Group `Network & Repositories`: `SupabaseClient`, `AuthRepository`, `KaryawanRemoteRepository`, `ProduksiRemoteRepository`.
3. **Kotak Warna Oranye/Ungu (Supabase Server - Schema: `mobile`)**:
   - Group `Supabase Cloud`: `Supabase Auth`, `mobile.users`, `mobile.karyawan`, `mobile.produksi`.
