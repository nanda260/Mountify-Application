package com.azhar.infopendakian.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.azhar.infopendakian.R
import com.azhar.infopendakian.data.extensions.showCustomToast
import com.google.firebase.firestore.FirebaseFirestore

class EditorActivity : AppCompatActivity() {
    private lateinit var namaField: EditText
    private lateinit var imageField: EditText
    private lateinit var lokasiGroup: RadioGroup
    private lateinit var lat: EditText
    private lateinit var lon: EditText
    private lateinit var deskripsiField: EditText
    private lateinit var jalurField: EditText
    private lateinit var infoField: EditText
    private lateinit var lainnyaField: EditText
    private lateinit var simpanButton: Button
    private lateinit var backBtn: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        namaField = findViewById(R.id.namaField)
        imageField = findViewById(R.id.imageField)
        lokasiGroup = findViewById(R.id.lokasiGroup)
        lat = findViewById(R.id.lat)
        lon = findViewById(R.id.lon)
        deskripsiField = findViewById(R.id.deskripsiField)
        jalurField = findViewById(R.id.jalurField)
        infoField = findViewById(R.id.infoField)
        lainnyaField = findViewById(R.id.lainnya)
        simpanButton = findViewById(R.id.simpanButton)
        backBtn = findViewById(R.id.backBtn)

        lainnyaField.addTextChangedListener {
            if (lainnyaField.text.isNotEmpty()) {
                findViewById<RadioButton>(R.id.radio_jawa_timur).isChecked = false
                findViewById<RadioButton>(R.id.radio_jawa_tengah).isChecked = false
                findViewById<RadioButton>(R.id.radio_jawa_barat).isChecked = false
            }
        }

        // Tombol Simpan
        simpanButton.setOnClickListener {
            val latValue = lat.text.toString().toDoubleOrNull()
            val lonValue = lon.text.toString().toDoubleOrNull()

            // Validasi data sebelum disimpan
            if (validateFields() && latValue != null && lonValue != null) {
                val selectedId = lokasiGroup.checkedRadioButtonId
                var lokasiGunung = ""
                val lainnyaValue = lainnyaField.text.toString()
                if (lainnyaValue.isNotEmpty()) {
                    lokasiGunung = lainnyaValue
                } else if (selectedId != -1) {
                    val radioButton = findViewById<RadioButton>(selectedId)
                    lokasiGunung = radioButton.text.toString()
                }

                // Data gunung untuk disimpan
                val gunungData = hashMapOf(
                    "nama" to namaField.text.toString(),
                    "imageGunung" to imageField.text.toString(),
                    "lokasi" to lokasiGunung,
                    "lat" to latValue,
                    "lon" to lonValue,
                    "deskripsi" to deskripsiField.text.toString(),
                    "jalurPendakian" to jalurField.text.toString(),
                    "infoGunung" to infoField.text.toString()
                )

                // Menyimpan data gunung ke Firestore
                db.collection("gunung").add(gunungData)
                    .addOnSuccessListener {
                        showCustomToast("Data berhasil disimpan")
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        showCustomToast("Gagal menyimpan data")
                    }

                // Simpan provinsi jika provinsi lainnya baru
                if (lainnyaValue.isNotEmpty()) {
                    saveProvinsiIfFirstEntry(lokasiGunung, imageField.text.toString())
                }
            } else {
                showCustomToast("Silahkan isi semua data dengan valid")
            }
        }

        // Tombol Kembali
        backBtn.setOnClickListener {
            finish()
        }
    }

    // Fungsi untuk validasi form
    private fun validateFields(): Boolean {
        return when {
            namaField.text.isEmpty() -> {
                namaField.error = "Nama gunung harus diisi"
                false
            }
            imageField.text.isEmpty() -> {
                imageField.error = "URL gambar harus diisi"
                false
            }
            lokasiGroup.checkedRadioButtonId == -1 && lainnyaField.text.isEmpty() -> {
                lainnyaField.error = "Provinsi lainnya harus diisi"
                false
            }
            deskripsiField.text.isEmpty() -> {
                deskripsiField.error = "Deskripsi harus diisi"
                false
            }
            jalurField.text.isEmpty() -> {
                jalurField.error = "Jalur pendakian harus diisi"
                false
            }
            infoField.text.isEmpty() -> {
                infoField.error = "Info gunung harus diisi"
                false
            }
            else -> true
        }
    }

    // Fungsi untuk menyimpan provinsi jika provinsi belum ada
    private fun saveProvinsiIfFirstEntry(provinsi: String, imageUrl: String) {
        db.collection("provinsi").document(provinsi).get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val provinsiData = hashMapOf(
                    "provinsi" to provinsi,
                    "defaultImage" to imageUrl
                )
                db.collection("provinsi").document(provinsi).set(provinsiData)
                    .addOnSuccessListener {
                        showCustomToast("Provinsi berhasil ditambahkan")
                    }
                    .addOnFailureListener { e ->
                        showCustomToast("Provinsi gagal ditambahkan")
                    }
            }
        }
    }
}
