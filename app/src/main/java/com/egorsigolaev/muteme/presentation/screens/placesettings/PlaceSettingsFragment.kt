package com.egorsigolaev.muteme.presentation.screens.placesettings

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.domain.models.VolumeMode
import com.egorsigolaev.muteme.presentation.base.BaseFragment
import com.egorsigolaev.muteme.presentation.helpers.ViewModelFactory
import com.egorsigolaev.muteme.presentation.helpers.injectViewModel
import com.egorsigolaev.muteme.presentation.screens.placesettings.models.PlaceSettingsLoadingState
import com.egorsigolaev.muteme.presentation.screens.placesettings.models.PlaceSettingsViewAction
import com.egorsigolaev.muteme.presentation.screens.placesettings.models.PlaceSettingsViewEvent
import com.egorsigolaev.muteme.presentation.screens.placesettings.models.PlaceSettingsViewState
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_place_settings.*
import javax.inject.Inject

class PlaceSettingsFragment : BaseFragment(R.layout.fragment_place_settings),
    VolumeModeBottomSheetDialog.OnModeSelectListener {

    @Inject
    lateinit var factory: ViewModelFactory
    private lateinit var viewModel: PlaceSettingsViewModel
    private val chooseVolumeModeDialog by lazy {
        VolumeModeBottomSheetDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = injectViewModel(factory = factory)
        viewModel.apply {
            viewStates().observe(viewLifecycleOwner, Observer { bindViewState(viewState = it) })
            viewAction().observe(viewLifecycleOwner, Observer { bindViewAction(viewAction = it) })
        }
        textViewVolumeMode.setOnClickListener {
            expandChooseVolumeDialog()
        }
        buttonNext.setOnClickListener {
            val placeName = editTextPlaceName.editText?.text.toString().trim()
            val placeDescription = editTextPlaceDescription.editText?.text.toString().trim()
            if(viewModel.fieldsFilledCorrect(placeName = placeName, placeDescription = placeDescription)){
                //TODO Back to main screen
            }
        }
    }

    private fun bindViewAction(viewAction: PlaceSettingsViewAction) {
        when(viewAction){
            is PlaceSettingsViewAction.ShowError -> showToast(viewAction.message)
        }
    }

    private fun bindViewState(viewState: PlaceSettingsViewState) {
        when(viewState.loadingState){
            PlaceSettingsLoadingState.VolumeModeSelected -> {
                viewState.volumeMode?.let {
                    textViewVolumeMode.text = getString(it.stringRes())
                }
            }
            PlaceSettingsLoadingState.VolumeModeNotSelected -> {
                textViewVolumeMode.text = getString(R.string.not_specified)
            }
        }
    }

    private fun expandChooseVolumeDialog(){
        chooseVolumeModeDialog.show(childFragmentManager, "choose_volume_bottom_sheet")
    }

    private fun collapseChooseVolumeDialog(){
        chooseVolumeModeDialog.dismiss()
    }

    override fun onModeSelected(volumeMode: VolumeMode) {
        collapseChooseVolumeDialog()
        viewModel.obtainEvent(PlaceSettingsViewEvent.SelectVolumeMode(volumeMode = volumeMode))
    }

}