package com.jt.climapp.ui.view.ui.home

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.jt.climapp.R
import com.jt.climapp.databinding.FragmentHomeBinding
import com.jt.climapp.ui.adapter.DayAdapter
import com.jt.climapp.ui.core.RetrofitHelper.getRetrofit
import com.jt.climapp.ui.data.model.Daily
import com.jt.climapp.ui.data.model.User
import com.jt.climapp.ui.data.model.WeatherModel
import com.jt.climapp.ui.data.model.WeatherResponse
import com.jt.climapp.ui.data.network.WeatherApi
import com.jt.climapp.ui.utils.Connectivity.getShortDate
import com.jt.climapp.ui.utils.Connectivity.isOnline
import io.realm.Realm
import kotlinx.coroutines.*
import retrofit2.Response
import java.lang.Exception
import java.util.*


class HomeFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    lateinit var fusedLocationProvider: FusedLocationProviderClient
    lateinit var txtCity: TextView
    lateinit var txtTemp: TextView
    lateinit var txtTempFeelsLike: TextView
    lateinit var txtDate: TextView
    lateinit var txtTime: TextView
    lateinit var txtTimeZone: TextView
    lateinit var imgWeather: ImageView
    lateinit var group: Group
    lateinit var groupData: Group
    private val days = mutableListOf<Daily>()
    private lateinit var adapter: DayAdapter
    lateinit var svSearch: SearchView
    private lateinit var realm: Realm

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        realm = Realm.getDefaultInstance()
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context!!)
        val root: View = binding.root
        group = binding.groupLoading
        txtCity = binding.textViewCity
        txtTemp = binding.textViewTemperature
        txtTempFeelsLike = binding.textViewFeelsLikeTemperature
        txtDate = binding.textViewDate
        txtTime = binding.textViewTime
        txtTimeZone = binding.textViewTimezone
        imgWeather = binding.imageViewConditionIcon
        svSearch = binding.svCity
        groupData = binding.groupData
        group.visibility = View.VISIBLE
        groupData.visibility = View.GONE

        val fab: View = binding.fab
        fab.setOnClickListener {
            fetchLocation()
        }

        svSearch.setOnQueryTextListener(this)
        initRecyclerView()

        if (activity?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }
            != PackageManager.PERMISSION_GRANTED && activity?.let {
                ActivityCompat
                    .checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED
        ) {
            activity?.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101) }
            group.visibility = View.GONE
        } else {
            group.visibility = View.VISIBLE
            fetchLocation()
        }
        return root
    }

    private fun initRecyclerView() {
        adapter = DayAdapter(days)
        binding.rvDays.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvDays.adapter = adapter
    }


    private fun fetchLocation() {
        val task: Task<Location> = fusedLocationProvider.lastLocation

        if (activity?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) }
        != PackageManager.PERMISSION_GRANTED && activity?.let {
                ActivityCompat
                    .checkSelfPermission(it, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED
        ) {
            activity?.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101) }
            return
        }
        task.addOnSuccessListener {
            if (it != null){
                if(isOnline(requireActivity())) {
                    searchCity(it.latitude, it.longitude)
                }
            }
        }
    }

    private fun searchCity(lat: Double, long: Double){
        var url = "onecall?lat=${lat}&lon=${long}&units=metric&exclude=hourly,minutely&lang=sp&appid=539300b3bd123d1ad33dcd89e70184ce"
            CoroutineScope(Dispatchers.IO).launch {
                val call: Response<WeatherModel> =
                    getRetrofit().create(WeatherApi::class.java)
                        .getWeather(url)
                if (call.isSuccessful){
                    val weather: WeatherModel? = call.body()
                    if (call.isSuccessful) {
                        loadData(weather, lat, long)
                    } else {
                        group.visibility = View.GONE
                        Toast.makeText(activity, "Ocurrió un error, intentalo más tarde", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun searchByCityName(query:String){
        group.visibility = View.VISIBLE
        var url = "weather?q=${query}&units=metric&lang=sp&appid=539300b3bd123d1ad33dcd89e70184ce"
        CoroutineScope(Dispatchers.IO).launch {
            val call: Response<WeatherResponse> =
                getRetrofit().create(WeatherApi::class.java)
                    .getWeatherByCity(url)
            if (call.isSuccessful){
                val weather: WeatherResponse? = call.body()
                if (call.isSuccessful) {
                    weather?.coord?.latitude?.let { searchCity(it, weather?.coord?.longuitude) }
                } else {
                    group.visibility = View.GONE
                    Toast.makeText(activity, "Ocurrió un error, intentalo más tarde", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadData(weather: WeatherModel?, lat: Double, long: Double){
        val geocoder = Geocoder(activity, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(lat, long, 1)
        val cityName: String = addresses[0].locality ?: addresses[0].adminArea
        val countryName: String = addresses[0].countryName

        val stringBuilder = StringBuilder()
        stringBuilder.append(cityName)
            .append(",")
            .append(countryName)
        GlobalScope.launch(Dispatchers.Main) {
            txtCity.text = stringBuilder.toString()
            stringBuilder.clear()
            stringBuilder.append(String.format("%.0f",weather?.current?.temp))
                .append("° C")
            txtTemp.text = stringBuilder.toString()
            stringBuilder.clear()
            stringBuilder.append("Sensación termica:\n")
            stringBuilder.append(String.format("%.0f",weather?.current?.feels_like))
                .append("° C")
            txtTempFeelsLike.text = stringBuilder.toString()
            stringBuilder.clear()
            stringBuilder.append("Fecha:")
            stringBuilder.append(getShortDate(weather?.current?.dt,"Date"))
            txtDate.text = stringBuilder.toString()
            stringBuilder.clear()
            stringBuilder.append("Hora:")
            stringBuilder.append(getShortDate(weather?.current?.dt,"Time"))
            txtTime.text = stringBuilder.toString()
            stringBuilder.clear()
            stringBuilder.append("Zona horaria:")
            stringBuilder.append(weather?.timezone)
            txtTimeZone.text = stringBuilder.toString()
            activity?.let { Glide.with(it).asGif().load(R.drawable.gifweather).into(imgWeather) }
            val daysList: List<Daily> = weather?.daily ?: emptyList()
            days.clear()
            days.addAll(daysList)
            adapter.notifyDataSetChanged()
            group.visibility = View.GONE
            groupData.visibility = View.VISIBLE
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            101 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    fetchLocation()
                } else {
                    group.visibility = View.GONE
                }
                return
            }
            else -> {
                group.visibility = View.GONE
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(!query.isNullOrEmpty()){
            if(isOnline(requireActivity())) {
                searchByCityName(query)
            }
            hideKeyboard()
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
    }
}
