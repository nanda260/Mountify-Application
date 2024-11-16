package com.azhar.infopendakian.activities

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.azhar.infopendakian.R
import com.azhar.infopendakian.databinding.ActivityDetailGunungBinding
import com.azhar.infopendakian.model.ModelGunung
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetailGunungActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityDetailGunungBinding
    private lateinit var googleMaps: GoogleMap
    private lateinit var modelGunung: ModelGunung

    var dblLatitude = 0.0
    var dblLongitude = 0.0
    var strNamaGunung: String? = null
    var strDeskripsi: String? = null
    var strJalurGunung: String? = null
    var strInfoGunung: String? = null
    lateinit var strLokasiGunung: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailGunungBinding.inflate(layoutInflater)
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

        // Get data
        modelGunung = intent.getSerializableExtra(DETAIL_GUNUNG) as ModelGunung
        if (modelGunung != null) {
            strLokasiGunung = modelGunung.strLokasiGunung
            strNamaGunung = modelGunung.strNamaGunung
            strDeskripsi = modelGunung.strDeskripsi
            strJalurGunung = modelGunung.strJalurPendakian
            strInfoGunung = modelGunung.strInfoGunung
            dblLatitude = modelGunung.strLat
            dblLongitude = modelGunung.strLong

            // Set data ke view dengan binding
            Glide.with(this)
                .load(modelGunung.strImageGunung)
                .into(binding.imageGunung)

            binding.tvNamaGunung.text = strNamaGunung
            binding.tvLokasiGunung.text = strLokasiGunung
            binding.tvDeskripsi.text = strDeskripsi
            binding.tvJalurGunung.text = strJalurGunung
            binding.tvInfoGunung.text = strInfoGunung

            val webView = findViewById<WebView>(R.id.webView)
            val webSettings: WebSettings = webView.settings
            webSettings.javaScriptEnabled = true
            //Maps VIew
            val html = ("<html><head>"
                    + "<link rel='stylesheet' href='https://unpkg.com/leaflet/dist/leaflet.css' />"
                    + "<style>#map { height: 100%; }</style>"
                    + "</head><body>"
                    + "<div id='map' style='width: 100%; height: 100%;'></div>"
                    + "<script src='https://unpkg.com/leaflet/dist/leaflet.js'></script>"
                    + "<script>"
                    + "var map = L.map('map').setView([$dblLatitude, $dblLongitude], 13);"
                    + "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {"
                    + "attribution: '&copy; Mountify'"
                    + "}).addTo(map);"
                    + "L.marker([$dblLatitude, $dblLongitude]).addTo(map)"
                    + ".bindPopup('$strNamaGunung').openPopup();"
                    + "</script>"
                    + "</body></html>")

            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)

            webView.webViewClient = WebViewClient()
        }
    }

    // Set map
    override fun onMapReady(googleMap: GoogleMap) {
        googleMaps = googleMap
        val latLng = LatLng(dblLatitude, dblLongitude)
        googleMaps.addMarker(MarkerOptions().position(latLng).title(strNamaGunung))
        googleMaps.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        googleMaps.uiSettings.setAllGesturesEnabled(true)
        googleMaps.uiSettings.isZoomGesturesEnabled = true
        googleMaps.isTrafficEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val DETAIL_GUNUNG = "DETAIL_GUNUNG"
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
