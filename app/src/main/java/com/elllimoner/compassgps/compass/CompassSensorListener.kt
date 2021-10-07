package com.elllimoner.compassgps.compass

interface CompassSensorListener {
    fun onSensorChanged(course: Float, pitch: Float, roll: Float)

    fun onAccuracyChanged(accuracy: Int)
}