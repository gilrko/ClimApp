package com.jt.climapp.ui.data.network

import com.jt.climapp.ui.data.model.WeatherModel
import com.jt.climapp.ui.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface WeatherApi {
    val API_KEY: String
        get() = "539300b3bd123d1ad33dcd89e70184ce"

    //https://api.openweathermap.org/data/2.5/onecall?lat=33.44&lon=-94.04&exclude=hourly,minutely&lang=sp&appid=539300b3bd123d1ad33dcd89e70184ce
    @GET
    suspend fun getWeather(@Url url:String): Response<WeatherModel>

    @GET
    suspend fun getWeatherByCity(@Url url:String): Response<WeatherResponse>
}