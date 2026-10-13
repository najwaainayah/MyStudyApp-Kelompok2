package com.example.mystudyapp

import android.content.Context

class ProfilManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(NAMA_PREFS, Context.MODE_PRIVATE)

    companion object {
        private const val NAMA_PREFS = "profil_pengguna"
        private const val KEY_NAMA = "nama"
        private const val KEY_AVATAR = "avatar_index"
        const val JUMLAH_AVATAR = 3
    }

    fun simpanNama(nama: String) {
        prefs.edit().putString(KEY_NAMA, nama).apply()
    }

    fun ambilNama(): String = prefs.getString(KEY_NAMA, "") ?: ""

    fun simpanAvatar(index: Int) {
        prefs.edit().putInt(KEY_AVATAR, index).apply()
    }

    // coerceIn: kalau nilai tersimpan di luar 0..2, dipaksa kembali ke rentang aman
    fun ambilAvatar(): Int =
        prefs.getInt(KEY_AVATAR, 0).coerceIn(0, JUMLAH_AVATAR - 1)
}