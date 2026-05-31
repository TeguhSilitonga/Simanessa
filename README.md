# SIMANESSA (Sistem Manajemen Nilai Evaluasi Siswa)

SIMANESSA adalah aplikasi desktop berbasis **JavaFX** yang menerapkan pola arsitektur **MVC (Model-View-Controller)**. Aplikasi ini dirancang secara khusus untuk membantu tenaga pendidik (guru) dalam mendokumentasikan, mengelola, serta mengalkulasi komponen nilai akademik siswa secara digital, aman, dan efisien.

---

## 🚀 Fitur Utama

* **Autentikasi Akses Guru:** Sistem login terenkapsulasi untuk memastikan keamanan data nilai akademik siswa dari akses tidak sah.
* **Manajemen Data Siswa (CRUD):** Operasi komparatif lengkap untuk Menambah (*Create*), Membaca (*Read*), Mengubah (*Update*), dan Menghapus (*Delete*) data profil serta nilai siswa secara dinamis.
* **Pencarian Efisien & Real-Time:** Menapis dan menampilkan baris data siswa secara instan berdasarkan kecocokan Nomor Induk Siswa (NIS) atau Nama melalui komponen memori yang dioptimalkan.
* **Kalkulasi Bobot Nilai Dinamis:** Menyesuaikan persentase bobot penilaian (Tugas, UTS, UAS) secara global yang secara otomatis memperbarui akumulasi nilai akhir dan status kelulusan siswa.
* **Antarmuka Modern (Custom CSS):** Dilengkapi estetika antarmuka bertema gelap (*dark mode*) yang bersih, menggunakan kustomisasi penuh pada komponen TableView dan Dialog Alert.
* **Pelaporan PDF:** Fitur ekspor kompilasi data nilai siswa secara langsung ke dalam format dokumen PDF yang siap cetak.

---

## 🛠️ Arsitektur & Struktur Proyek

Proyek ini dipisahkan secara terstruktur berdasarkan tanggung jawab komponen logisnya:

app/src/main/java/
│
├── app/
│   └── Main.java               # Titik Awal Utama (Entry Point) & Manajemen Scene
│
├── model/
│   ├── User.java               # Kelas dasar struktural pengguna
│   ├── Guru.java               # Turunan model khusus entitas Guru
│   ├── Student.java            # Blueprint representasi identitas dan nilai siswa
│   └── WeightConfig.java       # Pengaturan pembobotan parameter nilai
│
├── controller/
│   ├── StudentManager.java     # Pengendali logika bisnis dan manipulasi I/O data
│   └── DatabaseHelper.java     # Konfigurasi koneksi database relasional (SQLite)
│
└── view/
    ├── SplashScreen.java       # Layar pemuatan (loading) visual awal aplikasi
    ├── LoginView.java          # Formulir autentikasi masuk sistem
    ├── DashboardView.java      # Panel kontrol utama input data akademik
    ├── DataSiswaView.java      # Tampilan tabel data induk dan bar pencarian
    ├── EditStudentView.java    # Dialog pembaruan/reduksi komponen nilai siswa
    ├── WeightView.java         # Antarmuka modifikasi konfigurasi bobot nilai global
    └── AlertHelper.java        # Kelas utilitas dialog notifikasi kustom

---

## 📦 Prasyarat Sistem

Sebelum mengeksekusi kode program, pastikan lingkungan perangkat Anda telah terkonfigurasi dengan runtime berikut:

* **Java Development Kit (JDK):** Versi 17 atau versi yang lebih baru.
* **Gradle:** Minimal Versi 8.x (Direkomendasikan menggunakan Gradle Wrapper bawaan proyek).

---

## ⚙️ Petunjuk Menjalankan Aplikasi

1. **Unduh atau Clone Repositori**
```bash
git clone [https://github.com/username_anda/simanessa.git](https://github.com/username_anda/simanessa.git)
cd simanessa
```

2. **Eksekusi Proyek Melalui Gradle**
```bash
./gradlew run run
```

---

## 🔒 Hak Akses Masuk Sistem (Default)

Gunakan kredensial bawaan berikut untuk menguji fungsionalitas menu guru pada halaman login utama:

* **Username:** `guru`
* **Password:** `123`

---

## 🛠️ Spesifikasi Teknologi

* **Bahasa Utama:** Java (JDK 17)
* **Framework Grafis:** JavaFX
* **Sistem Otomatisasi Build:** Gradle
* **Mekanisme Retensi Data:** Java Object Serialization (`.dat`) / SQLite Database Integration

---

*SIMANESSA — Dikembangkan untuk efisiensi administrasi akademis internal sekolah.*
