package dev.mattdebinion.onex3streamer.permissions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * The PermissionsViewModel allows the permissions table in general settings to update UI elements
 * according to permission granted status.
 *
 * @constructor A PermissionsViewModel
 */
class PermissionsViewModel: ViewModel() {

    // All MutableLiveData permissions for bluetooth, location, microphone, usb, and wifi
    private val _bluetoothPermissionGranted = MutableLiveData<Boolean>()
    private val _locationPermissionGranted = MutableLiveData<Boolean>()
    private val _microphonePermissionGranted = MutableLiveData<Boolean>()
    private val _usbPermissionGranted = MutableLiveData<Boolean>()
    private val _wifiPermissionGranted = MutableLiveData<Boolean>()

    val bluetoothPermissionGranted: LiveData<Boolean> get() = _bluetoothPermissionGranted
    val locationPermissionGranted: LiveData<Boolean> get() = _locationPermissionGranted
    val microphonePermissionGranted: LiveData<Boolean> get() = _microphonePermissionGranted
    val usbPermissionGranted: LiveData<Boolean> get() = _usbPermissionGranted
    val wifiPermissionGranted: LiveData<Boolean> get() = _wifiPermissionGranted

    fun setBluetoothPermissionGranted(granted: Boolean) {
        _bluetoothPermissionGranted.value = granted
    }

    fun setLocationPermissionGranted(granted: Boolean) {
        _locationPermissionGranted.value = granted
    }

    fun setMicrophonePermissionGranted(granted: Boolean) {
        _microphonePermissionGranted.value = granted
    }

    fun setUsbPermissionGranted(granted: Boolean) {
        _usbPermissionGranted.value = granted
    }

    fun setWifiPermissionGranted(granted: Boolean) {
        _wifiPermissionGranted.value = granted
    }
}