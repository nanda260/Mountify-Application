package com.azhar.infopendakian.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.azhar.infopendakian.adapter.AdminListGunungAdapter
import com.azhar.infopendakian.data.extensions.showCustomToast
import com.azhar.infopendakian.databinding.ActivityAdminListGunungBinding
import com.azhar.infopendakian.model.ModelGunung
import com.google.firebase.firestore.FirebaseFirestore

class AdminListGunungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminListGunungBinding
    private lateinit var listGunungAdapter: AdminListGunungAdapter
    private var modelGunung: MutableList<ModelGunung> = ArrayList()
    private val db = FirebaseFirestore.getInstance()

    companion object {
        const val REQUEST_CODE_EDIT = 100
        const val REQUEST_CODE_ADD = 101

        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val window = activity.window
            val layoutParams = window.attributes
            if (on) {
                layoutParams.flags = layoutParams.flags or bits
            } else {
                layoutParams.flags = layoutParams.flags and bits.inv()
            }
            window.attributes = layoutParams
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminListGunungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listGunungAdapter = AdminListGunungAdapter(this, modelGunung,
            onEditClicked = { gunungData ->
                val intent = Intent(this, EditGunungActivity::class.java)
                intent.putExtra("gunungData", gunungData)
                startActivityForResult(intent, REQUEST_CODE_EDIT)
            },
            onDeleteSuccess = {
                refreshList()
            }
        )

        binding.rvListGunung.layoutManager = GridLayoutManager(this, 2)
        binding.rvListGunung.adapter = listGunungAdapter

        getListGunung()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)



        binding.fabAdd.setOnClickListener {
            val intent = Intent(this@AdminListGunungActivity, EditorActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD)
        }
    }
    // Refresh data setelah penghapusan item
    private fun refreshList() {
        getListGunung()
    }
    private fun getListGunung() {
        modelGunung.clear()

        // Ambil data dari Firestore
        db.collection("gunung")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val dataApi = ModelGunung().apply {
                        strNamaGunung = document.getString("nama") ?: ""
                        strImageGunung = document.getString("imageGunung") ?: ""
                        strLokasiGunung = document.getString("lokasi") ?: ""
                        strDeskripsi = document.getString("deskripsi") ?: ""
                        strInfoGunung = document.getString("infoGunung") ?: ""
                        strJalurPendakian = document.getString("jalurPendakian") ?: ""
                        strLat = document.getDouble("lat") ?: 0.0
                        strLong = document.getDouble("lon") ?: 0.0
                    }
                    modelGunung.add(dataApi)
                }

                listGunungAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_EDIT -> getListGunung()
                REQUEST_CODE_ADD -> getListGunung()
            }
        } else {
            showCustomToast("Tidak ada data yang dirubah")
        }
    }
}
