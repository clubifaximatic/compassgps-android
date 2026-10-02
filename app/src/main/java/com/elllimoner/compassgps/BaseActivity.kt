package com.elllimoner.compassgps;

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController

import com.elllimoner.compassgps.databinding.ActivityMainBinding

open class BaseActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        applySystemBarInsets()

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    // Android 15+ draws edge-to-edge: the app bar sits below the status bar and the notch
    private fun applySystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBar) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(left = bars.left, top = bars.top, right = bars.right)
            insets
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val checkedId = when (UnitSystem.load(this)) {
            UnitSystem.METRIC -> R.id.units_metric
            UnitSystem.IMPERIAL -> R.id.units_imperial
        }
        menu.findItem(checkedId).isChecked = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Radio items in a menu are not checked automatically
            R.id.units_metric, R.id.units_imperial -> {
                item.isChecked = true
                val unitSystem =
                    if (item.itemId == R.id.units_imperial) UnitSystem.IMPERIAL else UnitSystem.METRIC
                UnitSystem.save(this, unitSystem)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
