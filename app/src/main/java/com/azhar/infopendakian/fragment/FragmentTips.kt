package com.azhar.infopendakian.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.azhar.infopendakian.R
import com.azhar.infopendakian.adapter.TipsAdapter
import com.azhar.infopendakian.databinding.FragmentTipsBinding
import com.azhar.infopendakian.model.ModelPeralatan
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets

class FragmentTips : Fragment() {

    private var modelPeralatan: MutableList<ModelPeralatan> = mutableListOf()
    private lateinit var tipsAdapter: TipsAdapter
    private var _binding: FragmentTipsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tipsAdapter = TipsAdapter(requireContext(), modelPeralatan)
        binding.rvTips.layoutManager = LinearLayoutManager(context)
        binding.rvTips.adapter = tipsAdapter
        binding.rvTips.setHasFixedSize(true)

        // Get data nama peralatan
        getPeralatanGunung()
    }

    private fun getPeralatanGunung() {
        try {
            val stream = requireContext().assets.open("peralatan.json")
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            val strContent = String(buffer, StandardCharsets.UTF_8)
            try {
                val jsonObject = JSONObject(strContent)
                val jsonArray = jsonObject.getJSONArray("peralatan")
                for (i in 0 until jsonArray.length()) {
                    val jsonObjectData = jsonArray.getJSONObject(i)
                    val dataApi = ModelPeralatan().apply {
                        strNamaPeralatan = jsonObjectData.getString("nama")
                        strImagePeralatan = jsonObjectData.getString("image_url")
                        strTipePeralatan = jsonObjectData.getString("tipe")
                        strDeskripsiPeralatan = jsonObjectData.getString("deskripsi")
                        strTipsPeralatan = jsonObjectData.getString("tips")
                    }
                    modelPeralatan.add(dataApi)
                }
                tipsAdapter.notifyDataSetChanged()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Oops, ada yang tidak beres. Coba ulangi beberapa saat lagi.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
