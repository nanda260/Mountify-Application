package com.azhar.infopendakian.activities

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.azhar.infopendakian.R
import com.azhar.infopendakian.adapter.ListGunungAdapter
import com.azhar.infopendakian.data.AppDatabase
import com.azhar.infopendakian.data.dao.GunungDao
import com.azhar.infopendakian.databinding.ActivityListGunungBinding
import com.azhar.infopendakian.model.ModelGunung
import com.azhar.infopendakian.model.ModelMain
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.ArrayList

class ListGunungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListGunungBinding
    lateinit var listGunungAdapter: ListGunungAdapter
    lateinit var modelMain: ModelMain
    var modelGunung: MutableList<ModelGunung> = ArrayList()
    var strLokasiGunung: String? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListGunungBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        modelMain = intent.getSerializableExtra(LIST_GUNUNG) as ModelMain
        if (modelMain != null) {
            strLokasiGunung = modelMain.strLokasi
            binding.tvLokasi.text = strLokasiGunung

            // Set data adapter to RecyclerView
            listGunungAdapter = ListGunungAdapter(this, modelGunung)
            val gridLayoutManager = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                GridLayoutManager(this, 2)
            } else {
                GridLayoutManager(this, 3)
            }
            binding.rvListGunung.layoutManager = gridLayoutManager
            binding.rvListGunung.adapter = listGunungAdapter
            binding.rvListGunung.setHasFixedSize(true)

            getListGunung()
        }
    }

    private fun getListGunung() {
        try {
            modelGunung.clear()

            // Ambil data dari Firestore berdasarkan lokasi
            db.collection("gunung")
                .whereEqualTo("lokasi", strLokasiGunung ?: "")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val dataApi = ModelGunung().apply {
                            strNamaGunung = document.getString("nama") ?: "" // Jika null, gunakan string kosong
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
                    e.printStackTrace()
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        } catch (e: Exception) {
            e.printStackTrace()
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

    companion object {
        const val LIST_GUNUNG = "LIST_GUNUNG"
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
}
