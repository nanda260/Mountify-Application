package com.azhar.infopendakian.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azhar.infopendakian.data.extensions.showCustomToast
import com.azhar.infopendakian.databinding.ListItemGunungBinding
import com.azhar.infopendakian.model.ModelGunung
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class AdminListGunungAdapter(
    private val context: Context,
    private val modelGunung: List<ModelGunung>,
    private val onEditClicked: (ModelGunung) -> Unit,
    private val onDeleteSuccess: () -> Unit
) : RecyclerView.Adapter<AdminListGunungAdapter.ViewHolder>() {

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
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle(data.strNamaGunung)
            dialog.setItems(arrayOf("Edit", "Delete")) { _, which ->
                when (which) {
                    0 -> onEditClicked(data)
                    1 -> deleteItem(data)
                }
            }
            dialog.show()
        }
    }

    private fun deleteItem(data: ModelGunung) {
        val db = FirebaseFirestore.getInstance()
        db.collection("gunung").whereEqualTo("nama", data.strNamaGunung)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("gunung").document(document.id)
                        .delete()
                        .addOnSuccessListener {
                            val updatedList = modelGunung.toMutableList()
                            updatedList.remove(data)
                            notifyDataSetChanged()
                            context.showCustomToast("Gunung berhasil dihapus")
                            onDeleteSuccess() // Callback untuk refresh list
                        }
                        .addOnFailureListener { e ->
                            context.showCustomToast("Error: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                context.showCustomToast("Error: ${e.message}")
            }
    }

    override fun getItemCount(): Int = modelGunung.size

    class ViewHolder(val binding: ListItemGunungBinding) : RecyclerView.ViewHolder(binding.root)
}
