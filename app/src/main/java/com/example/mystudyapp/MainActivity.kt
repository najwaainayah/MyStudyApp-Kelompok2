package com.example.mystudyapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var settings: SettingsManager
    private lateinit var profil: ProfilManager

    private lateinit var tvSubJudul: TextView
    private lateinit var tvMateriIsi: TextView
    private lateinit var tvJadwalIsi: TextView
    private lateinit var tvTugasIsi: TextView
    private lateinit var tvRingkasan: TextView
    private lateinit var tvNamaProfil: TextView
    private lateinit var imgAvatar: ImageView

    private val daftarAvatar = intArrayOf(
        R.drawable.avatar_1,
        R.drawable.avatar_2,
        R.drawable.avatar_3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settings = SettingsManager(this)
        profil = ProfilManager(this)

        tvSubJudul   = findViewById(R.id.tvSubJudul)
        tvMateriIsi  = findViewById(R.id.tvMateriIsi)
        tvJadwalIsi  = findViewById(R.id.tvJadwalIsi)
        tvTugasIsi   = findViewById(R.id.tvTugasIsi)
        tvRingkasan  = findViewById(R.id.tvRingkasan)
        tvNamaProfil = findViewById(R.id.tvNamaProfil)
        imgAvatar    = findViewById(R.id.imgAvatarProfil)

        imgAvatar.setOnClickListener { pilihAvatar() }
        findViewById<Button>(R.id.btnEditProfil).setOnClickListener { editProfil() }
        findViewById<Button>(R.id.btnPengaturan).setOnClickListener {
            startActivity(Intent(this, PengaturanActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        terapkanPengaturanTampilan()
        terapkanWarnaAksen()
        tampilkanProfil()
    }

    // ================= PROFIL =================
    private fun tampilkanProfil() {
        tvNamaProfil.text = profil.ambilNama()
            .ifEmpty { getString(R.string.profil_belum_diatur) }
        imgAvatar.setImageResource(daftarAvatar[profil.ambilAvatar()])
    }

    private fun editProfil() {
        val input = EditText(this).apply {
            hint = getString(R.string.hint_nama)
            setText(profil.ambilNama())
            setSelection(text.length)
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.btn_edit_profil)
            .setMessage(R.string.dialog_profil_pesan)
            .setView(input)
            .setPositiveButton(R.string.btn_simpan) { _, _ ->
                val nama = input.text.toString().trim()
                if (nama.isEmpty()) {
                    Toast.makeText(this, R.string.toast_nama_kosong, Toast.LENGTH_SHORT).show()
                } else {
                    profil.simpanNama(nama)
                    tampilkanProfil()
                }
            }
            .setNegativeButton(R.string.dialog_batal, null)
            .show()
    }

    private fun pilihAvatar() {
        val pilihan = Array(daftarAvatar.size) { getString(R.string.avatar_nama, it + 1) }

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_avatar_judul)
            .setSingleChoiceItems(pilihan, profil.ambilAvatar()) { dialog, index ->
                profil.simpanAvatar(index)
                tampilkanProfil()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_batal, null)
            .show()
    }

    // ================= PENGATURAN TAMPILAN =================
    private fun terapkanPengaturanTampilan() {
        val ukuran = settings.ukuranTeksSp()
        listOf(tvSubJudul, tvMateriIsi, tvJadwalIsi, tvTugasIsi).forEach {
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, ukuran)
        }
        tvRingkasan.text = RingkasanPengaturan.teks(this, settings)
    }

    private fun warnaAccent(): Int = when (settings.ambilAccent()) {
        SettingsManager.ACCENT_BIRU -> Color.parseColor("#4285F4")
        SettingsManager.ACCENT_UNGU -> Color.parseColor("#8B5CF6")
        else -> Color.parseColor("#2DB86F")
    }

    private fun terapkanWarnaAksen() {
        val warna = warnaAccent()
        val tint = ColorStateList.valueOf(warna)

        findViewById<Button>(R.id.btnPengaturan).backgroundTintList = tint
        findViewById<TextView>(R.id.tvNamaApp).setTextColor(warna)
        findViewById<TextView>(R.id.ikonMateri).backgroundTintList = tint
        findViewById<TextView>(R.id.ikonJadwal).backgroundTintList = tint
        findViewById<TextView>(R.id.ikonTugas).backgroundTintList = tint
    }
}