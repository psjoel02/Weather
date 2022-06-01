package com.example.weather

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


open class Today(var arrList: ArrayList<WeatherRV>? = null, private var weatherRV: RecyclerView? = null): Fragment(R.layout.today_layout) {

    private var searchLayout: TextInputLayout? = null
    private var searchEdit: TextInputEditText? = null
    private var currentTemp: TextView? = null; private var weather: TextView? = null
    private var highTemp: TextView? = null; private var lowTemp: TextView? = null
    private var feelsTemp: TextView? = null; private var dateTime: TextView? = null; private var description: TextView? = null
    private var sunriseVal: TextView? = null; private var sunsetVal: TextView? = null; private var precipitationVal: TextView? = null
    private var humidityVal: TextView? = null; private var windSpeedVal: TextView? = null; private var pressureVal: TextView? = null
    private var searchIcon: ImageView? = null; private var backIV: ImageView? = null; private var weatherImg: ImageView? = null
    private var locationManger: LocationManager? = null
    private var loadPB: ProgressBar? = null
    private var pCode: Int = 1
    private var cityName: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var todayRL: RelativeLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        return inflater.inflate(R.layout.today_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //fullscreen
        //loadPB = findViewById(R.id.loadingPB)
        weatherImg = view.findViewById(R.id.weatherImg)
        highTemp = view.findViewById(R.id.highTemp)
        lowTemp = view.findViewById(R.id.lowTemp)
        feelsTemp = view.findViewById(R.id.feelsLike)
        description = view.findViewById(R.id.description)
        dateTime = view.findViewById(R.id.dateTime)
        sunriseVal = view.findViewById(R.id.sunriseVal)
        sunsetVal = view.findViewById(R.id.sunsetVal)
        precipitationVal = view.findViewById(R.id.precipitationVal)
        humidityVal = view.findViewById(R.id.humidityVal)
        windSpeedVal = view.findViewById(R.id.windSpeedVal)
        pressureVal = view.findViewById(R.id.pressureVal)
        searchLayout = view.findViewById(R.id.searchLayout)
        searchEdit = view.findViewById(R.id.searchEdit)
        searchIcon = view.findViewById(R.id.search_Icon)
        currentTemp = view.findViewById(R.id.currentTemp)
        weather = view.findViewById(R.id.desc)
        backIV = view.findViewById(R.id.backIV)
        loadPB = view.findViewById(R.id.loadingPB)
        todayRL = view.findViewById(R.id.todayRL)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        locationManger = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION), pCode)
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if(location != null){
                    cityName = getCityName(location.longitude, location.latitude)
                    searchEdit?.setText(cityName)
                    getWeatherInfo(cityName!!)
                    //automatically use location to find weather
                }
                else{
                    //if location is unknown, use last entered city name
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireActivity())
                    val city: String? = sharedPrefs.getString("cityName", "Dallas")
                    try{
                        cityName = city
                        searchEdit?.setText(city)
                        getWeatherInfo(cityName!!)
                    }catch(e: Exception){
                        //if last entered city is unknown, set default weather to Dallas
                        cityName = "Dallas"
                        searchEdit?.setText(city)
                        getWeatherInfo(cityName!!)
                    }
                }
            }
        weatherRV = requireActivity().findViewById(R.id.weatherRV)
        weatherRV?.setHasFixedSize(true)
        arrList = arrayListOf<WeatherRV>()
        weatherRV?.adapter = RecyclerAdapter(arrList!!)


        searchIcon?.setOnClickListener {
            val city: String = searchEdit?.text.toString()
            if(city.isEmpty()){
                Toast.makeText(requireActivity(), "Enter city name", Toast.LENGTH_SHORT).show()
            }
            else{
                //displayCity?.text = cityName
                getWeatherInfo(city)

            }
        }

        searchEdit?.setOnEditorActionListener { v, actionId, event ->
            val handled = false
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val city: String = searchEdit?.text.toString()
                if (city.isEmpty()) {
                    Toast.makeText(requireActivity(), "Enter city name", Toast.LENGTH_SHORT).show()
                } else {
                    //displayCity?.text = cityName
                    getWeatherInfo(city)
                    weatherRV?.adapter?.notifyDataSetChanged()
                }
            }
            handled
        }

        todayRL?.visibility = View.VISIBLE
        loadPB?.visibility = View.INVISIBLE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == pCode){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireActivity(), "Permissions granted", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireActivity(), "Please provide permissions", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    private fun getCityName(longitude: Double, latitude: Double): String{
        val cityName = "Not Found"
        val geoCD = Geocoder(requireActivity(), Locale.getDefault())
        val addresses: List<Address> = geoCD.getFromLocation(latitude, longitude, 10)
        if (addresses.isNotEmpty()) {
            for (adr in addresses) {
                if (adr.locality != null && adr.locality.isNotEmpty()) {
                    return adr.locality
                }
            }
        }

        return cityName
    }

    private fun getWeatherInfo(city: String){
        //replace unit group to metric outside of US
        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/$city?unitGroup=us&iconSet=icons2&key=DR7SE842V9TC3UK6854QN4YP7&contentType=json"

        //displayCity?.text = city
        val mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val mEditor: SharedPreferences.Editor = mPrefs.edit()
        mEditor.putString("cityName", city).apply()
        val rq: RequestQueue = Volley.newRequestQueue(requireActivity())
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                //homeRL.setVisibility(View.VISIBLE)
                arrList?.clear()
                try{
                    val feels: String = response.getJSONObject("currentConditions").getString("feelslike")
                    val icon: String = response.getJSONObject("currentConditions").getString("icon")
                    val datef = SimpleDateFormat("MMM d, h:mm aa")
                    val date = datef.format(Calendar.getInstance().time)
                    val rawRise: String = response.getJSONObject("currentConditions").getString("sunrise")
                    val rawSet: String = response.getJSONObject("currentConditions").getString("sunset")
                    val format: DateFormat = SimpleDateFormat("hh:mm:ss")
                    try {
                        val date: Date = format.parse(rawRise)
                        val date2: Date = format.parse(rawSet)
                        val format2 = SimpleDateFormat("h:mm a")
                        sunriseVal?.text = format2.format(date)
                        sunsetVal?.text = format2.format(date2)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                    loadImg(icon)
                    //based on different times of day, load different background image into backIV
                    //add alerts using response.getJSONObject("alerts").getString("event, headline, and description")
                    weather?.text = response.getJSONObject("currentConditions").getString("conditions")
                    currentTemp?.text = response.getJSONObject("currentConditions").getString("temp").plus("°F")
                    feelsTemp?.text = "Feels like ".plus(feels).plus("°F")
                    dateTime?.text = date
                    description?.text = response.getString("description")
                    precipitationVal?.text = response.getJSONObject("currentConditions").getString("precip").plus(" in")
                    humidityVal?.text = response.getJSONObject("currentConditions").getString("humidity").plus("%")
                    windSpeedVal?.text = response.getJSONObject("currentConditions").getString("windspeed").plus(" mph")
                    pressureVal?.text = response.getJSONObject("currentConditions").getString("pressure").plus(" inHg")

                    val forecastDaily: JSONArray = response.getJSONArray("days")
                    val forecast0: JSONObject = forecastDaily.getJSONObject(0)
                    highTemp?.text = "↑".plus(forecast0.getString("tempmax")).plus("°F")
                    lowTemp?.text = "↓".plus(forecast0.getString("tempmin")).plus("°F")

                    val forecastHourly: JSONArray = forecast0.getJSONArray("hours")

                    for (i in 0 until forecastHourly.length()) {
                        val currentObj: JSONObject = forecastHourly.getJSONObject(i)
                        val hrTemp: String = currentObj.getString("temp").plus("°F")
                        val hrIcon: String = currentObj.getString("icon")
                        val hrPrecip: String = currentObj.getString("precip").plus("%")
                        val hrWind: String = currentObj.getString("windspeed").plus("mph")
                        val converted: String = hrIcon.replace('-', '_')
                        //Log.d("icon:", hrIcon)
                        //Log.d("converted:", converted)
                        val wRV = WeatherRV(hrTemp, resources.getIdentifier(converted, "drawable", requireActivity().packageName), hrPrecip, hrWind)
                        arrList?.add(wRV)
                        weatherRV?.adapter?.notifyDataSetChanged()
                    }

                }catch(e: JSONException){
                    e.printStackTrace()
                }
                //Toast.makeText(this, city, Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(requireActivity(), "Please enter a valid city name", Toast.LENGTH_SHORT).show()
            }


        )
        rq.add(jsonObjectRequest)
    }

    private fun loadImg(icon: String){
        when(icon){
            "snow" ->  {
                Picasso.get().load(R.drawable.snow).into(weatherImg)
                Picasso.get().load(R.drawable.snow_bg).into(backIV)
            }
            "snow-showers-day" -> {
                Picasso.get().load(R.drawable.snow).into(weatherImg)
                Picasso.get().load(R.drawable.snow_bg).into(backIV)
            }
            "snow-showers-night" -> {
                Picasso.get().load(R.drawable.snow).into(weatherImg)
                Picasso.get().load(R.drawable.snow_bg).into(backIV)
            }
            "thunder-rain" -> {
                Picasso.get().load(R.drawable.thunder_rain).into(weatherImg)
                Picasso.get().load(R.drawable.thunder_rain_bg).into(backIV)
            }
            "thunder-showers-day" -> {
                Picasso.get().load(R.drawable.thunder_showers_day).into(weatherImg)
                Picasso.get().load(R.drawable.thunder_showers_day_bg).into(backIV)
            }
            "thunder-showers-night" -> {
                Picasso.get().load(R.drawable.thunder_rain).into(weatherImg)
                backIV?.setImageResource(R.drawable.thunder_rain_bg)
            }
            "rain" -> {
                Picasso.get().load(R.drawable.rain).into(weatherImg)
                backIV?.setImageResource(R.drawable.rain_bg)
            }
            "showers-day" -> {
                Picasso.get().load(R.drawable.thunder_showers_day).into(weatherImg)
                backIV?.setImageResource(R.drawable.thunder_showers_day_bg)
            }
            "showers-night" -> {
                Picasso.get().load(R.drawable.rain).into(weatherImg)
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


}
