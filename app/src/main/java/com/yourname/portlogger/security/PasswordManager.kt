package com.yourname.portlogger.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64

class PasswordManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val keyAlias = "password_key"

    init {
        if (!keyStore.containsAlias(keyAlias)) {
            generateKey()
        }
    }

    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    fun setPassword(password: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = keyStore.getKey(keyAlias, null) as SecretKey
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val encrypted = cipher.doFinal(password.toByteArray())
        val iv = cipher.iv

        prefs.edit()
            .putString("password", Base64.encodeToString(encrypted, Base64.DEFAULT))
            .putString("iv", Base64.encodeToString(iv, Base64.DEFAULT))
            .apply()
    }

    fun verifyPassword(password: String): Boolean {
        val encryptedPassword = prefs.getString("password", null) ?: return false
        val iv = prefs.getString("iv", null) ?: return false

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = keyStore.getKey(keyAlias, null) as SecretKey
        val spec = GCMParameterSpec(128, Base64.decode(iv, Base64.DEFAULT))
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val decrypted = String(cipher.doFinal(Base64.decode(encryptedPassword, Base64.DEFAULT)))
        return password == decrypted
    }

    fun isPasswordSet(): Boolean {
        return prefs.contains("password")
    }

    fun getLoginAttempts(): Int {
        return prefs.getInt("login_attempts", 0)
    }

    fun incrementLoginAttempts() {
        prefs.edit().putInt("login_attempts", getLoginAttempts() + 1).apply()
    }

    fun resetLoginAttempts() {
        prefs.edit().putInt("login_attempts", 0).apply()
    }

    fun getLastAttemptTime(): Long {
        return prefs.getLong("last_attempt_time", 0)
    }

    fun setLastAttemptTime(time: Long) {
        prefs.edit().putLong("last_attempt_time", time).apply()
    }
} 