package com.example.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList


class DailyAdapter(private val weatherList: ArrayList<DailyRV>): RecyclerView.Adapter<DailyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.daily_rv_main, parent, false)
        //use layout daily_rv_main for recyclerview
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: DailyAdapter.ViewHolder, position: Int) {
        val currentDay = weatherList[position]
        holder.dayRV.text = currentDay.day
        holder.weatherRV.text = currentDay.weather
        holder.highRV.text = currentDay.high
        holder.lowRV.text = currentDay.low
        holder.imgRV.setImageResource(currentDay.icon)
        //set values for layout using weatherList parameters passed in
    }

    override fun getItemCount(): Int {
        return weatherList.size
        //get item count using weather size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var dayRV: TextView = itemView.findViewById(R.id.dayRV)
        var weatherRV: TextView = itemView.findViewById(R.id.weatherRV)
        var highRV: TextView = itemView.findViewById(R.id.highRV)
        var lowRV: TextView = itemView.findViewById(R.id.lowRV)
        var imgRV: ImageView = itemView.findViewById(R.id.imgRV)
        //initialize viewholder variables to respective Views in layout
    }
}