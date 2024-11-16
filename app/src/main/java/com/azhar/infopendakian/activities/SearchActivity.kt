package com.azhar.infopendakian.activities

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.azhar.infopendakian.R
import com.azhar.infopendakian.adapter.ListGunungAdapter
import com.azhar.infopendakian.databinding.ActivitySearchBinding
import com.azhar.infopendakian.model.ModelGunung
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var listGunungAdapter: ListGunungAdapter
    private var modelGunung: MutableList<ModelGunung> = ArrayList()
    private var filteredGunung: MutableList<ModelGunung> = ArrayList()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listGunungAdapter = ListGunungAdapter(this, filteredGunung)
        binding.rvListGunung.adapter = listGunungAdapter
        binding.rvListGunung.layoutManager = GridLayoutManager(this, 2)

        binding.usernameField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterGunungList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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
    }

    private fun getListGunung() {
        try {
            modelGunung.clear()

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

                    filterGunungList(binding.usernameField.text.toString())
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

    private fun filterGunungList(query: String) {
        filteredGunung.clear()

        if (query.isEmpty()) {
            binding.tvEmptyMessage.text = "Belum ada gunung yang dicari."
            binding.tvEmptyMessage.visibility = View.VISIBLE
        } else {
            for (gunung in modelGunung) {
                if (gunung.strNamaGunung.contains(query, ignoreCase = true)) {
                    filteredGunung.add(gunung)
                }
            }

            if (filteredGunung.isEmpty()) {
                binding.tvEmptyMessage.text = "Masukkan nama gunung dengan benar."
                binding.tvEmptyMessage.visibility = View.VISIBLE
            } else {
                binding.tvEmptyMessage.visibility = View.GONE
            }
        }

        listGunungAdapter.notifyDataSetChanged()
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
