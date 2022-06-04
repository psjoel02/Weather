package com.example.weather

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SevenDays(var arrList3: ArrayList<DailyRV>? = null, private var weatherRV3: RecyclerView? = null): Fragment(R.layout.seven_days_layout) {

    private var sevenDaysRL: RelativeLayout? = null
    private var loadPB: ProgressBar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.seven_days_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sevenDaysRL = view.findViewById(R.id.seven_days_RL)

        weatherRV3 = requireActivity().findViewById(R.id.weatherRV3)
        weatherRV3?.setHasFixedSize(true)
        arrList3 = arrayListOf<DailyRV>()
        weatherRV3?.adapter = DailyAdapter(arrList3!!)


        // Use the Kotlin extension in the fragment-ktx artifact
        setFragmentResultListener("SevenKey") { requestKey, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            val jsonData = bundle.getString("key")
            if (jsonData != null) {
                //Log.d("json data sent", jsonData)
                val json = JSONObject(jsonData)
                getWeatherInfo(json)
                sevenDaysRL?.visibility = View.VISIBLE
                loadPB?.visibility = View.INVISIBLE
            }

        }

    }

    private fun getWeatherInfo(json: JSONObject){

        arrList3?.clear()
        val df = DecimalFormat("####0.0")
        val forecastDaily: JSONArray = json.getJSONArray("days")
        for(i in 0 until 10){
            val forecast: JSONObject = forecastDaily.getJSONObject(i)
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

            var wRV = DailyRV(dailyDate, dailyWeather, R.drawable.cloudy, dailyHigh, dailyLow)
            try{
                wRV = DailyRV(dailyDate, dailyWeather, resources.getIdentifier(converted, "drawable", requireActivity().packageName),
                    dailyHigh, dailyLow)
            }catch(e: Exception){
                Log.d("Exception", e.toString())
            }

            arrList3?.add(wRV)
            weatherRV3?.adapter?.notifyDataSetChanged()

        }


        /*val format: DateFormat = SimpleDateFormat("hh:mm:ss")
        try {
            val date: Date = format.parse(rawRise)!!
            val date2: Date = format.parse(rawSet)!!
            val format2 = SimpleDateFormat("h:mm a")
            //sunriseVal?.text = format2.format(date)
            //sunsetVal?.text = format2.format(date2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }*/

        //based on different times of day, load different background image into backIV
        //add alerts using response.getJSONObject("alerts").getString("event, headline, and description")
        /*highTemp?.text = "↑".plus(forecast1.getString("tempmax")).plus("°F")
        lowTemp?.text = "↓".plus(forecast1.getString("tempmin")).plus("°F")
        weather?.text = forecast1.getString("conditions")
        currentTemp?.text = forecast1.getString("temp").plus("°F")
        feelsTemp?.text = "Feels like ".plus(feels).plus("°F")
        dateTime?.text = tomorrowDate
        description?.text = forecast1.getString("description")
        precipitationVal?.text = forecast1.getString("precip").plus(" in")
        humidityVal?.text = forecast1.getString("humidity").plus("%")
        windSpeedVal?.text = forecast1.getString("windspeed").plus(" mph")

        //val forecastHourly: JSONArray = forecast1.getJSONArray("hours")
        //loadArr(forecastHourly)*/

    }

    /*private fun loadArr(forecastHourly: JSONArray){
        for (i in 0 until forecastHourly.length()) {
            val currentObj: JSONObject = forecastHourly.getJSONObject(i)
            val hrTemp: String = currentObj.getString("temp").plus("°F")
            val hrIcon: String = currentObj.getString("icon")
            val hrPrecip: String = currentObj.getString("precipprob").plus("%")
            val hrWind: String = currentObj.getString("windspeed").plus("mph")
            val converted: String = hrIcon.replace('-', '_')
            //Log.d("converted:", converted)
            var wRV = Daily(hrTemp, R.drawable.cloudy, hrPrecip, hrWind)
            try{
                wRV = Daily(hrTemp, resources.getIdentifier(converted, "drawable", requireActivity().packageName), hrPrecip, hrWind)
            }catch(e: Exception){
                Log.d("Exception", e.toString())
            }
            arrList3?.add(wRV)
            weatherRV3?.adapter?.notifyDataSetChanged()
        }
    }*/

    override fun onResume() {
        super.onResume()
    }

}