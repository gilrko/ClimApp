package com.jt.climapp.ui.view.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _group = MutableLiveData<Boolean>().apply {
        value = true
    }
    val group: LiveData<Boolean> = _group
}