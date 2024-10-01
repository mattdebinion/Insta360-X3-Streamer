package dev.mattdebinion.onex3streamer

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import dev.mattdebinion.onex3streamer.camera.CameraProperties

/**
 * The SharedPreferencesManager saves, edits, and retrieves camera information.
 *
 * @constructor
 *
 * @param application
 */
class SharedPreferencesManager(application: Application) {

    private val sharedPreferences = application.getSharedPreferences("cameras", Context.MODE_PRIVATE)!!
    private val editor = sharedPreferences.edit()

    /**
     * Saves a new camera properties
     *
     * @param cameraProperties
     */
    fun saveCameraProperties(cameraProperties: CameraProperties) : Boolean {
        val gson = Gson()

        // If the camera exists, do not save and return false
        val existingCameraCheck = sharedPreferences.getString(cameraProperties.ssid, null)
        if (existingCameraCheck != null)
            return false

        val cameraConfig = gson.toJson(cameraProperties)
        editor.putString(cameraProperties.ssid, cameraConfig)
        editor.commit()

        return true
    }

    /**
     * Get saved camera properties given an SSID
     *
     * @param ssid A valid ssid
     * @return `CameraProperties` if valid, `null` otherwise.
     */
    fun getSavedCameraProperties(ssid: String): CameraProperties? {
        val cameraJson = sharedPreferences.getString(ssid, null)

        return if (cameraJson != null) {
            try {
                val gson = Gson()
                gson.fromJson(cameraJson, CameraProperties::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    /**
     * Given a camera SSID, modify the saved camera configuration
     *
     * @param ssid A valid SSID (case sensitive)
     * @param cameraProperties The properties to modify
     * @return `true` if successful, `false` otherwise.
     */
    fun editSavedCameraProperties(ssid: String, cameraProperties: CameraProperties) : Boolean {
        val cameraJson = sharedPreferences.getString(ssid, null)

        return if (cameraJson != null) {
            try {
                val gson = Gson()
                val cameraConfigJson = gson.toJson(cameraProperties)

                editor.putString(cameraProperties.ssid, cameraConfigJson)
                editor.commit()

                true
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }

    /**
     * Checks if there are any saved camera properties
     *
     * @return Boolean indicating if any camera properties are saved
     */
    fun hasSavedCameras(): Boolean {
        val allEntries = sharedPreferences.all
        return allEntries.isNotEmpty()
    }

    fun getAllSavedCameras(): Map<String, *> {
        return sharedPreferences.all
    }

}