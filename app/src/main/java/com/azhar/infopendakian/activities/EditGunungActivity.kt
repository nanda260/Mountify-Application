package com.azhar.infopendakian.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.azhar.infopendakian.R
import com.azhar.infopendakian.data.extensions.showCustomToast
import com.azhar.infopendakian.model.ModelGunung
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class EditGunungActivity : AppCompatActivity() {
    private lateinit var namaField: EditText
    private lateinit var imageField: EditText
    private lateinit var lokasiGroup: RadioGroup
    private lateinit var lat: EditText
    private lateinit var lon: EditText
    private lateinit var deskripsiField: EditText
    private lateinit var jalurField: EditText
    private lateinit var infoField: EditText
    private lateinit var simpanButton: Button
    private lateinit var backBtn: Button
    private val db = FirebaseFirestore.getInstance()
    private var documentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_gunung)

        namaField = findViewById(R.id.namaField)
        imageField = findViewById(R.id.imageField)
        lokasiGroup = findViewById(R.id.lokasiGroup)
        lat = findViewById(R.id.lat)
        lon = findViewById(R.id.lon)
        deskripsiField = findViewById(R.id.deskripsiField)
        jalurField = findViewById(R.id.jalurField)
        infoField = findViewById(R.id.infoField)
        simpanButton = findViewById(R.id.simpanButton)
        backBtn = findViewById(R.id.backBtn)
        documentId = intent.getStringExtra("documentId")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get Gunung data
        val gunungData = intent.getSerializableExtra("gunungData") as? ModelGunung
        gunungData?.let {
            namaField.setText(it.strNamaGunung)
            imageField.setText(it.strImageGunung)
            lat.setText(it.strLat.toString())
            lon.setText(it.strLong.toString())
            deskripsiField.setText(it.strDeskripsi)
            jalurField.setText(it.strJalurPendakian)
            infoField.setText(it.strInfoGunung)

            when (it.strLokasiGunung) {
                "Jawa Timur" -> findViewById<RadioButton>(R.id.radio_jawa_timur).isChecked = true
                "Jawa Tengah" -> findViewById<RadioButton>(R.id.radio_jawa_tengah).isChecked = true
                "Jawa Barat" -> findViewById<RadioButton>(R.id.radio_jawa_barat).isChecked = true
                else -> {
                    findViewById<RadioButton>(R.id.radio_jawa_timur).isChecked = false
                    findViewById<RadioButton>(R.id.radio_jawa_tengah).isChecked = false
                    findViewById<RadioButton>(R.id.radio_jawa_barat).isChecked = false
                    findViewById<EditText>(R.id.lainnya).setText(it.strLokasiGunung)
                }
            }
        }

        val lainnyaField = findViewById<EditText>(R.id.lainnya)
        lainnyaField.addTextChangedListener {
            if (lainnyaField.text.isNotEmpty()) {
                findViewById<RadioButton>(R.id.radio_jawa_timur).isChecked = false
                findViewById<RadioButton>(R.id.radio_jawa_tengah).isChecked = false
                findViewById<RadioButton>(R.id.radio_jawa_barat).isChecked = false
            }
        }

        // Simpan gunung
        simpanButton.setOnClickListener {
            if (namaField.text.isNotEmpty() && imageField.text.isNotEmpty() && lat.text.isNotEmpty() && lon.text.isNotEmpty()
                && deskripsiField.text.isNotEmpty() && jalurField.text.isNotEmpty() && infoField.text.isNotEmpty()) {

                val latValue = lat.text.toString().toDoubleOrNull() ?: 0.0
                val lonValue = lon.text.toString().toDoubleOrNull() ?: 0.0
                val selectedId = lokasiGroup.checkedRadioButtonId
                var lokasiGunung = ""
                val lainnyaValue = lainnyaField.text.toString()
                if (lainnyaValue.isNotEmpty()) {
                    lokasiGunung = lainnyaValue
                } else if (selectedId != -1) {
                    val radioButton = findViewById<RadioButton>(selectedId)
                    lokasiGunung = radioButton.text.toString()
                }

                // Create data to save to Firestore
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

                db.collection("gunung")
                    .whereEqualTo("nama", namaField.text.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {

                            val documentId = documents.documents[0].id

                            // Update
                            db.collection("gunung").document(documentId)
                                .set(gunungData, SetOptions.merge())
                                .addOnSuccessListener {
                                    showCustomToast("Data berhasil diperbarui")
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(applicationContext, "Gagal memperbarui data: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            showCustomToast("Data dengan nama tersebut tidak ditemukan")
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(applicationContext, "Gagal mencari data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                // Jika provinsi baru, maka tambah lagi provinsi lainnya
                if (lainnyaValue.isNotEmpty()) {
                    saveProvinsiIfFirstEntry(lainnyaValue, imageField.text.toString())
                }
            } else {
                showCustomToast("Silahkan isi semua data dengan valid")
            }
        }

        // Back button
        backBtn.setOnClickListener {
            finish()
        }
    }

    // Fungsi menambahkan gunung jika tidak ada provinsi yg sesuai
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
