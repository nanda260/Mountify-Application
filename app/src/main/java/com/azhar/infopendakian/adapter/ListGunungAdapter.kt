package com.azhar.infopendakian.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.azhar.infopendakian.R
import com.azhar.infopendakian.activities.DetailGunungActivity
import com.azhar.infopendakian.model.ModelGunung
import com.bumptech.glide.Glide
import com.azhar.infopendakian.databinding.ListItemGunungBinding

class ListGunungAdapter(
    private val context: Context,
    private val modelGunung: List<ModelGunung>
) : RecyclerView.Adapter<ListGunungAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemGunungBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = modelGunung[position]

        Glide.with(context)
            .load(data.strImageGunung)
            .into(holder.binding.imageGunung)

        holder.binding.tvNamaGunung.text = data.strNamaGunung
        holder.binding.tvLokasiGunung.text = data.strLokasiGunung

        holder.binding.cvListGunung.setOnClickListener {
            val intent = Intent(context, DetailGunungActivity::class.java)
            intent.putExtra(DetailGunungActivity.DETAIL_GUNUNG, modelGunung[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = modelGunung.size

    class ViewHolder(val binding: ListItemGunungBinding) : RecyclerView.ViewHolder(binding.root)
}
