package com.jt.climapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jt.climapp.ui.data.model.WeatherModel
import com.jt.climapp.ui.domain.GetWeatherUseCase
import kotlinx.coroutines.launch

class WeatherViewModel: ViewModel() {

    val weather = MutableLiveData<WeatherModel?>()

    var getWeathersUseCase = GetWeatherUseCase()
    fun onCreate() {
        viewModelScope.launch {
        }
    }
}