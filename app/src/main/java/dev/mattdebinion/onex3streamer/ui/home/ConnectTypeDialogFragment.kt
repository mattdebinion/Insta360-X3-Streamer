package dev.mattdebinion.onex3streamer.ui.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.arashivision.sdkcamera.camera.InstaCameraManager

/**
 * The ConnectTypeDialogFragment class presents the user two options of how to connect.
 *
 * @constructor A ConnectTypeDialogFragment alert
 */
class ConnectTypeDialogFragment : DialogFragment() {

    private lateinit var listener: ConnectTypeDialogListener
    private var selectedOption: Int = InstaCameraManager.CONNECT_TYPE_USB
    interface ConnectTypeDialogListener {
        fun onConfirmClick(dialog: DialogFragment, connectType: Int)
        fun onCancelClick(dialog: DialogFragment)
    }

    fun setListener(listener: ConnectTypeDialogListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            builder
                .setTitle("Choose Connection Type")
                .setPositiveButton("Confirm") { _, _ ->
                    listener.onConfirmClick(this, selectedOption)
                }
                .setNegativeButton("Cancel") { _, _ ->
                    listener.onCancelClick(this)
                }
                .setSingleChoiceItems(
                    arrayOf("USB", "Wi-Fi"), 0
                ) { _, which ->
                    selectedOption = if (which == 0) InstaCameraManager.CONNECT_TYPE_USB else InstaCameraManager.CONNECT_TYPE_WIFI
                }
            // Create the AlertDialog object and return it.
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}