package com.elllimoner.compassgps.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.elllimoner.compassgps.R

class SensorInfoView (context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {

    private var nameText: TextView? = null
    private var providerText: TextView? = null
    private var accuracyLayout: ViewGroup? = null

    private var name: String = ""
        set(value) {
            nameText?.text = value
            field = value
        }

    var provider: String = ""
        set(value) {
            providerText?.text = value
            field = value
        }

    var accuracy: Int = 0
        set(value) {
            val accuracy = when (value) {
                1 -> R.drawable.accuracy_low
                2 -> R.drawable.accuracy_good
                3 -> R.drawable.accuracy_very_good
                else -> R.drawable.accuracy_bad
            }
            accuracyLayout?.setBackgroundResource(accuracy)
            field = value
        }


    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.sensor_info, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SenorInfoView)
        try {
            nameText = findViewById(R.id.sensorInfoName)
            providerText = findViewById(R.id.sensorInfoProvider)
            accuracyLayout = findViewById(R.id.sensorInfoAccuracy)

            val sensorName = attributes.getString(R.styleable.SenorInfoView_sensorName)
            if (sensorName != null) {
                name = sensorName
            }
        } finally {
            attributes.recycle()
        }
    }
}