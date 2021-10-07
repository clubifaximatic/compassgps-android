package com.elllimoner.compassgps

import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp

import com.elllimoner.compassgps.compass.CompassSensorEvaluator
import com.elllimoner.compassgps.compass.CompassSensorListener
import com.elllimoner.compassgps.databinding.ActivityMainBinding
import com.elllimoner.compassgps.widget.CompassImageView

import kotlin.math.floor


class MainActivity : AppCompatActivity(), CompassSensorListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var compassSensorEvaluator: CompassSensorEvaluator
    private lateinit var compassImage: CompassImageView
    private lateinit var courseText: TextView
    private var currentCourse = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        compassSensorEvaluator = CompassSensorEvaluator(sensorManager)
    }

    override fun onStart() {
        super.onStart()

        compassImage = findViewById(R.id.imgCompass)
        courseText = findViewById(R.id.txtCourse)
    }

    override fun onResume() {
        super.onResume()

        compassSensorEvaluator.registerListener(this)
    }

    override fun onPause() {
        super.onPause()

        compassSensorEvaluator.unregisterListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onSensorChanged(course: Float, pitch: Float, roll: Float) {
        if (Math.abs(currentCourse - course) < 1) {
            return
        }

        currentCourse = course
        courseText.text = "${floor(course).toInt()}"

        compassImage.setCourse(course)
    }

    override fun onAccuracyChanged(accuracy: Int) {
        // nop
    }
}
