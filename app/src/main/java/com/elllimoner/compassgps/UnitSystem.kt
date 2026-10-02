package com.elllimoner.compassgps

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.edit

// Exact definitions (international yard and pound agreement, 1959)
private const val METERS_PER_FOOT = 0.3048
private const val METERS_PER_MILE = 1609.344
private const val METERS_PER_KILOMETER = 1000.0
private const val SECONDS_PER_HOUR = 3600.0

// Location gives altitude in meters: metric shows it as is, imperial in feet (~3.28084)
private const val METERS_PER_METER = 1.0
private const val FEET_PER_METER = 1 / METERS_PER_FOOT

// Location gives speed in m/s: 1 m/s = 3600 m/h, which is 3.6 km/h or ~2.23694 mph
private const val KMH_PER_METER_PER_SECOND = SECONDS_PER_HOUR / METERS_PER_KILOMETER
private const val MPH_PER_METER_PER_SECOND = SECONDS_PER_HOUR / METERS_PER_MILE

enum class UnitSystem(
    // multiply meters to get the altitude unit
    private val altitudePerMeter: Double,
    // multiply m/s to get the speed unit
    private val speedPerMeterPerSecond: Double,
    @StringRes val altitudeFormat: Int,
    @StringRes val speedFormat: Int,
) {
    METRIC(
        METERS_PER_METER,
        KMH_PER_METER_PER_SECOND,
        R.string.altitudeValueFormat,
        R.string.speedValueFormat
    ),
    IMPERIAL(
        FEET_PER_METER,
        MPH_PER_METER_PER_SECOND,
        R.string.altitudeValueFormatImperial,
        R.string.speedValueFormatImperial
    );

    fun altitudeFromMeters(meters: Double): Double = meters * altitudePerMeter

    fun speedFromMetersPerSecond(metersPerSecond: Float): Float =
        (metersPerSecond * speedPerMeterPerSecond).toFloat()

    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_UNIT_SYSTEM = "unit_system"

        fun load(context: Context): UnitSystem {
            val name = prefs(context).getString(KEY_UNIT_SYSTEM, null)
            return entries.firstOrNull { it.name == name } ?: METRIC
        }

        fun save(context: Context, unitSystem: UnitSystem) {
            prefs(context).edit { putString(KEY_UNIT_SYSTEM, unitSystem.name) }
        }

        private fun prefs(context: Context) =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
