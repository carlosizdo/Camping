package com.example.campingscv

import android.database.DataSetObservable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campingscv.databinding.TextRowItemBinding

class MyAdapter(private val dataSet: ArrayList<Camping>, private val listener: CampingListener) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: TextRowItemBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            TextRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            val camping = dataSet[position]
            textView.text = camping.nombre
            textView2.text = "Plazas ${camping.plazas}"
        }
        holder.itemView.setOnClickListener {
            listener.onItemClick(dataSet[position])
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    interface CampingListener {
        fun onItemClick(camping: Camping)
    }

    fun updateData(newData: List<Camping>) {
        dataSet.clear()
        dataSet.addAll(newData)
        notifyDataSetChanged()
    }

}