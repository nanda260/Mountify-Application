package com.azhar.infopendakian.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azhar.infopendakian.R
import com.azhar.infopendakian.activities.DetailPeralatanActivity
import com.azhar.infopendakian.model.ModelPeralatan
import com.bumptech.glide.Glide
import com.azhar.infopendakian.databinding.ListItemPeralatanTipsBinding

class PeralatanAdapter(
    private val context: Context,
    private val modelPeralatan: List<ModelPeralatan>
) : RecyclerView.Adapter<PeralatanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemPeralatanTipsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = modelPeralatan[position]

        Glide.with(context)
            .load(data.strImagePeralatan)
            .into(holder.binding.imagePeralatan)

        holder.binding.tvNamaAlat.text = data.strNamaPeralatan
        holder.binding.tvTipeAlat.text = data.strTipePeralatan

        holder.binding.cvListPeralatan.setOnClickListener {
            val intent = Intent(context, DetailPeralatanActivity::class.java)
            intent.putExtra(DetailPeralatanActivity.DETAIL_PERALATAN, modelPeralatan[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = modelPeralatan.size

    class ViewHolder(val binding: ListItemPeralatanTipsBinding) : RecyclerView.ViewHolder(binding.root)
}
