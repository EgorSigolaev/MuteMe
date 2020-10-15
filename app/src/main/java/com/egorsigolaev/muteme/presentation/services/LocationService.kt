package com.egorsigolaev.muteme.presentation.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.egorsigolaev.muteme.MuteMeApp.Companion.TAG
import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.data.models.Place
import com.egorsigolaev.muteme.data.models.UserCoordinates
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import dagger.android.AndroidInjection
import javax.inject.Inject


class LocationService(): Service() {

    private var places: ArrayList<Place> = arrayListOf()
    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val placesIntent = it.getParcelableArrayListExtra<Place>(PLACES_EXTRA)
            placesIntent?.let {
                places.clear()
                places.addAll(placesIntent)
            }

        }
        getLocation()
        return START_STICKY
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        showForegroundNotification()
    }

    private fun showForegroundNotification(){
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                FOREGROUND_NOTIFICATION_CHANNEL_ID,
                "Location service notification channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            val notification: Notification = Notification.Builder(
                this,
                FOREGROUND_NOTIFICATION_CHANNEL_ID
            )
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.foreground_notification_description)).build()
            startForeground(FOREGROUND_NOTIFICATION_ID, notification)
        }
    }

    private fun getLocation(){
        val mLocationRequestHighAccuracy = LocationRequest()
        mLocationRequestHighAccuracy.interval = UPDATE_INTERVAL
        mLocationRequestHighAccuracy.fastestInterval = FASTEST_INTERVAL
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.")
            stopSelf()
            return
        }

        Log.d(TAG, "getLocation: getting location information.")
        fusedLocationClient.requestLocationUpdates(
            mLocationRequestHighAccuracy, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    Log.d(TAG, "onLocationResult: got location result.")
                    val location: Location? = locationResult.lastLocation
                    location?.let {
                        val geoCoder = Geocoder(applicationContext)
                        val user = geoCoder.getFromLocation(it.latitude, it.longitude, 1)[0]
                        val userLocation =
                            UserCoordinates(
                                latitude = user.latitude,
                                longitude = user.longitude
                            )
                        // Location data
                        val userLocationDataIntent = Intent(LOCATION_ACTION)
                        userLocationDataIntent.putExtra(LOCATION_DATA, userLocation)
                        sendBroadcast(userLocationDataIntent)

                        //All screen data
                        val screenDataIntent = Intent(SCREEN_DATA_ACTION)
                        userLocationDataIntent.putExtra(SCREEN_DATA, userLocation)
                        sendBroadcast(screenDataIntent)
                    }
                }
            },
            Looper.getMainLooper()
        )


    }

    companion object{
        private const val FOREGROUND_NOTIFICATION_CHANNEL_ID = "FOREGROUND_NOTIFICATION_CHANNEL_ID"
        private const val FOREGROUND_NOTIFICATION_ID = 1

        private const val UPDATE_INTERVAL = 4000L // 4 secs
        private const val FASTEST_INTERVAL: Long = 2000L // 2 secs

        const val LOCATION_DATA = "LOCATION_DATA"
        const val SCREEN_DATA = "LOCATION_USER_DATA"
        const val PLACES_EXTRA = "PLACES_EXTRA"
        const val RESOLVABLE_EXCEPTION_EXTRA = "RESOLVABLE_EXCEPTION_EXTRA"

        const val GPS_OFF_ERROR_ACTION = "GPS_OFF_ERROR_ACTION"
        const val LOCATION_ACTION = "LOCATION_ACTION"
        const val SCREEN_DATA_ACTION = "SCREEN_DATA_ACTION"
    }


}