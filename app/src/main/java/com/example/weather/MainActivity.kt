package com.example.weather

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
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
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MainActivity(var arrList: MutableList<String>? = null, private var weatherRV: RecyclerView? = null) : AppCompatActivity() {
    //private var wRVA: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null, goes in constructor
    private var searchLayout: TextInputLayout? = null
    private var searchEdit: TextInputEditText? = null
    private var currentTemp: TextView? = null; private var weather: TextView? = null
    private var displayCity: TextView? = null; private var feelsTemp: TextView? = null; private var dateTime: TextView? = null
    private var weatherImg: ImageView? = null; private var searchIcon: ImageView? = null
    private var loadPB: ProgressBar? = null
    private var locationManger: LocationManager? = null
    private var pCode: Int = 1
    private var cityName: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lm: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        //fullscreen
        feelsTemp = findViewById(R.id.feelsLike)
        dateTime = findViewById(R.id.dateTime)
        searchLayout = findViewById(R.id.searchLayout)
        searchEdit = findViewById(R.id.searchEdit)
        searchIcon = findViewById(R.id.search_Icon)
        currentTemp = findViewById(R.id.currentTemp)
        weather = findViewById(R.id.desc)
        weatherImg = findViewById(R.id.weatherImg)
        weatherRV = findViewById(R.id.weatherRV)
        //loadPB = findViewById(R.id.loadingPB)
        lm = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        weatherRV?.layoutManager = lm
        adapter = RecyclerAdapter()
        weatherRV?.adapter = adapter
        //wRVA = arrList?.let { RecyclerAdapter(it) }
        //weatherRV?.adapter = wRVA



        locationManger = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION), pCode)
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if(location != null){
                    cityName = getCityName(location.longitude, location.latitude)
                    getWeatherInfo(cityName!!)
                    //automatically use location to find weather
                }
                else{
                    //if location is unknown, use last entered city name
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
                    var city: String? = sharedPrefs.getString("cityName", "Dallas")
                    try{
                        cityName = city
                        searchEdit?.setText(city)
                        getWeatherInfo(cityName!!)
                    }catch(e: Exception){
                        //if last entered city is unknown, set default weather to Dallas
                        cityName = "Dallas"
                        getWeatherInfo(cityName!!)
                    }
                }
            }

        searchIcon?.setOnClickListener {
            var city: String = searchEdit?.text.toString()
            if(city.isEmpty()){
                Toast.makeText(this, "Enter city name", Toast.LENGTH_SHORT).show()
            }
            else{
                //displayCity?.text = cityName
                getWeatherInfo(city)
            }
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
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Please provide permissions", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun getCityName(longitude: Double, latitude: Double): String{
        var cityName = "Not Found"
        val geoCD = Geocoder(baseContext, Locale.getDefault())
            var addresses: List<Address> = geoCD.getFromLocation(latitude, longitude, 10)
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
        var url: String = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/$city?unitGroup=us&iconSet=icons2&key=DR7SE842V9TC3UK6854QN4YP7&contentType=json"

        //displayCity?.text = city
        val mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val mEditor: SharedPreferences.Editor = mPrefs.edit()
        mEditor.putString("cityName", city).apply()

        var rq: RequestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                //homeRL.setVisibility(View.VISIBLE)
                arrList?.clear()
                try{
                    var feels: String = response.getJSONObject("currentConditions").getString("feelslike")
                    var icon: String = response.getJSONObject("currentConditions").getString("icon")
                    var df = SimpleDateFormat("EEE, MMM d yyyy H:mm")
                    var date = df.format(Calendar.getInstance().time)
                    when(icon){
                        "snow" ->  Picasso.get().load(R.drawable.snow).into(weatherImg)
                        "snow-showers-day" -> Picasso.get().load(R.drawable.snow).into(weatherImg)
                        "snow-showers-night" -> Picasso.get().load(R.drawable.snow).into(weatherImg)
                        "thunder-rain" -> Picasso.get().load(R.drawable.thunder_rain).into(weatherImg)
                        "thunder-showers-day" -> Picasso.get().load(R.drawable.thunder_showers_day).into(weatherImg)
                        "thunder-showers-night" -> Picasso.get().load(R.drawable.thunder_rain).into(weatherImg)
                        "rain" -> Picasso.get().load(R.drawable.rain).into(weatherImg)
                        "showers-day" -> Picasso.get().load(R.drawable.thunder_showers_day).into(weatherImg)
                        "showers-night" -> Picasso.get().load(R.drawable.rain).into(weatherImg)
                        "fog" -> Picasso.get().load(R.drawable.fog).into(weatherImg)
                        "wind" -> Picasso.get().load(R.drawable.wind).into(weatherImg)
                        "cloudy" -> Picasso.get().load(R.drawable.cloudy).into(weatherImg)
                        "partly-cloudy-day" -> Picasso.get().load(R.drawable.partly_cloudy_day).into(weatherImg)
                        "partly-cloudy-night" -> Picasso.get().load(R.drawable.partly_cloudy_night).into(weatherImg)
                        "clear-day" -> Picasso.get().load(R.drawable.clear_day).into(weatherImg)
                        "clear-night" -> Picasso.get().load(R.drawable.clear_night).into(weatherImg)
                    }
                    //based on different times of day, load different background image into backIV
                    //add alerts using response.getJSONObject("alerts").getString("event, headline, and description")
                    weather?.text = response.getJSONObject("currentConditions").getString("conditions")
                    currentTemp?.text = response.getJSONObject("currentConditions").getString("temp").plus("°F")
                    feelsTemp?.text = "Feels like ".plus(feels).plus("°F")
                    dateTime?.text = date

                    var forecastObj: JSONObject = response.getJSONObject("days")
                    var forecast0: JSONObject = forecastObj.getJSONArray("hours").getJSONObject(0)
                    //fullweather?.text = forecast0.getString("description")
                    for(i in 0..forecast0.length()) {
                        var hrTime: String = forecast0.getString("datetime")
                        var hrTemp: String = forecast0.getString("temp")
                        var hrIcon: String = forecast0.getString("icon")
                        var hrWind: String = forecast0.getString("windspeed").plus("MPH")
                        //arrList?.add(WeatherRV(hrTime, hrTemp, hrIcon, hrWind))
                    }
                    //wRVA?.notifyDataSetChanged()

                }catch(e: JSONException){
                    e.printStackTrace()
                }
                Toast.makeText(this, city, Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Please enter a valid city name", Toast.LENGTH_SHORT).show()
            }


        )
        rq.add(jsonObjectRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}