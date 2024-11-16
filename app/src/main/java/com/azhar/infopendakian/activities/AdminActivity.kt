package com.azhar.infopendakian.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.azhar.infopendakian.R
import com.azhar.infopendakian.data.extensions.showCustomToast

class AdminActivity : AppCompatActivity() {
    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var backBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        usernameField = findViewById(R.id.usernameField)
        passwordField = findViewById(R.id.passwordField)
        loginButton = findViewById(R.id.loginButton)
        backBtn = findViewById(R.id.backButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loginButton.setOnClickListener {
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
        backBtn.setOnClickListener {
            finish()
        }
    }
}