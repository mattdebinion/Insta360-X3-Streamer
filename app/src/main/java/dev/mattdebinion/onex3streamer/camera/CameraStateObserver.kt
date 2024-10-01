package dev.mattdebinion.onex3streamer.camera
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.arashivision.sdkcamera.camera.InstaCameraManager
import com.arashivision.sdkcamera.camera.callback.ICameraChangedCallback

/**
 * The CameraStateObserver class observes and notifies updates about a connected camera to the ViewModel.
 * This is done by:
 * * Implementing SDK interface for camera states via `ICameraChangedCallback`.
 * * Implementing `AppCompatActivity` that registers these callbacks.
 *
 * Ensure this observer is registered with the InstaCameraManager instance with the
 * registerCameraChangedCallback method during onCreate in the main activity/fragment and destroyed
 * with the respective unregister method in OnDestroy!
 *
 * @param viewModel A CameraViewModel to update these changes for the UI to see.
 * @constructor Creates CameraStateObserver
 */
open class CameraStateObserver(private val viewModel: CameraViewModel) : AppCompatActivity(), ICameraChangedCallback {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InstaCameraManager.getInstance().registerCameraChangedCallback(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        InstaCameraManager.getInstance().unregisterCameraChangedCallback(this)
    }
    /**
     * The camera state is changed to ON (true) or OFF (false)
     *
     * @param enabled: Whether the camera is available
     */
    override fun onCameraStatusChanged(enabled: Boolean) {

        viewModel.setCameraStatusChanged(enabled)
        viewModel.setCameraConnectingStatusChanged(false)

        if(enabled) {
            Log.i("CameraStateObserver", "The camera is turned on.")

        } else {
            Log.i("CameraStateObserver", "The camera is turned off.")
        }
    }

    /**
     * Camera connection failed
     *
     * A common situation is that other phones or other applications of this phone have already
     * established a connection with this camera, resulting in this establishment failure,
     * and other phones need to disconnect from this camera first.
     * @param errorCode The corresponding error code
     */
    override fun onCameraConnectError(errorCode: Int) {
        viewModel.setCameraConnectError(errorCode)
        Log.e("CameraStateObserver", "The camera failed to connect. (ERROR CODE $errorCode)")
    }

    /**
     * SD card insertion notification
     *
     * @param enabled: Whether the current SD card is available
     */
    override fun onCameraSDCardStateChanged(enabled: Boolean) {
        viewModel.setCameraSDCardStateChanged(enabled)
        Log.i("CameraStateObserver", "The camera SD state changed to $enabled!")
    }

    /**
     * SD card storage status changed
     *
     * @param freeSpace:  Currently available size
     * @param totalSpace: Total size
     */
    override fun onCameraStorageChanged(freeSpace: Long, totalSpace: Long) {
        viewModel.setCameraStorageChanged(freeSpace, totalSpace)
        Log.i("CameraStateObserver", "The camera storage changed to $freeSpace out of $totalSpace.")
    }

    /**
     * Low battery notification
     */
    override fun onCameraBatteryLow() {
        Log.w("CameraStateObserver", "The camera battery is low!")
    }

    /**
     * Camera power change notification
     *
     * @param batteryLevel: Current power (0-100, always returns 100 when charging)
     * @param isCharging:   Whether the camera is charging
     */
    override fun onCameraBatteryUpdate(batteryLevel: Int, isCharging: Boolean) {

        viewModel.setCameraBatteryUpdate(batteryLevel, isCharging)

        if(isCharging)
            Log.i("CameraStateObserver", "The camera is charging")
        else
            Log.i("CameraStateObserver", "The camera is discharging")

        Log.i("CameraStateObserver", "The battery is at $batteryLevel%!")
    }

}