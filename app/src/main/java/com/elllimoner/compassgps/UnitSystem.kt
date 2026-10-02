package com.elllimoner.compassgps

import android.content.Context
import androidx.core.content.edit

enum class UnitSystem {
    METRIC,
    IMPERIAL;

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
