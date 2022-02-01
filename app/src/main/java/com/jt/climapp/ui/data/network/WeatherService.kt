package com.jt.climapp.ui.data.network

import com.jt.climapp.ui.core.RetrofitHelper
import com.jt.climapp.ui.data.model.WeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherService {
    private val retrofit = RetrofitHelper.getRetrofit()
}