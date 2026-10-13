
package com.example.mystudyapp

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class PengaturanActivity : AppCompatActivity() {

    private lateinit var settings: SettingsManager
    private lateinit var tvRingkasan: TextView
    private lateinit var tvStatusNotifikasi: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengaturan)

        settings = SettingsManager(this)
        tvRingkasan = findViewById(R.id.tvRingkasan)
        tvStatusNotifikasi = findViewById(R.id.tvStatusNotifikasi)

        val rgBahasa = findViewById<RadioGroup>(R.id.rgBahasa)
        val rgTema = findViewById<RadioGroup>(R.id.rgTema)
        val rgUkuran = findViewById<RadioGroup>(R.id.rgUkuran)
        val rgAccent = findViewById<RadioGroup>(R.id.rgAccent)
        val swNotifikasi = findViewById<SwitchCompat>(R.id.swNotifikasi)

        nonaktifkanSimpanState(
            rgBahasa, rgTema, rgUkuran, rgAccent
        )
        swNotifikasi.isSaveEnabled = false

        isiKeadaanAwal(
            rgBahasa,
            rgTema,
            rgUkuran,
            rgAccent,
            swNotifikasi
        )

        // Pilihan bahasa — konfirmasi dulu karena mengubah seluruh tampilan
        rgBahasa.setOnCheckedChangeListener { group, id ->
            if (sedangMemulihkanBahasa) return@setOnCheckedChangeListener

            val bahasaBaru = if (id == R.id.rbEnglish) {
                SettingsManager.BAHASA_EN
            } else {
                SettingsManager.BAHASA_ID
            }
            val bahasaLama = settings.ambilBahasa()
            if (bahasaBaru == bahasaLama) return@setOnCheckedChangeListener

            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_bahasa_judul)
                .setMessage(R.string.dialog_bahasa_pesan)
                .setNegativeButton(R.string.dialog_batal) { _, _ ->
                    kembalikanPilihanBahasa(group, bahasaLama)
                }
                .setOnCancelListener {
                    kembalikanPilihanBahasa(group, bahasaLama)
                }
                .setPositiveButton(R.string.dialog_ya) { _, _ ->
                    settings.simpanBahasa(bahasaBaru)
                    SettingsManager.terapkanKeAplikasi(this)
                }
                .show()
        }

        // Pilihan mode terang atau gelap
        rgTema.setOnCheckedChangeListener { _, id ->
            settings.simpanTema(
                when (id) {
                    R.id.rbGelap  -> SettingsManager.TEMA_GELAP
                    R.id.rbSistem -> SettingsManager.TEMA_SISTEM
                    else          -> SettingsManager.TEMA_TERANG
                }
            )

            SettingsManager.terapkanKeAplikasi(this)
        }

        // Pilihan ukuran teks
        rgUkuran.setOnCheckedChangeListener { _, id ->
            val nilai = when (id) {
                R.id.rbKecil -> SettingsManager.UKURAN_KECIL
                R.id.rbBesar -> SettingsManager.UKURAN_BESAR
                else -> SettingsManager.UKURAN_SEDANG
            }

            settings.simpanUkuran(nilai)
            perbaruiRingkasan()
        }

        // Pilihan warna aksen — cukup satu listener
        rgAccent.setOnCheckedChangeListener { _, id ->
            val warna = when (id) {
                R.id.rbBiru -> SettingsManager.ACCENT_BIRU
                R.id.rbUngu -> SettingsManager.ACCENT_UNGU
                R.id.rbHijau -> SettingsManager.ACCENT_HIJAU
                else -> return@setOnCheckedChangeListener
            }

            settings.simpanAccent(warna)
        }

        // Pilihan notifikasi
        swNotifikasi.setOnCheckedChangeListener { _, aktif ->
            settings.simpanNotifikasi(aktif)
            perbaruiRingkasan()
        }

        findViewById<Button>(R.id.btnReset).setOnClickListener {
            konfirmasiReset()
        }
    }

    override fun onResume() {
        super.onResume()
        perbaruiRingkasan()
    }

    private fun nonaktifkanSimpanState(vararg grup: RadioGroup) {
        grup.forEach { g ->
            for (i in 0 until g.childCount) {
                g.getChildAt(i).isSaveEnabled = false
            }
        }
    }

    private fun isiKeadaanAwal(
        rgBahasa: RadioGroup,
        rgTema: RadioGroup,
        rgUkuran: RadioGroup,
        rgAccent: RadioGroup,
        swNotifikasi: SwitchCompat
    ) {
        rgBahasa.check(
            if (settings.ambilBahasa() == SettingsManager.BAHASA_EN) {
                R.id.rbEnglish
            } else {
                R.id.rbIndonesia
            }
        )

        rgTema.check(
            when (settings.ambilTema()) {
                SettingsManager.TEMA_GELAP  -> R.id.rbGelap
                SettingsManager.TEMA_SISTEM -> R.id.rbSistem
                else                        -> R.id.rbTerang
            }
        )

        rgUkuran.check(
            when (settings.ambilUkuran()) {
                SettingsManager.UKURAN_KECIL -> R.id.rbKecil
                SettingsManager.UKURAN_BESAR -> R.id.rbBesar
                else -> R.id.rbSedang
            }
        )

        rgAccent.check(
            when (settings.ambilAccent()) {
                SettingsManager.ACCENT_BIRU -> R.id.rbBiru
                SettingsManager.ACCENT_UNGU -> R.id.rbUngu
                else -> R.id.rbHijau
            }
        )

        swNotifikasi.isChecked = settings.ambilNotifikasi()
        perbaruiRingkasan()
    }

    private fun perbaruiRingkasan() {
        tvRingkasan.text = RingkasanPengaturan.teks(this, settings)
        tvStatusNotifikasi.text =
            RingkasanPengaturan.statusNotifikasi(this, settings)
    }

    private fun kembalikanPilihanBahasa(group: RadioGroup, bahasaLama: String) {
        // tandai dulu supaya listener di atas tidak jalan lagi saat radio di-reset
        sedangMemulihkanBahasa = true
        group.check(
            if (bahasaLama == SettingsManager.BAHASA_EN) R.id.rbEnglish else R.id.rbIndonesia
        )
        sedangMemulihkanBahasa = false
    }

    private var sedangMemulihkanBahasa = false

    private fun konfirmasiReset() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_reset_judul)
            .setMessage(R.string.dialog_reset_pesan)
            .setNegativeButton(R.string.dialog_batal, null)
            .setPositiveButton(R.string.dialog_ya) { _, _ ->
                settings.reset()

                Toast.makeText(
                    this,
                    getString(R.string.toast_reset),
                    Toast.LENGTH_SHORT
                ).show()

                SettingsManager.terapkanKeAplikasi(this)
                recreate()
            }
            .show()
    }
}