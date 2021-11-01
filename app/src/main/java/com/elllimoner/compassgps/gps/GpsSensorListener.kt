package com.elllimoner.compassgps.gps

import android.location.Location

interface GpsSensorListener {
    fun onGpsChanged(location: Location)

    fun onGpsAccuracyChanged(provider: String, accuracy: Int)
}