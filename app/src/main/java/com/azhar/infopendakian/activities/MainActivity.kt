package com.azhar.infopendakian.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.azhar.infopendakian.R
import com.azhar.infopendakian.adapter.MainAdapter
import com.azhar.infopendakian.databinding.ActivityMainBinding
import com.azhar.infopendakian.model.ModelMain
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import com.google.firebase.firestore.FirebaseFirestore
import java.nio.charset.StandardCharsets
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var modelMain: MutableList<ModelMain> = ArrayList()
    lateinit var mainAdapter: MainAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val adminButton = toolbar.findViewById<Button>(R.id.adminButton)
        val searchButton = toolbar.findViewById<Button>(R.id.searchButton)
        adminButton.setOnClickListener {
            val intent = Intent(this@MainActivity, AdminActivity::class.java)
            startActivity(intent)
        }
        searchButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        val imageList = ArrayList<SlideModel>() // Create image list
        imageList.add(SlideModel("https://i.ibb.co.com/h1sqsDr/Blue-Minimalist-Fuji-Mountain-Dekstop-Wallpaper.png"))
        imageList.add(SlideModel("https://torch.id/cdn/shop/articles/1706999589_Preview_12782da7-0e30-4f27-a52d-142ad405ef12.jpg?v=1706999596&width=1100"))
        imageList.add(SlideModel("https://goestrip.wordpress.com/wp-content/uploads/2015/12/merbabu-4.jpg"))
        imageList.add(SlideModel("https://upload.wikimedia.org/wikipedia/commons/thumb/b/ba/Sindoro_Mount.jpg/533px-Sindoro_Mount.jpg"))
        imageList.add(SlideModel("https://static.promediateknologi.id/crop/0x0:0x0/0x0/webp/photo/akuratco/928008400733.jpg"))
        imageList.add(SlideModel("https://static.promediateknologi.id/crop/0x0:0x0/0x0/webp/photo/p3/30/2024/01/28/WhatsApp-Image-2024-01-27-at-083747-2981778750.jpeg"))

        val imageSlider = binding.slider
        imageSlider.setImageList(imageList)

        mainAdapter = MainAdapter(this, modelMain)
        binding.rvLokasi.layoutManager = LinearLayoutManager(this)
        binding.rvLokasi.adapter = mainAdapter
        binding.rvLokasi.setHasFixedSize(true)

        binding.fabPeralatan.setOnClickListener {
            val intent = Intent(this@MainActivity, PeralatanActivity::class.java)
            startActivity(intent)
        }
        getProvinsiFromFirestore()
    }

    private fun getProvinsiFromFirestore() {
        db.collection("provinsi").get()
            .addOnSuccessListener { provinsiDocuments ->
                val provinsiGunung = mutableSetOf<String>()

                db.collection("gunung").get()
                    .addOnSuccessListener { gunungDocuments ->
                        for (document in gunungDocuments) {
                            val lokasiGunung = document.getString("lokasi")
                            if (lokasiGunung != null) {
                                provinsiGunung.add(lokasiGunung)
                            }
                        }

                        for (document in provinsiDocuments) {
                            val provinsi = document.getString("provinsi")
                            if (provinsi != null && !provinsiGunung.contains(provinsi)) {
                                // Jika provinsi tidak ada dalam gunung, hapus dari koleksi "provinsi"
                                deleteProvinsiDocument(document.id)
                            }
                        }

                        modelMain.clear()
                        for (document in provinsiDocuments) {
                            val provinsi = document.getString("provinsi")
                            if (provinsi != null && provinsiGunung.contains(provinsi)) {
                                val dataApi = ModelMain()
                                dataApi.strLokasi = provinsi
                                dataApi.strImageUrl = document.getString("defaultImage")
                                modelMain.add(dataApi)
                            }
                        }
                        mainAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Gagal mengambil data gunung: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mengambil data provinsi: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteProvinsiDocument(provinsiId: String) {
        db.collection("provinsi").document(provinsiId).delete()
            .addOnSuccessListener {
                println("Provinsi dengan ID $provinsiId berhasil dihapus karena tidak ada gunung terkait.")
            }
            .addOnFailureListener { exception ->
                println("Gagal menghapus provinsi dengan ID $provinsiId: ${exception.message}")
            }
    }

    override fun onResume() {
        super.onResume()
        getProvinsiFromFirestore()
    }

    companion object {
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
