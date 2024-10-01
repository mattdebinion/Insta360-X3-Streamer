package dev.mattdebinion.onex3streamer.ui.settings

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GeneralViewModel() : ViewModel() {

    // The live mutable data for the General Settings fragment
    private val _isEditingCredential = MutableLiveData<Boolean>().apply { value = false }
    private val _cameraSSID = MutableLiveData<String>()
    private val _cameraPassword = MutableLiveData<String>()

    val isEditingCredential: LiveData<Boolean> get() = _isEditingCredential
    val cameraSSID: LiveData<String> get() = _cameraSSID
    val cameraPass: LiveData<String> get() = _cameraPassword

    fun toggleCredentialEditing() {
        _isEditingCredential.value = _isEditingCredential.value != true
    }

    fun setCameraSSID(ssid: String) {
        _cameraSSID.value = ssid
    }

    fun setCameraPassword(pass: String) {
        _cameraPassword.value = pass
    }

}