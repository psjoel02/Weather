package com.example.weather

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
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
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


open class Today(var arrList: ArrayList<WeatherRV>? = null, private var weatherRV: RecyclerView? = null): Fragment(R.layout.today_layout) {

    private var searchLayout: TextInputLayout? = null; private var searchEdit: TextInputEditText? = null
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        locationManger = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION), pCode)
        }
        //use locationManager to request permissions if permission for location has not been granted

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if(location != null){
                    cityName = getCityName(location.longitude, location.latitude)
                    searchEdit?.setText(cityName)
                    getWeatherInfo(cityName!!)
                    //automatically use location to find weather if location is turned on
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

        //set layout to today_layout

        return inflater.inflate(R.layout.today_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPB = view.findViewById(R.id.loadingPB)
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

        weatherRV = requireActivity().findViewById(R.id.weatherRV)
        weatherRV?.setHasFixedSize(true)
        arrList = arrayListOf<WeatherRV>()
        //create arraylist of type WeatherRV
        weatherRV?.adapter = RecyclerAdapter(arrList!!)
        loadPB?.bringToFront()

        searchIcon?.setOnClickListener {
            val city: String = searchEdit?.text.toString()
            if(city.isEmpty()){
                Toast.makeText(requireActivity(), "Enter city name", Toast.LENGTH_SHORT).show()
                //if editText is empty, toast that name is required
            }
            else{
                //else get Weather info for specified city
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
                    //if city is not empty, get weather info and update recyclerview
                    getWeatherInfo(city)
                    weatherRV?.adapter?.notifyDataSetChanged()
                }
            }
            handled
        }


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
        //geolocate city using geocoder
        return cityName
    }

    private fun getWeatherInfo(city: String){

        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/$city?unitGroup=us&iconSet=icons2&key=DR7SE842V9TC3UK6854QN4YP7&contentType=json"
        val mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val mEditor: SharedPreferences.Editor = mPrefs.edit()
        mEditor.putString("cityName", city).apply()
        //if city name is successful, save in sharedpreferences

        //use volley to get a json object request for the url with the specified city
        val rq: RequestQueue = Volley.newRequestQueue(requireActivity())
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                arrList?.clear()
                //clear past data from recyclerview

                try{
                    val feels: String = response.getJSONObject("currentConditions").getString("feelslike")
                    val icon: String = response.getJSONObject("currentConditions").getString("icon")
                    val rawRise: String = response.getJSONObject("currentConditions").getString("sunrise")
                    val rawSet: String = response.getJSONObject("currentConditions").getString("sunset")
                    val windSpeed: String = response.getJSONObject("currentConditions").getString("windspeed")
                    val forecastDaily: JSONArray = response.getJSONArray("days")
                    val forecast0: JSONObject = forecastDaily.getJSONObject(0)
                    val forecastHourly: JSONArray = forecast0.getJSONArray("hours")
                    val todayf = SimpleDateFormat("MMM d, h:mm aa")
                    val currentDate = todayf.format(Calendar.getInstance().time)
                    val df = DecimalFormat("####0.0")
                    var pressure: Double = response.getJSONObject("currentConditions").getString("pressure").toDouble()
                    pressure /= 33.864
                    //convert pressure to inHg

                    val jsonData: String = response.toString()
                    setFragmentResult("TomKey", bundleOf("key" to jsonData))
                    setFragmentResult("SevenKey", bundleOf("key" to jsonData))
                    //pass json response to other fragments

                    val format: DateFormat = SimpleDateFormat("hh:mm:ss")
                    try {
                        val date: Date = format.parse(rawRise)!!
                        val date2: Date = format.parse(rawSet)!!
                        val format2 = SimpleDateFormat("h:mm a")
                        sunriseVal?.text = format2.format(date)
                        sunsetVal?.text = format2.format(date2)
                        //convert sunrise and sunset times into specified format
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                    loadImg(icon)
                    //based on different times of day, load different background image into backIV

                    weather?.text = response.getJSONObject("currentConditions").getString("conditions")
                    currentTemp?.text = response.getJSONObject("currentConditions").getString("temp").plus("°F")
                    feelsTemp?.text = "Feels like ".plus(feels).plus("°F")
                    dateTime?.text = currentDate
                    description?.text = response.getString("description")
                    precipitationVal?.text = response.getJSONObject("currentConditions").getString("precip").plus(" in")
                    humidityVal?.text = response.getJSONObject("currentConditions").getString("humidity").plus("%")
                    pressureVal?.text = df.format(pressure).toString().plus(" inHg")
                    highTemp?.text = "↑".plus(forecast0.getString("tempmax")).plus("°F")
                    lowTemp?.text = "↓".plus(forecast0.getString("tempmin")).plus("°F")
                    //set view values based on current conditions

                    if(windSpeed != "null"){
                        windSpeedVal?.text = response.getJSONObject("currentConditions").getString("windspeed").plus(" mph")
                    }
                    else{
                        windSpeedVal?.text = "0 mph"
                    }

                    loadArr(forecastHourly)
                    //load recyclerview for each hour of the day

                }catch(e: JSONException){
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(requireActivity(), "Please enter a valid city name", Toast.LENGTH_SHORT).show()
            }


        )
        rq.add(jsonObjectRequest)
    }

    private fun loadArr(forecastHourly: JSONArray){
        for (i in 0 until forecastHourly.length()) {
            val currentObj: JSONObject = forecastHourly.getJSONObject(i)
            val hrTemp: String = currentObj.getString("temp").plus("°F")
            val hrIcon: String = currentObj.getString("icon")
            val hrPrecip: String = currentObj.getString("precipprob").plus("%")
            val hrWind: String = currentObj.getString("windspeed").plus("mph")
            val converted: String = hrIcon.replace('-', '_')
            var wRV = WeatherRV(hrTemp, R.drawable.cloudy, hrPrecip, hrWind)
            //set default icon as cloudy in case drawable fails to load

            try{
                wRV = WeatherRV(hrTemp, resources.getIdentifier(converted, "drawable", requireActivity().packageName), hrPrecip, hrWind)
            }catch(e: Exception){
                Log.d("Exception", e.toString())
            }
            arrList?.add(wRV)
        }
        loadPB?.visibility = View.INVISIBLE
        weatherRV?.adapter?.notifyDataSetChanged()
    }

    private fun loadImg(icon: String){
        backIV?.elevation = 0F
        //hide loading background and replace weather icon and background with icon according to API
        when(icon){
            "snow" ->  {
                Picasso.get().load(R.drawable.snow).into(weatherImg)
                backIV?.setImageResource(R.drawable.snow_bg)
            }
            "snow-showers-day" -> {
                Picasso.get().load(R.drawable.snow_showers_day).into(weatherImg)
                backIV?.setImageResource(R.drawable.snow_bg)
            }
            "snow-showers-night" -> {
                Picasso.get().load(R.drawable.snow_showers_night).into(weatherImg)
                backIV?.setImageResource(R.drawable.snow_bg)
            }
            "thunder-rain" -> {
                Picasso.get().load(R.drawable.thunder_rain).into(weatherImg)
                backIV?.setImageResource(R.drawable.thunder_rain_bg)
            }
            "thunder-showers-day" -> {
                Picasso.get().load(R.drawable.thunder_showers_day).into(weatherImg)
                backIV?.setImageResource(R.drawable.thunder_rain_bg)
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

}
