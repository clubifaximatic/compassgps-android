package com.elllimoner.compassgps.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class CompassSensorEvaluator : SensorEventListener {

    private val sensorManager: SensorManager
    private val samplingPeriod: Int
    private val magnetic: Sensor
    private val accelerometer: Sensor

    private val magneticFieldValues = FloatArray(3)
    private val accelerometerValues = FloatArray(3)
    private val rotationMatrixR = FloatArray(9)
    private val rotationMatrixI = FloatArray(9)
    private val orientation = FloatArray(3)

    private var accelerometerUpdated = false
    private var magneticFieldUpdated = false

    private val listeners: MutableSet<CompassSensorListener> = mutableSetOf()

    constructor(
        sensorManager: SensorManager,
        samplingPeriod: Int = SensorManager.SENSOR_DELAY_NORMAL
    ) {
        this.sensorManager = sensorManager
        this.samplingPeriod = samplingPeriod

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    fun registerListener(listener: CompassSensorListener) {
        listeners.add(listener)

        if (listeners.size == 1) {
            resume()
        }
    }

    fun unregisterListener(listener: CompassSensorListener) {
        listeners.remove(listener)

        if (listeners.isEmpty()) {
            pause()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_MAGNETIC_FIELD -> updateMagneticField(event.values)
            Sensor.TYPE_ACCELEROMETER -> updateAccelerometer(event.values)
            else -> return
        }

        if (accelerometerUpdated && magneticFieldUpdated) {
            if (SensorManager.getRotationMatrix(
                    rotationMatrixR,
                    rotationMatrixI,
                    accelerometerValues,
                    magneticFieldValues
                )
            ) {
                SensorManager.getOrientation(rotationMatrixR, orientation)
                updateListeners()
            }
            accelerometerUpdated = false
            magneticFieldUpdated = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        for (value in listeners) {
            value.onAccuracyChanged(accuracy)
        }
    }

    private fun resume() {
        sensorManager.registerListener(this, accelerometer, samplingPeriod)
        sensorManager.registerListener(this, magnetic, samplingPeriod)
    }

    private fun pause() {
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetic)
    }

    private fun updateListeners() {
        val course = (Math.toDegrees(orientation[0].toDouble()) + 360) % 360
        val pitch = Math.toDegrees(orientation[1].toDouble())
        val roll = Math.toDegrees(orientation[2].toDouble())

        for (value in listeners) {
            value.onSensorChanged(course.toFloat(), pitch.toFloat(), roll.toFloat())
        }
    }

    private fun updateMagneticField(newValues: FloatArray) {
        inPlaceCopy(magneticFieldValues, newValues)
        magneticFieldUpdated = true
    }

    private fun updateAccelerometer(newValues: FloatArray) {
        inPlaceCopy(accelerometerValues, newValues)
        accelerometerUpdated = true
    }

    private fun inPlaceCopy(destination: FloatArray, source: FloatArray) {
        for ((i, value) in source.withIndex()) {
            destination[i] = value
        }
    }

    private fun printFloatArray(values: FloatArray): String {
        return "[" + values[0] + " " + values[1] + " " + values[2] + "]"
    }
}