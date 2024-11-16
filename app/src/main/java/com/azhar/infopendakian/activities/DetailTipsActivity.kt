package com.azhar.infopendakian.activities

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.azhar.infopendakian.R
import com.azhar.infopendakian.databinding.ActivityDetailPeralatanBinding
import com.azhar.infopendakian.model.ModelPeralatan
import com.bumptech.glide.Glide

class DetailTipsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPeralatanBinding
    lateinit var modelPeralatan: ModelPeralatan
    var strNamaAlat: String? = null
    var strTips: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailPeralatanBinding.inflate(layoutInflater)
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

        modelPeralatan = intent.getSerializableExtra(DETAIL_PERALATAN) as ModelPeralatan
        if (modelPeralatan != null) {
            strNamaAlat = modelPeralatan.strNamaPeralatan
            strTips = modelPeralatan.strTipsPeralatan

            // Set data ke view menggunakan binding
            Glide.with(this)
                .load(modelPeralatan.strImagePeralatan)
                .into(binding.imageAlat)

            binding.tvNamaAlat.text = strNamaAlat
            binding.tvDetailAlat.text = strTips
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
        const val DETAIL_PERALATAN = "DETAIL_PERALATAN"
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
