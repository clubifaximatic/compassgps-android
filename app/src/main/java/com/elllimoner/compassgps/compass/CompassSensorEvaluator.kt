package com.elllimoner.compassgps.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.elllimoner.compassgps.R

class CompassSensorEvaluator : SensorEventListener {

    private val sensorManager: SensorManager
    private val samplingPeriod: Int
    private var rotation: Sensor? = null
    private var magnetic: Sensor? = null
    private var accelerometer: Sensor? = null
    private var compass: Sensor? = null

    private val magneticFieldValues = FloatArray(3)
    private val accelerometerValues = FloatArray(3)
    private val rotationMatrixR = FloatArray(9)
    private val rotationMatrixI = FloatArray(9)

    private var accelerometerUpdated = false
    private var magneticFieldUpdated = false

    private val listeners: MutableSet<CompassSensorListener> = mutableSetOf()

    constructor(
        sensorManager: SensorManager,
        samplingPeriod: Int = SensorManager.SENSOR_DELAY_NORMAL,
    ) {
        this.sensorManager = sensorManager
        this.samplingPeriod = samplingPeriod
    }

    fun init(listener: CompassSensorInitializeListener) {
        rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotation != null) {
            return
        }

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (magnetic != null && accelerometer != null) {
            return
        }

        compass = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        if (compass != null) {
            return
        }

        listener.onError("")
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
            Sensor.TYPE_ROTATION_VECTOR -> updateRotationVector(event.values)
            Sensor.TYPE_MAGNETIC_FIELD -> updateMagneticField(event.values)
            Sensor.TYPE_ACCELEROMETER -> updateAccelerometer(event.values)
            Sensor.TYPE_ORIENTATION -> notifyListeners(event.values[0], event.values[1],event.values[2])
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
                val rads = FloatArray(3)
                SensorManager.getOrientation(rotationMatrixR, rads)

                val yaw = (Math.toDegrees(rads[0].toDouble()) + 360) % 360
                val pitch = Math.toDegrees(rads[1].toDouble())
                val roll = Math.toDegrees(rads[2].toDouble())

                notifyListeners(yaw.toFloat(), pitch.toFloat(), roll.toFloat())
            }
            accelerometerUpdated = false
            magneticFieldUpdated = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        val provider = if (sensor?.name == null) "" else sensor.name
        for (value in listeners) {
            value.onAccuracyChanged(provider, accuracy)
        }
    }

    private fun resume() {
        if (rotation != null) {
            sensorManager.registerListener(this, rotation, samplingPeriod)
        }
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, samplingPeriod)
        }
        if (magnetic != null) {
            sensorManager.registerListener(this, magnetic, samplingPeriod)
        }
        if (compass != null) {
            sensorManager.registerListener(this, compass, samplingPeriod)
        }
    }

    private fun pause() {
        if (rotation != null) {
            sensorManager.unregisterListener(this, rotation)
        }
        if (accelerometer != null) {
            sensorManager.unregisterListener(this, accelerometer)
        }
        if (magnetic != null) {
            sensorManager.unregisterListener(this, magnetic)
        }
        if (compass != null) {
            sensorManager.unregisterListener(this, compass)
        }
    }

    private fun notifyListeners(yaw: Float, pitch: Float, roll: Float) {
        for (value in listeners) {
//            println(" > $yaw, $pitch, $roll")
            value.onSensorChanged(yaw, pitch, roll)
        }
    }

    private fun updateRotationVector(newValues: FloatArray) {
        SensorManager.getRotationMatrixFromVector(rotationMatrixR, newValues);
        val rads = FloatArray(3)
        SensorManager.getOrientation(rotationMatrixR, rads)

        val yaw = (Math.toDegrees(rads[0].toDouble()) + 360) % 360
        val pitch = Math.toDegrees(rads[1].toDouble())
        val roll = Math.toDegrees(rads[2].toDouble())

        notifyListeners(yaw.toFloat(), pitch.toFloat(), roll.toFloat())
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