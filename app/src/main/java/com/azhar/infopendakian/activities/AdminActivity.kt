package com.azhar.infopendakian.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.azhar.infopendakian.R
import com.azhar.infopendakian.data.extensions.showCustomToast
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {
    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var backBtn: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        usernameField = findViewById(R.id.usernameField)
        passwordField = findViewById(R.id.passwordField)
        loginButton = findViewById(R.id.loginButton)
        backBtn = findViewById(R.id.backButton)
        db = FirebaseFirestore.getInstance()

        val createAccountText = findViewById<TextView>(R.id.createAccountText)
        createAccountText.setOnClickListener {
            // Arahkan ke halaman Tambah Akun
            val intent = Intent(this, TambahAkun::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loginButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                showCustomToast("Field tidak boleh kosong")
            } else {
                loginUser(username, password)
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            if (username == "admin" && password == "admin") {
                // Jika username dan password benar, pindah ke EditorActivity
                showCustomToast("Anda berhasil masuk ke Admin Page")
                val intent = Intent(this, AdminListGunungActivity::class.java)
                startActivity(intent)
            } else {
                showCustomToast("Username atau password anda salah.")
            }
        }
            }
        backBtn.setOnClickListener {
            finish()
        }
    }
    private fun loginUser(username: String, password: String) {
        db.collection("admin")
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Login berhasil
                    showCustomToast("Anda berhasil masuk ke Admin Page")
                    val intent = Intent(this, AdminListGunungActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login gagal
                    showCustomToast("Username atau password salah")
                }
            }
            .addOnFailureListener { e ->
                showCustomToast("Terjadi kesalahan: ${e.message}")
            }
    }
}