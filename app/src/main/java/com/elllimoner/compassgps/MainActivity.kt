package com.elllimoner.compassgps

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.location.*
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.elllimoner.compassgps.compass.CompassSensorEvaluator
import com.elllimoner.compassgps.compass.CompassSensorInitializeListener
import com.elllimoner.compassgps.compass.CompassSensorListener
import com.elllimoner.compassgps.gps.GpsSensorEvaluator
import com.elllimoner.compassgps.gps.GpsSensorListener
import com.elllimoner.compassgps.widget.CompassImageView
import com.elllimoner.compassgps.widget.SensorInfoView
import kotlin.math.abs
import kotlin.math.floor


class MainActivity : BaseActivity(), CompassSensorListener, GpsSensorListener {

    private lateinit var compassSensorEvaluator: CompassSensorEvaluator
    private lateinit var gpsSensorEvaluator: GpsSensorEvaluator
    private lateinit var compassImage: CompassImageView
    private lateinit var courseText: TextView
    private lateinit var altitudeText: TextView

    private lateinit var accuracyCompass: SensorInfoView
    private lateinit var accuracyGps: SensorInfoView
    private lateinit var debugText: TextView

    private var currentCourse = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeCompassSensorEvaluator()

        initializeGpsSensorEvaluator()
    }

    override fun onStart() {
        super.onStart()

        compassImage = findViewById(R.id.imgCompass)
        courseText = findViewById(R.id.txtCourse)
        altitudeText = findViewById(R.id.txtAltitude)
        debugText = findViewById(R.id.txtDebug)
        accuracyCompass = findViewById(R.id.accuracyCompass)
        accuracyGps = findViewById(R.id.accuracyGps)
    }

    override fun onResume() {
        super.onResume()

        compassSensorEvaluator.registerListener(this)
        gpsSensorEvaluator.registerListener(this, this)
    }

    override fun onPause() {
        super.onPause()

        compassSensorEvaluator.unregisterListener(this)
        gpsSensorEvaluator.unregisterListener(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    initializeGpsSensorEvaluator()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }
            else -> {
                // nop
            }
        }
    }

    override fun onSensorChanged(yaw: Float, pitch: Float, roll: Float) {
        if (abs(currentCourse - yaw) < 1) {
            return
        }

        currentCourse = yaw
        courseText.text = resources.getString(R.string.courseValueFormat, floor(yaw).toInt())
        compassImage.setCourse(yaw)
    }

    override fun onAccuracyChanged(provider: String, accuracy: Int) {
        accuracyCompass.provider = provider
        accuracyCompass.accuracy = accuracy
    }

    override fun onGpsChanged(location: Location) {
        altitudeText.text = resources.getString(R.string.altitudeValueFormat, location.altitude.toInt())
    }

    override fun onGpsAccuracyChanged(provider: String, accuracy: Int) {
        accuracyGps.provider = provider
        accuracyGps.accuracy = accuracy
    }

    private fun initializeCompassSensorEvaluator() {
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        compassSensorEvaluator = CompassSensorEvaluator(sensorManager)

        val context = this;
        compassSensorEvaluator.init(object : CompassSensorInitializeListener {
            override fun onError(message: String) {
                AlertDialog.Builder(context)
                    .setTitle("Sensor not found")
                    .setMessage(R.string.noSensorFoundForCompass)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        });
    }

    private fun initializeGpsSensorEvaluator() {
        gpsSensorEvaluator = GpsSensorEvaluator(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
        }
    }
}
