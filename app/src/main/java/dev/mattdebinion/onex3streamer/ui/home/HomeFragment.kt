package dev.mattdebinion.onex3streamer.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.arashivision.sdkcamera.camera.InstaCameraManager
import com.arashivision.sdkcamera.camera.resolution.PreviewStreamResolution
import com.arashivision.sdkmedia.player.capture.InstaCapturePlayerView
import dev.mattdebinion.onex3streamer.R
import dev.mattdebinion.onex3streamer.camera.CameraConnectionManager
import dev.mattdebinion.onex3streamer.camera.CameraPreviewManager
import dev.mattdebinion.onex3streamer.camera.CameraViewModel
import dev.mattdebinion.onex3streamer.databinding.FragmentHomeBinding
import dev.mattdebinion.onex3streamer.permissions.PermissionsViewModel
import dev.mattdebinion.onex3streamer.ui.settings.GeneralViewModel

/**
 * The Home fragment displays a feed when connected to the camera as well as the camera status.
 *
 * @constructor Creates the default Home fragment
 */
class HomeFragment : Fragment(), ConnectTypeDialogFragment.ConnectTypeDialogListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // View models that need to be checked by the camera
    // TODO permissions on the buttons if not all granted!

    // private val permissionsViewModel: PermissionsViewModel by activityViewModels()
    private val generalViewModel: GeneralViewModel by activityViewModels()
    private val cameraViewModel: CameraViewModel by activityViewModels()

    // The camera connection manager and preview manager. Related variables included
    private lateinit var cameraConnectionManager: CameraConnectionManager
    private lateinit var cameraPreviewManager: CameraPreviewManager
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var runnable2: Runnable
    private var startTime: Long = 0

    // The CapturePlayerView
    private lateinit var capturePlayerView: InstaCapturePlayerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize the capturePlayerView, bind the lifecycle, and set the listener!
        capturePlayerView = binding.capturePlayerView
        cameraPreviewManager = CameraPreviewManager(cameraViewModel)
        cameraPreviewManager.bindLifecycle(capturePlayerView, viewLifecycleOwner.lifecycle)
        InstaCameraManager.getInstance().setPreviewStatusChangedListener(cameraPreviewManager)
        handler = Handler(Looper.getMainLooper())

        setFragmentListeners()
        setButtonListeners()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        InstaCameraManager.getInstance().setPreviewStatusChangedListener(null)
        _binding = null
    }

    override fun onConfirmClick(dialog: DialogFragment, connectType: Int) {
        Log.i("HomeFragment", "Confirmed click for $connectType")

        Log.i("HomeFragment", "Camera connection status: ${cameraViewModel.isCameraConnected.value}")
        if(cameraViewModel.isCameraConnected.value == false) {
            val ssid = generalViewModel.cameraSSID.value.toString()
            val password = generalViewModel.cameraPass.value.toString()


            cameraConnectionManager = CameraConnectionManager(cameraViewModel, requireContext(), ssid, password)
            startCameraConnectionProcess(connectType, cameraConnectionManager)

        } else if (cameraViewModel.isCameraConnected.value == true) {
            cameraConnectionManager.disconnectCamera()
        }

    }

    override fun onCancelClick(dialog: DialogFragment) {
        Log.i("HomeFragment", "Cancel click!")
    }

    /**
     * setFragmentObservers sets all the UI elements to their respective observers, if necessary.
     */
    private fun setFragmentListeners() {

        // If the camera is connected, update the button icon and text accordingly
        cameraViewModel.isCameraConnected.observe(viewLifecycleOwner, Observer { isCameraConnected ->
            binding.buttonConnection.isEnabled = true

            if(isCameraConnected) {
                binding.buttonConnection.text = getString(R.string.button_text_disconnect)
                binding.buttonConnection.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_material_link_off, 0, 0, 0)
                binding.capturePlayerFrameStatus.visibility = View.INVISIBLE
                binding.capturePlayerView.visibility = View.VISIBLE
            } else {
                binding.buttonConnection.text = getString(R.string.button_text_connect)
                binding.buttonConnection.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_material_link, 0, 0, 0)
                binding.capturePlayerFrameStatus.visibility = View.VISIBLE
                binding.capturePlayerView.visibility = View.INVISIBLE
            }
        })

        cameraViewModel.isCameraConnecting.observe(viewLifecycleOwner, Observer { isCameraConnecting ->

            var currentIconIndex = 0
            val icons = listOf(
                R.drawable.ic_material_wifi_bar_0,
                R.drawable.ic_material_wifi_bar_1,
                R.drawable.ic_material_wifi_bar_2,
                R.drawable.ic_material_wifi_bar_3,
                R.drawable.ic_material_wifi_bar_4
            )
            if(isCameraConnecting) {
                binding.buttonConnection.isEnabled = false
                binding.capturePlayerFrameStatusText.text = "Camera is connecting..."
                runnable2 = object : Runnable {
                    override fun run() {
                        binding.capturePlayerFrameStatusIcon.setImageResource(icons[currentIconIndex])
                        currentIconIndex = (currentIconIndex + 1) % icons.size

                        handler.postDelayed(this, 500)
                    }
                }
                handler.post(runnable2)
            } else {
                if(::runnable2.isInitialized) {
                    binding.capturePlayerFrameStatusIcon.setImageResource(R.drawable.ic_material_signal_disconnected)
                    binding.buttonConnection.isEnabled = true
                    binding.capturePlayerFrameStatusText.text = "Camera could not connect."
                    handler.removeCallbacks(runnable2)
                }
            }
        })


        // TODO the HomeViewModel for the pause/unpause button!
    }

    private fun setButtonListeners() {
        binding.buttonConnection.setOnClickListener { _ ->
            val promptConnectType = ConnectTypeDialogFragment()
            promptConnectType.setListener(this)
            promptConnectType.show(parentFragmentManager, "ConnectTypeDialogFragment")
        }
    }

    private fun startCameraConnectionProcess(type: Int, cameraConnectionManager: CameraConnectionManager) {

        cameraConnectionManager.connectCamera(type)

        startTime = System.currentTimeMillis()
        runnable = object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime
                if(cameraViewModel.isCameraConnected.value == true) {
                    Log.i("HomeFragment", "The supported resolutions are: ")
                    Log.i("HomeFragment", InstaCameraManager.getInstance().getSupportedPreviewStreamResolution(InstaCameraManager.PREVIEW_TYPE_LIVE).toString())
                    Log.i("HomeFragment", "Launching the preview stream :3")
                    InstaCameraManager.getInstance().setPreviewStatusChangedListener(cameraPreviewManager)
                    InstaCameraManager.getInstance().startPreviewStream(PreviewStreamResolution.STREAM_2560_1280_30FPS,InstaCameraManager.PREVIEW_TYPE_LIVE)
                    handler.removeCallbacks(this)
                } else if(elapsedTime >= 10000) {
                    Log.e("HomeFragment", "Unable to connect.")
                    cameraViewModel.setCameraConnectingStatusChanged(false)
                    handler.removeCallbacks(this)
                } else {
                    Log.i("HomeFragment", "Waiting to connect to camera...")
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(runnable)
    }
}