package com.azhar.infopendakian.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azhar.infopendakian.R
import com.azhar.infopendakian.data.extensions.showCustomToast
import com.google.firebase.firestore.FirebaseFirestore

class TambahAkun : AppCompatActivity() {

    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var createButton: Button
    private lateinit var backBtn: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_akun)

        // Inisialisasi Views
        usernameField = findViewById(R.id.usernameField)
        passwordField = findViewById(R.id.passwordField)
        createButton = findViewById(R.id.TambahButton)
        backBtn = findViewById(R.id.backButton)

        // Inisialisasi Firebase Firestore
        db = FirebaseFirestore.getInstance()

        createButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                saveAdminAccount(username, password)
            }
            finish()
        }
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun saveAdminAccount(username: String, password: String) {
        val adminData = hashMapOf(
            "username" to username,
            "password" to password
        )

        db.collection("admin")
            .add(adminData)
            .addOnSuccessListener {
                showCustomToast("Anda berhasil membuat akun")
                // Reset form
                usernameField.text.clear()
                passwordField.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}
