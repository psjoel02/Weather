package com.example.weather

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class TenDays(var arrList3: ArrayList<DailyRV>? = null, private var weatherRV3: RecyclerView? = null): Fragment(R.layout.ten_days_layout) {

    private var tenDaysRL: RelativeLayout? = null
    private var loadPB: ProgressBar? = null
    private var backIV: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //set layout to ten_days_layout
        return inflater.inflate(R.layout.ten_days_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tenDaysRL = view.findViewById(R.id.ten_days_RL)
        backIV = view.findViewById(R.id.backIV3)
        weatherRV3 = requireActivity().findViewById(R.id.weatherRV3)
        weatherRV3?.setHasFixedSize(true)
        arrList3 = arrayListOf<DailyRV>()
        //create arraylist of type DailyRV
        weatherRV3?.adapter = DailyAdapter(arrList3!!)


        // Use the Kotlin extension in the fragment-ktx artifact
        setFragmentResultListener("SevenKey") { requestKey, bundle ->
            val jsonData = bundle.getString("key")
            if (jsonData != null) {
                //Log.d("json data sent", jsonData)
                val json = JSONObject(jsonData)
                getWeatherInfo(json)
                tenDaysRL?.visibility = View.VISIBLE
                loadPB?.visibility = View.INVISIBLE
            }
            //use setFragmentResultListener to obtain json data from Today fragment
        }

    }

    private fun getWeatherInfo(json: JSONObject){

        //clear past data from recyclerview
        arrList3?.clear()

        val df = DecimalFormat("####0.0")
        val forecastDaily: JSONArray = json.getJSONArray("days")
        var icon = "cloudy"

        for(i in 0 until 10){
            val forecast: JSONObject = forecastDaily.getJSONObject(i)
            if(i == 0){
                icon = forecast.getString("icon")
            }
            var dailyDate: String
            val dailyWeather = forecast.getString("conditions")
            val dailyIcon = forecast.getString("icon")
            val converted: String = dailyIcon.replace('-', '_')
            val highVal = df.format(forecast.getString("tempmax").toDouble())
            val lowVal = df.format(forecast.getString("tempmin").toDouble())
            val dailyHigh: String = "↑".plus(highVal.toString()).plus("°F")
            val dailyLow: String = "↓".plus(lowVal.toString()).plus("°F")
            val dailyf = SimpleDateFormat("EEEE, MMM d")
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, i)
            dailyf.format(calendar.time)
            dailyDate = if(i == 0){
                "Today"
            } else{
                dailyf.format(calendar.time)
            }
            loadImg(icon)

            var wRV = DailyRV(dailyDate, dailyWeather, R.drawable.cloudy, dailyHigh, dailyLow)
            //set default icon as cloudy in case drawable fails to load

            try{
                wRV = DailyRV(dailyDate, dailyWeather, resources.getIdentifier(converted, "drawable", requireActivity().packageName),
                    dailyHigh, dailyLow)
            }catch(e: Exception){
                Log.d("Exception", e.toString())
            }

            arrList3?.add(wRV)
        }
        weatherRV3?.adapter?.notifyDataSetChanged()

    }

    private fun loadImg(icon: String){
        //hide loading background and replace weather icon and background with icon according to API
        when(icon){
            "snow" -> backIV?.setImageResource(R.drawable.snow_bg)

            "snow-showers-day" -> backIV?.setImageResource(R.drawable.snow_bg)

            "snow-showers-night" -> backIV?.setImageResource(R.drawable.snow_bg)

            "thunder-rain" -> backIV?.setImageResource(R.drawable.thunder_rain_bg)

            "thunder-showers-day" -> backIV?.setImageResource(R.drawable.thunder_rain_bg)

            "thunder-showers-night" -> backIV?.setImageResource(R.drawable.thunder_rain_bg)

            "rain" -> backIV?.setImageResource(R.drawable.rain_bg)

            "showers-day" -> backIV?.setImageResource(R.drawable.thunder_showers_day_bg)

            "showers-night" -> backIV?.setImageResource(R.drawable.rain_bg)

            "fog" -> backIV?.setImageResource(R.drawable.fog_bg)

            "wind" -> backIV?.setImageResource(R.drawable.wind_bg)

            "cloudy" -> backIV?.setImageResource(R.drawable.cloudy_bg)

            "partly-cloudy-day" -> backIV?.setImageResource(R.drawable.partly_cloudy_day_bg)

            "partly-cloudy-night" -> backIV?.setImageResource(R.drawable.partly_cloudy_night_bg)

            "clear-day" -> backIV?.setImageResource(R.drawable.clear_day_bg)

            "clear-night" -> backIV?.setImageResource(R.drawable.clear_night_bg)
        }
    }


}