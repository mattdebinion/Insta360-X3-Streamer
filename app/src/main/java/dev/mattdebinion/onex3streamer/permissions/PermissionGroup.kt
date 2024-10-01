package dev.mattdebinion.onex3streamer.permissions

/**
 * This permission group enum class shows all the permissions the app requires. requestCodes are assigned to each
 * permission group, and increment higher the safer the permissions are per protectionLevel in the dev docs:
 *
 * * `100` Normal permissions that don't require explicit permission.
 * * `200` Dangerous permissions that require explicit permission as it deals with sensitive info.
 * * `300` Placeholder value for `signature`.
 * * `400` Placeholder value for `knownSigner`.
 * * `500` Placeholder value for `signatureOrSystem`.
 * * `900` A permission granted at runtime, specifically for USB operations.
 *
 * The currently defined permissions are:
 * * `BLUETOOTH` with request code `100`.
 * * `WIFI` with request code `101`.
 * * `LOCATION` with request code `200`.
 * * `MICROPHONE` with request code `201`.
 * * `USB` with request code `900`.
 *
 * @property permissions The array of specific permissions within this group.
 * @property requestCode The associated request code for this group.
 */
enum class PermissionGroup(val permissions: Array<String>, val requestCode: Int) {
    BLUETOOTH(
        arrayOf(
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN

        ),
        100
    ),
    LOCATION(
        arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ),
        200
    ),
    MICROPHONE(
        arrayOf(
            android.Manifest.permission.RECORD_AUDIO
        ),
        201
    ),
    USB(
        arrayOf(),
        900
    ),
    WIFI(
        arrayOf(
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CHANGE_WIFI_STATE,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET
        ),
        101
    )
}