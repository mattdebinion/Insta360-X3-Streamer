package dev.mattdebinion.onex3streamer.camera

import android.graphics.Camera
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.arashivision.sdkcamera.camera.InstaCameraManager
import com.arashivision.sdkcamera.camera.callback.IPreviewStatusListener
import com.arashivision.sdkcamera.camera.resolution.PreviewStreamResolution
import com.arashivision.sdkmedia.player.capture.CaptureParamsBuilder
import com.arashivision.sdkmedia.player.capture.InstaCapturePlayerView
import com.arashivision.sdkmedia.player.listener.PlayerViewListener

/**
 * Camera preview handler
 *
 * @param cameraViewModel The camera view model to update changes to for the UI
 * @constructor Create empty Camera preview handler
 */
class CameraPreviewManager(private val cameraViewModel: CameraViewModel) : IPreviewStatusListener {

    private lateinit var capturePlayerView: InstaCapturePlayerView


    /**
     * Binds the InstaCapturePlayerView to a lifecycle
     *
     * @param lifecycle The lifecycle for the activity or fragment
     */
    fun bindLifecycle(feedView: InstaCapturePlayerView, lifecycle: Lifecycle) {
        capturePlayerView = feedView
        capturePlayerView.setLifecycle(lifecycle)
    }


    // TODO
    override fun onOpening() {
        super.onOpening()

        cameraViewModel.setCameraPreviewConnectingStatusChanged(true)
        Log.i("CameraPreviewManager", "The preview is opening...")
    }

    //TODO
    override fun onOpened() {
        super.onOpened()

        cameraViewModel.setCameraPreviewConnectingStatusChanged(true)                               // Set the preview connecting status to TRUE
        InstaCameraManager.getInstance().setStreamEncode()
        capturePlayerView.setPlayerViewListener(object : PlayerViewListener {
            override fun onLoadingFinish() {
                InstaCameraManager.getInstance().setPipeline(capturePlayerView.pipeline)

                cameraViewModel.setCameraPreviewConnectingStatusChanged(false)                      // Set the preview connecting status to FALSE
                cameraViewModel.setCameraPreviewStatusChanged(true)                                 // then set the preview connected status to TRUE
                Log.i("CameraPreviewHandler", "The preview has loaded successfully.")
            }

            override fun onReleaseCameraPipeline() {
                Log.i("CameraPreviewHandler", "The preview pipeline has ended.")
                InstaCameraManager.getInstance().setPipeline(null)
                capturePlayerView.keepScreenOn = false
                cameraViewModel.setCameraPreviewStatusChanged(false)                                // Set the preview connected status to FALSE
            }
        })
        capturePlayerView.prepare(createParams())
        capturePlayerView.play()
        capturePlayerView.keepScreenOn = true
    }

    override fun onIdle() {
        super.onIdle()

        cameraViewModel.setCameraPreviewStatusChanged(false)                                        // Set the preview connected status to FALSE
        capturePlayerView.destroy()
        capturePlayerView.keepScreenOn = false
        //cameraConnectionManager.disconnectCamera()
    }

    override fun onError() {
        super.onError()

        cameraViewModel.setCameraPreviewStatusChanged(false)                                        // Set the preview connected status to FALSE
        Log.w("CameraPreviewManager", "ERRORS OCCURED!!!!!!!!!!!!!!")
    }

    private fun createParams(): CaptureParamsBuilder {
        val builder = CaptureParamsBuilder()
            .setCameraType(InstaCameraManager.getInstance().cameraType)
            .setMediaOffset(InstaCameraManager.getInstance().mediaOffset)
            .setMediaOffsetV2(InstaCameraManager.getInstance().mediaOffsetV2)
            .setMediaOffsetV3(InstaCameraManager.getInstance().mediaOffsetV3)
            .setCameraSelfie(InstaCameraManager.getInstance().isCameraSelfie)
            .setGyroTimeStamp(InstaCameraManager.getInstance().gyroTimeStamp)
            .setBatteryType(InstaCameraManager.getInstance().batteryType)
            .setResolutionParams(1920, 1080, 30)

        return builder
    }

}