package com.egorsigolaev.muteme.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavHost
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.presentation.screens.addplace.AddPlaceFragment
import com.egorsigolaev.muteme.presentation.screens.addplace.models.AddPlaceViewEvent
import dagger.android.AndroidInjection
import org.greenrobot.eventbus.EventBus


class MainActivity : AppCompatActivity() {

    private val TAG = "LOG_DEBUG"

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    private fun hideSystemUI() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}