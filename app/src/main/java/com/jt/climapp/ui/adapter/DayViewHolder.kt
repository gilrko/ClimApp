package com.jt.climapp.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jt.climapp.R
import com.jt.climapp.databinding.ItemDayBinding
import com.jt.climapp.ui.data.model.Daily
import com.jt.climapp.ui.utils.Connectivity
import java.lang.StringBuilder

class DayViewHolder (view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemDayBinding.bind(view)
    fun bind(day:Daily){
        val stringBuilder = StringBuilder()
        Glide.with(itemView).load(R.drawable.gifweather).into(binding.imDay)
        stringBuilder.append(Connectivity.getShortDate(day?.dt, "Date"))
        binding.textViewDay.text = stringBuilder.toString()
        stringBuilder.clear()
        stringBuilder.append("Max:")
            .append(String.format("%.0f",day?.temp?.max))
            .append("° C")
        binding.textViewMax.text = stringBuilder.toString()
        stringBuilder.clear()
        stringBuilder.append("Max:")
            .append(String.format("%.0f",day?.temp?.min))
            .append("° C")
        binding.textViewMin.text = stringBuilder.toString()
        stringBuilder.clear()
    }
}