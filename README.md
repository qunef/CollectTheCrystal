# Collect The Skill Balls
Collect The Skill Balls adalah sebuah game desktop 2D yang dibangun menggunakan Java Swing dengan arsitektur Model-View-Presenter (MVP). Dalam game ini, pemain mengontrol seorang astronot untuk mengumpulkan kristal energi yang melayang di luar angkasa menggunakan sinar traktor. Skor didapatkan dengan cara membawa kristal yang tertangkap secara manual ke sebuah blackhole.

Proyek ini merupakan implementasi dari berbagai konsep Object-Oriented Programming (OOP), arsitektur perangkat lunak, game loop dengan multithreading, hingga konektivitas database untuk sistem skor yang persisten.

# Fitur Utama
* Mekanik Gameplay Unik: Karakter harus menangkap objek dengan "lasso" (sinar traktor), membawanya, lalu secara manual menyetorkannya ke titik kumpul (blackhole) untuk mendapatkan skor.
* Karakter Responsif: Karakter astronot dapat bergerak ke empat arah dan gambar sprite-nya akan membalik secara otomatis saat bergerak ke kiri atau kanan.
* Sistem Koleksi Bervariasi: Terdapat 4 jenis kristal (merah, biru, hijau, ungu) dengan nilai skor yang berbeda dan probabilitas kemunculan yang bervariasi.
* Skor Persisten: Skor pemain disimpan dalam database MySQL. Jika pemain dengan username yang sama bermain lagi, skor akan diakumulasikan (UPDATE), bukan membuat entri baru (INSERT).
* High Score Table: Menu utama menampilkan daftar 10 skor tertinggi yang diambil langsung dari database dan dapat di-scroll.
* Arsitektur MVP: Logika bisnis (Presenter), data (Model), dan tampilan (View) dipisahkan secara rapi untuk meningkatkan keterbacaan dan kemudahan pengelolaan kode.
* Game Loop Berbasis Thread: Animasi dan logika game berjalan pada Thread terpisah untuk memastikan antarmuka pengguna (GUI) tetap responsif.
* Audio: Terdapat musik latar saat permainan berlangsung untuk meningkatkan pengalaman bermain.
