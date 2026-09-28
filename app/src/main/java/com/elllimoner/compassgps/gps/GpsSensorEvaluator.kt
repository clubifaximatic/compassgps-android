package com.elllimoner.compassgps.gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.math.roundToInt

class GpsSensorEvaluator : LocationListener {
    private val ACCURACY_BAD: Int = 0
    private val ACCURACY_LOW: Int = 1
    private val ACCURACY_GOOD: Int = 2
    private val ACCURACY_VERY_GOOD: Int = 3

    private val locationManager: LocationManager?

    private val listeners: MutableSet<GpsSensorListener> = mutableSetOf()

    constructor(
        context: Context
    ) {
        val locationManager = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
        this.locationManager = locationManager
    }

    fun init() {
    }

    fun registerListener(context: Context, listener: GpsSensorListener) {
        listeners.add(listener)

        if (listeners.size == 1) {
            resume(context)
        }
    }

    fun unregisterListener(listener: GpsSensorListener) {
        listeners.remove(listener)

        if (listeners.isEmpty()) {
            pause()
        }
    }

    override fun onLocationChanged(location: Location) {
        for (value in listeners) {
            value.onGpsChanged(location)
            value.onGpsAccuracyChanged(location.provider.orEmpty(),toAccuracy(location))
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // nop
    }

    // Must be overridden: before API 30 these have no default implementation
    // and the framework throws AbstractMethodError when GPS is toggled.
    override fun onProviderEnabled(provider: String) {
        // nop
    }

    override fun onProviderDisabled(provider: String) {
        // nop
    }

    private fun toAccuracy(location: Location): Int {
        if (!location.hasVerticalAccuracy()) {
            return ACCURACY_BAD
        }

        val accuracy = location.verticalAccuracyMeters
        if (accuracy > 10) {
            return ACCURACY_LOW
        } else if (accuracy > 5) {
            return ACCURACY_GOOD
        }

        return ACCURACY_VERY_GOOD
    }

    private fun resume(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // GPS only: never fall back to network (cell/Wi-Fi) positioning
            if (locationManager?.allProviders?.contains(LocationManager.GPS_PROVIDER) == true) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, this)
            }
        }
    }

    private fun pause() {
        locationManager?.removeUpdates(this)
    }
}