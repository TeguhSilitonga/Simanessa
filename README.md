# SIMANESSA (Sistem Manajemen Nilai Evaluasi Siswa)

SIMANESSA adalah aplikasi desktop berbasis **JavaFX** yang menerapkan pola arsitektur **MVC (Model-View-Controller)**. Aplikasi ini dirancang secara khusus untuk membantu tenaga pendidik (guru) dalam mendokumentasikan, mengelola, serta mengalkulasi komponen nilai akademik siswa secara digital, aman, dan efisien.

---

## 👥 Tim Pengembang

Aplikasi ini dikembangkan secara kolaboratif untuk memenuhi Proyek Akhir Lab PBO oleh:
* **Afdhol As Syamardi - H071251049** - *Fokus: Security, Validasi Regex, Anti-Data Collision, & Utilitas Database.*
* **Ilmi Ahmad Alfaridzi - H071251052** - *Fokus: Core Engine, Model Data, File I/O Serialization, & Kalkulasi Bobot.*
* **Teguh Paniro Silitonga -H071251017** - *Fokus: Front-End Architecture, UI Routing, & Desain Antarmuka.*
--- 

## 🚀 Fitur Utama

* **Sistem Autentikasi & Keamanan Ketat:** Dilengkapi proteksi *Brute-Force* (pembekuan layar login otomatis selama 15 detik pada 3 kali kegagalan beruntun), validasi input berbasis *Regex*, dan perlindungan *Shoulder Surfing* pada tabel sandi.

* **Manajemen Multi-Peran (Admin & Guru):** Hierarki hak akses terpusat di mana Administrator bertindak sebagai *Super User* untuk mengelola (CRUD) akun kredensial Guru, dilengkapi algoritma *Collision Avoidance* untuk mencegah duplikasi NUPTK/Username.

* **Isolasi Data Siswa (CRUD):** Implementasi penyimpanan Serialization (`.dat`) terenkapsulasi di mana setiap Guru memiliki ruang *database* berkasnya masing-masing. Memastikan data siswa antar kelas/guru terisolasi dengan aman dan tidak bocor.

* **Kalkulasi Bobot Dinamis & Dynamic Tasks:** Jumlah input "Nilai Tugas" bersifat fleksibel (tak terbatas). Guru dapat mengubah persentase bobot penilaian (Tugas, UTS, UAS) secara global yang memicu rekalkulasi otomatis nilai akhir dan status kelulusan seluruh siswa.

* **Filter Bersarang & Pencarian Real-Time:** Menapis dan menyortir data siswa secara instan berdasarkan kecocokan Nomor Induk Siswa (NIS), Nama, Kelas, hingga Status Kelulusan.

* **Arsitektur Single Page Application (SPA):** Menggunakan teknik modifikasi "Root Scene" murni JavaFX (tanpa FXML) yang menghasilkan transisi perpindahan menu super mulus tanpa kedipan (*flicker*) atau perubahan ukuran layar.

* **Pelaporan Eksekutif PDF:** Fitur ekspor kompilasi data nilai akademik siswa secara instan ke dalam format dokumen PDF profesional (terintegrasi dengan *iTextPDF*).
---

## 🛠️ Arsitektur & Struktur Proyek

Proyek ini dipisahkan secara terstruktur berdasarkan prinsip pembagian tanggung jawab komponen (*MVC Pattern*):

```

text
app/src/main/java/
│
├── app/
│   └── Main.java               # Titik Awal Utama (Entry Point) & SPA Scene Router
│
├── model/
│   ├── User.java               # Kelas abstrak dasar kredensial pengguna
│   ├── Admin.java              # Entitas super-user (Pewarisan dari User)
│   ├── Guru.java               # Entitas pendidik (Pewarisan dari User)
│   ├── Student.java            # Blueprint representasi identitas dan kalkulasi siswa
│   └── WeightConfig.java       # Kelas statis pengaturan pembobotan parameter nilai
│
├── controller/
│   ├── StudentManager.java     # Mesin I/O Serialisasi berfitur Isolasi Data per Guru
│   ├── GuruManager.java        # Pengelola logika bisnis autentikasi dan akun Guru
│   └── DatabaseHelper.java     # Pondasi utilitas koneksi basis data abstrak
│
└── view/
    ├── SplashScreen.java       # Layar animasi pemuatan visual awal aplikasi
    ├── LoginView.java          # Gerbang autentikasi dengan detektor Brute-Force
    ├── DashboardViewAdmin.java # Panel navigasi utama khusus Administrator
    ├── DashboardViewGuru.java  # Panel ringkasan statistik real-time khusus Guru
    ├── ManajemenGuruView.java  # Tabel pengelolaan dan form registrasi akun Guru
    ├── EditGuruView.java       # Dialog pembaruan kredensial dan identitas Guru
    ├── DataSiswaView.java      # Tampilan tabel data induk siswa dan form dinamis
    ├── EditStudentView.java    # Dialog modifikasi identitas dan matriks nilai siswa
    ├── WeightView.java         # Antarmuka penyesuaian rasio persentase bobot nilai
    └── AlertHelper.java        # Kelas template utilitas dialog notifikasi seragam



---

## 🧱 Penerapan 4 Pilar OOP

Sebagai pemenuhan standar proyek rekayasa perangkat lunak berorientasi objek, SIMANESSA menerapkan empat pilar utama (4 Pillars of OOP) di dalam kodenya:

1. **Encapsulation (Enkapsulasi):** Seluruh properti kritis dalam model data (seperti `password`, `nis`, `nilaiAkhir` di kelas `Student` dan `Guru`) disembunyikan menggunakan access modifier `private`. Modifikasi dan akses data dari luar kelas diatur secara ketat melalui metode `getter` dan `setter`.
2. **Abstraction (Abstraksi):** Sistem menggunakan kelas `User.java` sebagai konsep abstrak dari pengguna aplikasi. Detail spesifik disembunyikan, dan hanya menyediakan kerangka dasar (seperti `username` dan `password`) yang wajib diimplementasikan oleh entitas nyata turunannya.
3. **Inheritance (Pewarisan):** Kelas `Guru.java` merupakan turunan (melakukan `extends`) dari kelas induk `User.java`. Kelas `Guru` secara otomatis mewarisi sifat dasar kredensial login, memungkinkan kelas ini fokus pada penambahan atribut spesifik pendidik (seperti NUPTK dan Mata Pelajaran).
4. **Polymorphism (Polimorfisme):** Diterapkan pada proses distribusi otorisasi. Sistem dapat menerima objek berbentuk kelas induk (`User`), namun saat *runtime* (aplikasi berjalan), metode mampu mengenali wujud asli objek turunannya (`Guru` atau `Admin`) dan memproses tampilan dasbor yang sesuai dengan identitas tersebut.



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

---

2. **Eksekusi Proyek Melalui Gradle**
./gradlew run

 silahkan open new terminal dan jalankan kode berikut:  ./gradlew ru
```

---

## 🔒 Hak Akses Masuk Sistem (Default)

Untuk keperluan pengujian aplikasi (*testing*), sistem telah dilengkapi dengan kredensial bawaan. Gunakan akses **Administrator** untuk mengelola pendaftaran akun Guru.

**👨‍💼 Akses Administrator (Super User):**
* **Username:** `admin`
* **Password:** `admin123` *(Sesuaikan jika sandi admin Anda berbeda)*

**👩‍🏫 Akses Pendidik (Guru):**
*(Anda dapat membuat akun guru baru secara langsung melalui Dasbor Administrator).*

---

## 🛠️ Spesifikasi Teknologi

* **Bahasa Utama:** Java (JDK 17)
* **Framework Grafis:** JavaFX
* **Sistem Otomatisasi Build:** Gradle
* **Mekanisme Retensi Data:** Java Object Serialization (`.dat`) / SQLite Database Integration

---

*SIMANESSA — Dikembangkan untuk efisiensi administrasi akademis internal sekolah.*
