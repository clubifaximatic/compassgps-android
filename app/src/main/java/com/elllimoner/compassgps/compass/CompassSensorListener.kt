package com.elllimoner.compassgps.compass

interface CompassSensorListener {
    fun onSensorChanged(yaw: Float, pitch: Float, roll: Float)

    fun onAccuracyChanged(accuracy: Int)
}