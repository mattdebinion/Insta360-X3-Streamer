package dev.mattdebinion.onex3streamer.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dev.mattdebinion.onex3streamer.permissions.AppPermissionManager
import dev.mattdebinion.onex3streamer.databinding.FragmentSettingsAudioBinding
import dev.mattdebinion.onex3streamer.databinding.FragmentSettingsLiveBinding

class AudioFragment : Fragment(), AppPermissionManager.PermissionActions {

    private var _binding: FragmentSettingsAudioBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPermissionGranted(requestCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onPermissionDenied(requestCode: Int) {
        TODO("Not yet implemented")
    }
}