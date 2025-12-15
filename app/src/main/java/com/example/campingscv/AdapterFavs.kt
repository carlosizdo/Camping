package com.example.campingscv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campingscv.databinding.TextRowItemBinding

class AdapterFavs(private val dataSet: ArrayList<Camping>, private val listener: CampingFavsListener) : RecyclerView.Adapter<AdapterFavs.MyFavsViewHolder>() {

    class MyFavsViewHolder(val binding: TextRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFavsViewHolder {
        return MyFavsViewHolder(
            TextRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyFavsViewHolder, position: Int) {
        holder.binding.apply {
            val camping = dataSet[position]
            textView.text = camping.nombre
            textView2.text = "Plazas: " + camping.plazas.toString()
        }
        holder.itemView.setOnClickListener {
            listener.onItemClick(dataSet[position])
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    interface CampingFavsListener {
        fun onItemClick(camping: Camping)
    }

    fun updateData(newData: List<Camping>) {
        dataSet.clear()
        dataSet.addAll(newData)
        notifyDataSetChanged()
    }
}