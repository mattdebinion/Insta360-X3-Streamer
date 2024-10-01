package dev.mattdebinion.onex3streamer.camera

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.mattdebinion.onex3streamer.R

/**
 * The CameraViewModel class holds the state of the camera connection and information about a connected
 * camera for the purpose of updating UI elements.
 *
 * @constructor Creates a CameraViewModel
 */
class CameraViewModel : ViewModel() {

    // All mutable live data initializations
    private val _isConnected = MutableLiveData<Boolean>().apply { value = false }
    private val _isConnecting = MutableLiveData<Boolean>().apply { value = false }
    private val _isPreviewConnected = MutableLiveData<Boolean>().apply { value = false }
    private val _isPreviewConnecting = MutableLiveData<Boolean>().apply { value = false }
    private val _latestConnectErrorCode = MutableLiveData<Int>()
    private val _checkSDCardStateChange = MutableLiveData<Boolean>().apply { value = false }
    private val _checkFreeSpace = MutableLiveData<Long>()
    private val _checkTotalSpace = MutableLiveData<Long>()
    private val _checkBatteryLevel = MutableLiveData<Int>()
    private val _isCharging = MutableLiveData<Boolean>()

    // Public variables that can be called anywhere this model is imported!
    val isCameraConnected: LiveData<Boolean> = _isConnected
    val isCameraConnecting: LiveData<Boolean> = _isConnecting
    val isPreviewConnected: LiveData<Boolean> = _isPreviewConnected
    val isPreviewConnecting: LiveData<Boolean> = _isPreviewConnecting
    val latestConnectErrorCode: LiveData<Int> = _latestConnectErrorCode
    val checkSDCardStateChange: LiveData<Boolean> = _checkSDCardStateChange
    val checkFreeSpace: LiveData<Long> = _checkFreeSpace
    val checkTotalSpace: LiveData<Long> = _checkTotalSpace
    val checkBatteryLevel: LiveData<Int> = _checkBatteryLevel
    val isCharging: LiveData<Boolean> = _isCharging

    /**
     * Set the camera status
     *
     * @param state `true` means the camera is available, `false` otherwise.
     */
    fun setCameraStatusChanged(state: Boolean) {
        _isConnected.value = state
    }

    /**
     * Set the camera connecting in progress status
     *
     * @param state `true` means the camera is connecting, `false` otherwise.
     */
    fun setCameraConnectingStatusChanged(state: Boolean) {

        if(Looper.myLooper() == Looper.getMainLooper()) {
            _isConnecting.value = state
        } else {
            _isConnecting.postValue(state)
        }

    }

    /**
     * Set the camera preview feed status
     *
     * @param state `true` means the feed is visible, `false` otherwise.
     */
    fun setCameraPreviewStatusChanged(state: Boolean) {
        _isPreviewConnected.value = state
    }

    /**
     * Set the camera feed connecting in progress status
     *
     * @param state `true` means the feed is loading, `false` otherwise.
     */
    fun setCameraPreviewConnectingStatusChanged(state: Boolean) {
        _isPreviewConnecting.value = state
    }

    /**
     * Set camera connect error
     *
     * @param errorCode The corresponding error code.
     */
    fun setCameraConnectError(errorCode: Int) {
        _latestConnectErrorCode.value = errorCode
    }

    /**
     * Set camera SD card state changed
     *
     * @param state Set `true` is the SD card is available, `false` otherwise.
     */
    fun setCameraSDCardStateChanged(state: Boolean) {
        _checkSDCardStateChange.value = state
    }

    /**
     * Set camera storage changed
     *
     * @param freeSpace storage free space in bytes.
     * @param totalSpace total storage in bytes.
     */
    fun setCameraStorageChanged(freeSpace: Long, totalSpace: Long) {
        _checkFreeSpace.value = freeSpace
        _checkTotalSpace.value = totalSpace
    }

    /**
     * Set camera battery update
     *
     * @param batteryLevel The battery level from `0` to `100`.
     * @param isCharging Set to `true` if charging, `false` otherwise.
     */
    fun setCameraBatteryUpdate(batteryLevel: Int, isCharging: Boolean) {

        if(batteryLevel < 0 || batteryLevel > 100) {
            _checkBatteryLevel.value = 0
        } else {
            _checkBatteryLevel.value = batteryLevel
        }

        _isCharging.value = isCharging
    }
}