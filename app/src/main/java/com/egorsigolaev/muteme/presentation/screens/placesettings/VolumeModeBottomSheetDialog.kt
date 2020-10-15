package com.egorsigolaev.muteme.presentation.screens.placesettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.domain.models.VolumeMode
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_fragment_choose_volume_mode.*

class VolumeModeBottomSheetDialog(private val listener: OnModeSelectListener?): BottomSheetDialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_fragment_choose_volume_mode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        volumeMode.setOnClickListener {
            listener?.onModeSelected(VolumeMode.VOLUME_MODE)
        }

        vibrationMode.setOnClickListener {
            listener?.onModeSelected(VolumeMode.VIBRATION_MODE)
        }

        silentMode.setOnClickListener {
            listener?.onModeSelected(VolumeMode.SILENT_MODE)
        }
    }

    interface OnModeSelectListener{
        fun onModeSelected(volumeMode: VolumeMode)
    }

}