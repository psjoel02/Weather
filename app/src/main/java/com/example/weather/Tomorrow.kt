package com.example.weather

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

class Tomorrow(var arrList2: ArrayList<WeatherRV>? = null, private var weatherRV2: RecyclerView? = null): Fragment(R.layout.tomorrow_layout) {

    private var currentTemp: TextView? = null; private var weather: TextView? = null
    private var highTemp: TextView? = null; private var lowTemp: TextView? = null; private var location: TextView? = null
    private var feelsTemp: TextView? = null; private var dateTime: TextView? = null; private var description: TextView? = null
    private var sunriseVal: TextView? = null; private var sunsetVal: TextView? = null; private var precipitationVal: TextView? = null
    private var humidityVal: TextView? = null; private var windSpeedVal: TextView? = null; private var pressureVal: TextView? = null
    private var backIV: ImageView? = null; private var weatherImg: ImageView? = null
    private var loadPB: ProgressBar? = null
    private var tomorrowRL: RelativeLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.tomorrow_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //fullscreen
        weatherImg = view.findViewById(R.id.weatherImg2)
        highTemp = view.findViewById(R.id.highTemp2)
        lowTemp = view.findViewById(R.id.lowTemp2)
        feelsTemp = view.findViewById(R.id.feelsLike2)
        description = view.findViewById(R.id.description2)
        dateTime = view.findViewById(R.id.dateTime2)
        sunriseVal = view.findViewById(R.id.sunriseVal2)
        sunsetVal = view.findViewById(R.id.sunsetVal2)
        precipitationVal = view.findViewById(R.id.precipitationVal2)
        humidityVal = view.findViewById(R.id.humidityVal2)
        windSpeedVal = view.findViewById(R.id.windSpeedVal2)
        pressureVal = view.findViewById(R.id.pressureVal2)
        currentTemp = view.findViewById(R.id.currentTemp2)
        weather = view.findViewById(R.id.desc2)
        backIV = view.findViewById(R.id.backIV2)
        loadPB = view.findViewById(R.id.loadingPB2)
        tomorrowRL = view.findViewById(R.id.tomorrowRL)
        location = view.findViewById(R.id.location)

        weatherRV2 = requireActivity().findViewById(R.id.weatherRV2)
        weatherRV2?.setHasFixedSize(true)
        arrList2 = arrayListOf<WeatherRV>()
        weatherRV2?.adapter = RecyclerAdapter(arrList2!!)

        // Use the Kotlin extension in the fragment-ktx artifact
        setFragmentResultListener("TomKey") { requestKey, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            val jsonData = bundle.getString("key")
            if (jsonData != null) {
                //Log.d("json data sent", jsonData)
                val json = JSONObject(jsonData)
                location?.text = json.getString("resolvedAddress")
                getWeatherInfo(json)

                tomorrowRL?.visibility = View.VISIBLE
                loadPB?.visibility = View.INVISIBLE
            }

        }

    }

    private fun getWeatherInfo(json: JSONObject){

        arrList2?.clear()
        val tomorrowf = SimpleDateFormat("EEEE, MMM d")
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowDate = tomorrowf.format(calendar.time)

        val forecastDaily: JSONArray = json.getJSONArray("days")
        val forecast1: JSONObject = forecastDaily.getJSONObject(1)
        highTemp?.text = "↑".plus(forecast1.getString("tempmax")).plus("°F")
        lowTemp?.text = "↓".plus(forecast1.getString("tempmin")).plus("°F")
        val feels: String = forecast1.getString("feelslike")
        val icon: String = forecast1.getString("icon")
        Log.d("icon:", icon)
        var pressure: Double = forecast1.getString("pressure").toDouble()
        pressure /= 33.864

        val rawRise: String = forecast1.getString("sunrise")
        val rawSet: String = forecast1.getString("sunset")
        val format: DateFormat = SimpleDateFormat("hh:mm:ss")
        try {
            val date: Date = format.parse(rawRise)!!
            val date2: Date = format.parse(rawSet)!!
            val format2 = SimpleDateFormat("h:mm a")
            sunriseVal?.text = format2.format(date)
            sunsetVal?.text = format2.format(date2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        loadImg(icon)
        //based on different times of day, load different background image into backIV
        //add alerts using response.getJSONObject("alerts").getString("event, headline, and description")
        weather?.text = forecast1.getString("conditions")
        currentTemp?.text = forecast1.getString("temp").plus("°F")
        feelsTemp?.text = "Feels like ".plus(feels).plus("°F")
        dateTime?.text = tomorrowDate
        description?.text = forecast1.getString("description")
        precipitationVal?.text = forecast1.getString("precip").plus(" in")
        humidityVal?.text = forecast1.getString("humidity").plus("%")
        windSpeedVal?.text = forecast1.getString("windspeed").plus(" mph")
        val df = DecimalFormat("####0.0")
        pressureVal?.text = df.format(pressure).toString().plus(" inHg")

        val forecastHourly: JSONArray = forecast1.getJSONArray("hours")
        loadArr(forecastHourly)

    }

    private fun loadArr(forecastHourly: JSONArray){
        for (i in 0 until forecastHourly.length()) {
            val currentObj: JSONObject = forecastHourly.getJSONObject(i)
            val hrTemp: String = currentObj.getString("temp").plus("°F")
            val hrIcon: String = currentObj.getString("icon")
            val hrPrecip: String = currentObj.getString("precipprob").plus("%")
            val hrWind: String = currentObj.getString("windspeed").plus("mph")
            val converted: String = hrIcon.replace('-', '_')
            //Log.d("converted:", converted)
            var wRV = WeatherRV(hrTemp, R.drawable.cloudy, hrPrecip, hrWind)
            try{
                wRV = WeatherRV(hrTemp, resources.getIdentifier(converted, "drawable", requireActivity().packageName), hrPrecip, hrWind)
            }catch(e: Exception){
                Log.d("Exception", e.toString())
            }
            arrList2?.add(wRV)
            weatherRV2?.adapter?.notifyDataSetChanged()
        }
    }


    private fun loadImg(icon: String){
        when(icon){
            "snow" ->  {
                Picasso.get().load(R.drawable.snow).into(weatherImg)
                Picasso.get().load(R.drawable.snow_bg).into(backIV)
            }
            "snow-showers-day" -> {
                Picasso.get().load(R.drawable.snow_showers_day).into(weatherImg)
                Picasso.get().load(R.drawable.snow_bg).into(backIV)
            }
            "snow-showers-night" -> {
                Picasso.get().load(R.drawable.snow_showers_night).into(weatherImg)
                Picasso.get().load(R.drawable.snow_bg).into(backIV)
            }
            "thunder-rain" -> {
                Picasso.get().load(R.drawable.thunder_rain).into(weatherImg)
                Picasso.get().load(R.drawable.thunder_rain_bg).into(backIV)
            }
            "thunder-showers-day" -> {
                Picasso.get().load(R.drawable.thunder_showers_day).into(weatherImg)
                Picasso.get().load(R.drawable.thunder_rain_bg).into(backIV)
            }
            "thunder-showers-night" -> {
                Picasso.get().load(R.drawable.thunder_showers_night).into(weatherImg)
                backIV?.setImageResource(R.drawable.thunder_rain_bg)
            }
            "rain" -> {
                Picasso.get().load(R.drawable.rain).into(weatherImg)
                backIV?.setImageResource(R.drawable.rain_bg)
            }
            "showers-day" -> {
                Picasso.get().load(R.drawable.showers_day).into(weatherImg)
                backIV?.setImageResource(R.drawable.thunder_showers_day_bg)
            }
            "showers-night" -> {
                Picasso.get().load(R.drawable.showers_night).into(weatherImg)
                backIV?.setImageResource(R.drawable.rain_bg)
            }
            "fog" -> {
                Picasso.get().load(R.drawable.fog).into(weatherImg)
                backIV?.setImageResource(R.drawable.fog_bg)
            }
            "wind" -> {
                Picasso.get().load(R.drawable.wind).into(weatherImg)
                backIV?.setImageResource(R.drawable.wind_bg)
            }
            "cloudy" -> {
                Picasso.get().load(R.drawable.cloudy).into(weatherImg)
                backIV?.setImageResource(R.drawable.cloudy_bg)
            }
            "partly-cloudy-day" -> {
                Picasso.get().load(R.drawable.partly_cloudy_day).into(weatherImg)
                backIV?.setImageResource(R.drawable.partly_cloudy_day_bg)
            }
            "partly-cloudy-night" -> {
                Picasso.get().load(R.drawable.partly_cloudy_night).into(weatherImg)
                backIV?.setImageResource(R.drawable.partly_cloudy_night_bg)
            }
            "clear-day" -> {
                Picasso.get().load(R.drawable.clear_day).into(weatherImg)
                backIV?.setImageResource(R.drawable.clear_day_bg)
            }
            "clear-night" -> {
                Picasso.get().load(R.drawable.clear_night).into(weatherImg)
                backIV?.setImageResource(R.drawable.clear_night_bg)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }



}