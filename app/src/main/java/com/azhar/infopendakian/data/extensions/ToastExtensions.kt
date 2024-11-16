package com.azhar.infopendakian.data.extensions
// File: src/main/java/com/example/myapp/extensions/ToastExtensions.kt

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.view.View
import com.azhar.infopendakian.R

// Extension function untuk menampilkan custom Toast
fun Context.showCustomToast(message: String) {
    // Inflate layout custom Toast
    val inflater = LayoutInflater.from(this)
    val layout: View = inflater.inflate(R.layout.toast_custom, null)

    // Set message pada TextView
    val toastMessage: TextView = layout.findViewById(R.id.toast_message)
    toastMessage.text = message

    // Set ikon pada ImageView (optional)
    val toastIcon: ImageView = layout.findViewById(R.id.toast_icon)
    toastIcon.setImageResource(R.drawable.logotransparan) // Ganti dengan ikon yang diinginkan

    // Buat dan tampilkan Toast
    val toast = Toast(this)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = layout
    toast.show()
}
