package com.elllimoner.compassgps.compass

interface CompassSensorInitializeListener {
    fun onInit()

    fun onError(message: String)
}
