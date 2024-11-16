package com.azhar.infopendakian.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azhar.infopendakian.R
import com.azhar.infopendakian.activities.ListGunungActivity
import com.azhar.infopendakian.databinding.ListItemMainBinding
import com.azhar.infopendakian.model.ModelMain
import com.bumptech.glide.Glide

class MainAdapter(private val context: Context, private val modelMain: List<ModelMain>) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = modelMain[position]

        holder.binding.tvLokasi.text = data.strLokasi

        if (data.strImageUrl != null) {
            Glide.with(context)
                .load(data.strImageUrl)
                .into(holder.binding.imageLokasi)
        }

        holder.binding.cvListLokasi.setOnClickListener {
            val intent = Intent(context, ListGunungActivity::class.java)
            intent.putExtra(ListGunungActivity.LIST_GUNUNG, data)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return modelMain.size
    }

    class ViewHolder(val binding: ListItemMainBinding) : RecyclerView.ViewHolder(binding.root)
}
