package dev.mattdebinion.onex3streamer.permissions

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * App Permission Manager is a class that handles permissions that are used within the app. The app
 * permissions are defined in the AndroidManifest.xml. This class manages the following runtime
 * permissions:
 *
 * * Bluetooth (Using connected bluetooth devices with microphones)
 * * Networks (Wi-Fi settings to find and connect to camera wirelessly)
 * * USB Permission (connecting to camera via USB)
 * * Microphone (using microphones for LIVE)
 *
 * @property appContext The application context to be able to access the application resources and
 * system services.
 * @constructor Creates an AppPermissionManager
 */
class AppPermissionManager private constructor(private val appContext: Context) {

    companion object {
        const val ACTION_USB_PERMISSION = "dev.mattdebinion.USB_PERMISSION"

        @Volatile
        private var instance: AppPermissionManager? = null

        /**
         * Get the instance of the AppPermissionManager
         * @param context The application context
         * @return AppPermissionManager
         */
        fun getInstance(context: Context): AppPermissionManager {
            return instance ?: synchronized(this) {
                instance ?: AppPermissionManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private val usbManager: UsbManager = appContext.getSystemService(Context.USB_SERVICE) as UsbManager
    private val permissionIntent: PendingIntent = PendingIntent.getBroadcast(appContext,
        PermissionGroup.USB.requestCode,
        Intent(ACTION_USB_PERMISSION),
        PendingIntent.FLAG_IMMUTABLE)
    private val usbReceiver = UsbPermissionReceiver()
    private var permissionActionsListener: PermissionActions? = null

    /**
     * A set of interfaces when a permission is granted or denied.
     * All permission functions pass in a requestCode that identifies what permission was passed in.
     */
    interface PermissionActions {
        fun onPermissionGranted(requestCode: Int)
        fun onPermissionDenied(requestCode: Int)
    }

    fun setPermissionActionsListener(listener: PermissionActions) {
        permissionActionsListener = listener
    }

    /**
     * Checks the permission group that allows app functionality.
     *
     * @param permissionGroup A permission group to check from this list:
     * * `BLUETOOTH` All bluetooth permissions
     * * `LOCATION` All location permissions
     * * `MICROPHONE` All microphone permissions
     * * `USB` All USB permissions
     * * `WIFI` All Wi-Fi permissions
     *
     * @return `true` if all permissions in the group are granted, `false` otherwise.
     */
    fun checkPermissionGroup(permissionGroup: PermissionGroup): Boolean {

        Log.i("AppPermissionManager", "Checking $permissionGroup permissions...")
        val permCheck = permissionGroup.permissions.all {
            ContextCompat.checkSelfPermission(appContext, it) == PackageManager.PERMISSION_GRANTED
        }

        if(permCheck) {
            permissionActionsListener?.onPermissionGranted(permissionGroup.requestCode)
        }

        return permCheck
    }

    /**
     * Request all permissions within a PermissionGroup
     *
     * @param activity The activity to request permission for
     * @param permissionGroup The PermissionGroup
     */
    fun requestPermission(activity: FragmentActivity, permissionGroup: PermissionGroup) {
        val deniedPermissions = permissionGroup.permissions.filter {
            ContextCompat.checkSelfPermission(appContext, it) != PackageManager.PERMISSION_GRANTED
        }

        if (deniedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                deniedPermissions.toTypedArray(),
                permissionGroup.requestCode
            )
        }
    }

    /**
     * Request all permissions within a PermissionGroup
     *
     * @param device
     */
    fun requestPermission(activity: FragmentActivity, permissionGroup: PermissionGroup, device: UsbDevice) {

        if(permissionGroup == PermissionGroup.USB) {
            if (!usbManager.hasPermission(device))
                usbManager.requestPermission(device, permissionIntent)
        }
    }

    /**
     * Handle the permission result and notify the listener.
     *
     * @param requestCode The request code passed in requestPermission
     * @param permissions The requested permissions
     * @param grantResults The grant results for the corresponding permissions.
     */
    fun handlePermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        PermissionGroup.entries.forEach { group ->
            if (requestCode == group.requestCode) {
                val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

                if (allPermissionsGranted) {
                    permissionActionsListener?.onPermissionGranted(requestCode)
                } else {
                    permissionActionsListener?.onPermissionDenied(requestCode)
                }

            }
        }
    }

    fun unregisterReceiver() {
        appContext.unregisterReceiver(usbReceiver)
    }

    private inner class UsbPermissionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.apply {
                            // Permission granted, proceed with USB communication
                        }
                    } else {
                        // Permission denied
                    }
                }
            }
        }
    }
}