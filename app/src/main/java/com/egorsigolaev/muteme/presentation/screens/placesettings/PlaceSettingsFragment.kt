package com.egorsigolaev.muteme.presentation.screens.placesettings

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.data.models.UserCoordinates
import com.egorsigolaev.muteme.domain.models.VolumeMode
import com.egorsigolaev.muteme.presentation.base.BaseFragment
import com.egorsigolaev.muteme.presentation.helpers.ViewModelFactory
import com.egorsigolaev.muteme.presentation.helpers.injectViewModel
import com.egorsigolaev.muteme.presentation.screens.addplace.AddPlaceFragment
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
        textViewPlaceEnterRadius.text = getString(R.string.place_enter_radius, 50)
        textViewVolumeMode.setOnClickListener {
            expandChooseVolumeDialog()
        }
        buttonNext.setOnClickListener {
            val placeName = editTextPlaceName.editText?.text.toString().trim()
            val placeDescription = editTextPlaceDescription.editText?.text.toString().trim()
            if(viewModel.fieldsFilledCorrect(placeName = placeName, placeDescription = placeDescription)){
                viewModel.obtainEvent(PlaceSettingsViewEvent.SavePlace(viewModel.getPlace(
                    name = placeName,
                    description = placeDescription,
                    volumeMode = viewModel.getSelectedVolumeMode(),
                    placeCoordinates = arguments?.getParcelable(AddPlaceFragment.PLACE_COORDINATED_BUNDLE_DATA)!!,
                    enterRadiusMeters = sliderEnterRadius.progress + 50
                )))
            }
        }
        sliderEnterRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewPlaceEnterRadius.text = getString(R.string.place_enter_radius, progress + 50)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
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
            PlaceSettingsLoadingState.PlaceSavingStarted -> {
                //TODO Maybe add loading bar
            }
            PlaceSettingsLoadingState.PlaceSavingFinished -> {
                findNavController().navigate(R.id.action_to_main_fragment)
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