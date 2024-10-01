package dev.mattdebinion.onex3streamer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    // The live mutable data for the General Settings fragment
    private val _isPaused = MutableLiveData<Boolean>().apply { value = false }

    val isPaused: LiveData<Boolean> get() = _isPaused

    fun toggleScreenPaused() {
        _isPaused.value = _isPaused.value != true
    }
}