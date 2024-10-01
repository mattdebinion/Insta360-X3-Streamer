package dev.mattdebinion.onex3streamer

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.arashivision.sdkcamera.InstaCameraSDK
import com.arashivision.sdkcamera.camera.InstaCameraManager
import com.arashivision.sdkmedia.InstaMediaSDK
import dev.mattdebinion.onex3streamer.camera.CameraConnectionManager
import dev.mattdebinion.onex3streamer.camera.CameraStateObserver
import dev.mattdebinion.onex3streamer.camera.CameraViewModel
import dev.mattdebinion.onex3streamer.databinding.ActivityMainBinding
import dev.mattdebinion.onex3streamer.permissions.AppPermissionManager
import dev.mattdebinion.onex3streamer.ui.settings.GeneralViewModel

/**
 * Main activity
 *
 * @constructor Create empty Main activity
 *
 * TODO: Does the frame listener terminate properly when the activity is destroyed?
 */
class MainActivity : AppCompatActivity() {

    // App permissions handling
    private lateinit var appPermissionManager: AppPermissionManager

    // The activity binding
    private lateinit var binding: ActivityMainBinding

    // Global variables and observables
    private val cameraViewModel: CameraViewModel by viewModels()
    private val generalViewModel : GeneralViewModel by viewModels()
    private lateinit var cameraConnectionManager: CameraConnectionManager
    private lateinit var cameraStateObserver: CameraStateObserver

    // UI elements
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Initialize the Insta360 SDKs
        InstaCameraSDK.init(application)
        InstaMediaSDK.init(application)

        // Initialize the permissions manager to handle app necessities
        appPermissionManager = AppPermissionManager.getInstance(this)

        // Inflates activity_main.xml and sets the view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Set the UI component observers in the Nav Drawer
        cameraViewModel.isCameraConnected.observe(this, Observer { isConnected ->
            navView.menu.findItem(R.id.nav_settings_audio).isEnabled = isConnected
            navView.menu.findItem(R.id.nav_settings_preview).isEnabled = isConnected
            navView.menu.findItem(R.id.nav_settings_live).isEnabled = isConnected
        })

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_settings_app,R.id.nav_settings_audio, R.id.nav_settings_preview, R.id.nav_settings_live
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Apart of AppCompatActivity, pass the result into the `AppPermissionsManager` class.
     *
     * @param requestCode The request code associated with the permission
     * @param permissions The array of permissions associated with the request code
     * @param grantResults The array of grant results.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        appPermissionManager.handlePermissionResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Cleanup on app destroy!
        InstaCameraManager.getInstance().unregisterCameraChangedCallback(cameraStateObserver)     // Unregister the camera observer callback
    }
}