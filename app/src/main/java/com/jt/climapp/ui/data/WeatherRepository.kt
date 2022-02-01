package com.jt.climapp.ui.data

import com.jt.climapp.ui.data.model.WeatherModel
import com.jt.climapp.ui.data.model.WeatherProvider
import com.jt.climapp.ui.data.network.WeatherService

class WeatherRepository {
    private val api = WeatherService()
}