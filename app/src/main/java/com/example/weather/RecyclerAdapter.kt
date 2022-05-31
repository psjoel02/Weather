package com.example.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList


class RecyclerAdapter(private val weatherList: ArrayList<WeatherRV>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {


    private var hours = arrayOf("12AM", "  1AM", "  2AM", "  3AM", "  4AM", "  5AM", "  6AM", "  7AM", "  8AM", "  9AM", "10AM", "11AM", "12AM",
                               "  1PM", "  2PM", "  3PM", "  4PM", "  5PM", "  6PM", "  7PM", "  8PM", "  9PM", "10PM", "11PM", "12AM")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.weather_rv_main, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        //addImages()
        val currentHour = weatherList[position]
        holder.timeRV.text = hours[position]
        holder.tempRV.text = currentHour.temperature
        holder.precipRV.text = currentHour.precip
        holder.windRV.text = currentHour.wind
        holder.imgRV.setImageResource(currentHour.icon)

    }

    override fun getItemCount(): Int {
        //Log.d("hello", weatherList.size.toString())
        return weatherList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var timeRV: TextView = itemView.findViewById(R.id.timeRV)
        var tempRV: TextView = itemView.findViewById(R.id.tempRV)
        var precipRV: TextView = itemView.findViewById(R.id.precipRV)
        var windRV: TextView = itemView.findViewById(R.id.windRV)
        var imgRV: ImageView = itemView.findViewById(R.id.imgRV)
    }
}