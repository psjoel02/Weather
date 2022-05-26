package com.example.weather



import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
/*
class RecyclerAdapter(private var weatherRVAL: MutableList<WeatherRV>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.weather_rv_main, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {

        var modal: WeatherRV = weatherRVAL[position]
        var icon: String = modal.icon
        when(icon){
            "snow" ->  Picasso.get().load(R.drawable.snow).into(holder.imgRV)
            "snow-showers-day" -> Picasso.get().load(R.drawable.snow).into(holder.imgRV)
            "snow-showers-night" -> Picasso.get().load(R.drawable.snow).into(holder.imgRV)
            "thunder-rain" -> Picasso.get().load(R.drawable.thunder_rain).into(holder.imgRV)
            "thunder-showers-day" -> Picasso.get().load(R.drawable.thunder_showers_day).into(holder.imgRV)
            "thunder-showers-night" -> Picasso.get().load(R.drawable.thunder_rain).into(holder.imgRV)
            "rain" -> Picasso.get().load(R.drawable.rain).into(holder.imgRV)
            "showers-day" -> Picasso.get().load(R.drawable.thunder_showers_day).into(holder.imgRV)
            "showers-night" -> Picasso.get().load(R.drawable.rain).into(holder.imgRV)
            "fog" -> Picasso.get().load(R.drawable.fog).into(holder.imgRV)
            "wind" -> Picasso.get().load(R.drawable.wind).into(holder.imgRV)
            "cloudy" -> Picasso.get().load(R.drawable.cloudy).into(holder.imgRV)
            "partly-cloudy-day" -> Picasso.get().load(R.drawable.partly_cloudy_day).into(holder.imgRV)
            "partly-cloudy-night" -> Picasso.get().load(R.drawable.partly_cloudy_night).into(holder.imgRV)
            "clear-day" -> Picasso.get().load(R.drawable.clear_day).into(holder.imgRV)
            "clear-night" -> Picasso.get().load(R.drawable.clear_night).into(holder.imgRV)
        }
        holder.tempRV.text = modal.temperature.plus("°F")
        Picasso.get().load(R.drawable.clear_day).into(holder.imgRV)
        holder.windRV.text = modal.windSpeed.plus("MPH")
        var into = SimpleDateFormat("yyyy-MM-dd hh:mm")
        var out = SimpleDateFormat("hh:mm aa")
        try{
            var time = into.parse(modal.time) as Date
            holder.timeRV.text = (out.format(time))
        }catch(e: ParseException){
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return weatherRVAL.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var timeRV: TextView = itemView.findViewById(R.id.timeRV)
        var tempRV: TextView = itemView.findViewById(R.id.tempRV)
        var windRV: TextView = itemView.findViewById(R.id.imgRV)
        var imgRV: ImageView = itemView.findViewById(R.id.windRV)


    }
}*/

class RecyclerAdapter(): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var hours = arrayOf("12AM", "1AM", "2AM", "3AM", "4AM", "5AM", "6AM", "7AM", "8AM", "9AM", "10AM", "11AM", "12AM" +
                               "1PM", "2PM", "3PM", "4PM", "5PM", "6PM", "7PM", "8PM", "9PM", "10PM", "11PM", "12AM")
    private var details = arrayOf("70°F", "70°F", "70°F", "70°F", "70°F", "70°F", "70°F", "70°F", "70°F", "70°F", "70°F", "70°F", "70°F",
                                  "70°F", "70°F", "70°F", "70°F", "70°F", "70°F", "70°F", "70°F", "70°F", "70°F", "70°F", "70°F")
    private var images = arrayOf(R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day,
        R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day,
        R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day, R.drawable.clear_day,)

    private var details2 = arrayOf("10mph", "10mph", "10mph", "10mph", "10mph", "10mph", "10mph", "10mph", "10mph", "10mph", "10mph", "10mph", "10mph",
    "10mph", "10mph", "10mph", "10mph", "10mph", "10mph", "10mph", "10mph", "10mph", "10mph", "10mph", "10mph")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.weather_rv_main, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.timeRV.text = hours[position]
        holder.tempRV.text = details[position]
        holder.imgRV.setImageResource(images[position])
        holder.windRV.text = details2[position]
    }

    override fun getItemCount(): Int {
        return hours.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var timeRV: TextView = itemView.findViewById(R.id.timeRV)
        var tempRV: TextView = itemView.findViewById(R.id.tempRV)
        var windRV: TextView = itemView.findViewById(R.id.windRV)
        var imgRV: ImageView = itemView.findViewById(R.id.imgRV)
    }
}