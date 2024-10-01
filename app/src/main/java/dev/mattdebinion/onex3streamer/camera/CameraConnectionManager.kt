package dev.mattdebinion.onex3streamer.camera

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.arashivision.sdkcamera.camera.InstaCameraManager
import com.arashivision.sdkcamera.camera.resolution.PreviewStreamResolution
import dev.mattdebinion.onex3streamer.networking.ConnectivityHandler


/**
 * The CameraConnectionManager class handles the interfacing between the Insta360 camera and Android.
 * Before calling, ensure the SSID and password are passed!
 *
 * @param cameraViewModel The camera view model to notify changes to during the connection state.
 * @param context The context for the app
 * @param ssid The SSID for the hotspot
 * @param pass The password for the hotspot
 */
class CameraConnectionManager(private val cameraViewModel: CameraViewModel, val context: Context, private val ssid: String, private val pass: String) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val connectivityHandler = ConnectivityHandler(context)

    /**
     * Connects to a camera
     *
     * @param type `CONNECT_TYPE_USB` and `CONNECT_TYPE_WIFI` supported.
     */
    fun connectCamera(type: Int) {
        // TODO: Check appropriate permissions and connectivity to camera here?
        cameraViewModel.setCameraConnectingStatusChanged(true)                                      //Set camera connecting status for UI

        try {
            if (type == InstaCameraManager.CONNECT_TYPE_USB) {
                Log.i("CameraConnectionManager", "Connecting to camera via USB...")

                mainHandler.post {
                    InstaCameraManager.getInstance().openCamera(type)
                }

                //TODO look for connection here

            } else if (type == InstaCameraManager.CONNECT_TYPE_WIFI) {
                Log.i("CameraConnectionManager", "Connecting to camera via Wi-Fi...")

                // Create an instance of ConnectivityHandler and pass context/credentials
                connectivityHandler.connectToHotspot(ssid, pass, {
                    Log.i("CameraConnectionManager", "Camera hotspot found.")

                    // Ensure connection to the camera handles on the main thread per documentation
                    mainHandler.post {
                        try {
                            InstaCameraManager.getInstance().openCamera(InstaCameraManager.CONNECT_TYPE_WIFI);
                            cameraViewModel.setCameraStatusChanged(true)                            // Set the camera connected status to TRUE
                            cameraViewModel.setCameraConnectingStatusChanged(false)                 // and set the connecting status to FALSE
                            Log.i("CameraConnectionManager", "Camera instance opened!");

                        } catch (e: Exception) {
                            Log.e("CameraConnectionManager", "Could not connect to the camera!")
                            cameraViewModel.setCameraConnectingStatusChanged(false)                 // Set the camera connecting status to FALSE on fail.
                            return@post;
                        }
                    }
                }, {
                    cameraViewModel.setCameraConnectingStatusChanged(false)                         // Set the camera connecting status to FALSE on fail
                })

            } else {
                Log.w("CameraConnectionManager", "Connect type code $type not supported.")
                cameraViewModel.setCameraConnectingStatusChanged(false)                             // Set the camera connecting status to FALSE on fail.
            }
        } catch (e: Exception) {
            Log.e("CameraConnectionManager", "Failed to connect to camera: $e")
            cameraViewModel.setCameraConnectingStatusChanged(false)                                 // Set the camera connecting status to FALSE on fail.
        }
    }

    fun disconnectCamera() {
        try {
            InstaCameraManager.getInstance().closeCamera()
            connectivityHandler.reconnectToPreviousNetwork()
            cameraViewModel.setCameraStatusChanged(false)
            cameraViewModel.setCameraPreviewStatusChanged(false)
        } catch (e: Exception) {
            Log.e("CameraConnectionManager", "Failed to disconnect from camera: $e")
        }
    }

    /**
     * Return if the camera is connected or not
     */
    private fun isCameraConnected(): Boolean {
        return InstaCameraManager.getInstance().cameraConnectedType != InstaCameraManager.CONNECT_TYPE_NONE
    }
}