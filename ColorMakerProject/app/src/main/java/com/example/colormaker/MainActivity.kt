package com.example.colormaker

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // Declare variables for controls/views
    private lateinit var inputRed: EditText
    private lateinit var inputGreen: EditText
    private lateinit var inputBlue: EditText

    private lateinit var seekBarRed: SeekBar
    private lateinit var seekBarGreen: SeekBar
    private lateinit var seekBarBlue: SeekBar

    private lateinit var switchRed: SwitchCompat
    private lateinit var switchGreen: SwitchCompat
    private lateinit var switchBlue: SwitchCompat

    private lateinit var viewColor: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // link our control/view variables to the ui components
        inputRed = findViewById(R.id.inputRed)
        inputGreen = findViewById(R.id.inputGreen)
        inputBlue = findViewById(R.id.inputBlue)

        seekBarRed = findViewById(R.id.seekBarRed)
        seekBarGreen = findViewById(R.id.seekBarGreen)
        seekBarBlue = findViewById(R.id.seekBarBlue)

        switchRed = findViewById(R.id.switchRed)
        switchGreen = findViewById(R.id.switchGreen)
        switchBlue = findViewById(R.id.switchBlue)

        viewColor = findViewById(R.id.viewColor)

        // Initialize default values
        setInitialValues()

        // Set seekBar listeners
        setSeekBarListener(seekBarRed, inputRed)
        setSeekBarListener(seekBarGreen, inputGreen)
        setSeekBarListener(seekBarBlue, inputBlue)

        // EditText change listeners
        setEditTextListener(inputRed, seekBarRed)
        setEditTextListener(inputGreen, seekBarGreen)
        setEditTextListener(inputBlue, seekBarBlue)

        // Set listeners for switches
        setSwitchListener(switchRed, seekBarRed, inputRed, R.color.red, R.color.darkRed)
        setSwitchListener(switchGreen, seekBarGreen, inputGreen, R.color.green, R.color.darkGreen)
        setSwitchListener(switchBlue, seekBarBlue, inputBlue, R.color.blue, R.color.darkBlue)

        // Set initial viewColor color
        updateColor()

        // Set initial toggle states for switches
        switchRed.isChecked = true
        switchGreen.isChecked = true
        switchBlue.isChecked = true
    }

    // Function initializes various controls to default values
    private fun setInitialValues() {
        val redDefault = getString(R.string.defaultRedValue).toFloatOrNull() ?: 0.0f
        val greenDefault = getString(R.string.defaultGreenValue).toFloatOrNull() ?: 0.0f
        val blueDefault = getString(R.string.defaultBlueValue).toFloatOrNull() ?: 0.0f

        // Convert Decimal to Whole number for seekbar init
        seekBarRed.progress = (redDefault * 100).toInt()
        seekBarGreen.progress = (greenDefault * 100).toInt()
        seekBarBlue.progress = (blueDefault * 100).toInt()

        // Added Locale.ROOT to appease Android Studio Warning
        inputRed.setText(String.format(Locale.ROOT, "%.2f", redDefault))
        inputGreen.setText(String.format(Locale.ROOT, "%.2f", greenDefault))
        inputBlue.setText(String.format(Locale.ROOT, "%.2f", blueDefault))
    }

    // Function updates the color of the viewColor square
    private fun updateColor() {

        // Check switch states and update the view color accordingly
        // Note: Need to convert form 0-100 format used by seekBar.progress,
        // to an rgb compatible format.
        val redValue = if (switchRed.isChecked) (seekBarRed.progress * 255 / 100) else 0
        val greenValue = if (switchGreen.isChecked) (seekBarGreen.progress * 255 / 100) else 0
        val blueValue = if (switchBlue.isChecked) (seekBarBlue.progress * 255 / 100) else 0

        // update the colorView with the determined RGB from above
        viewColor.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue))
    }

    // Function updates the color of toggle switches to a light/dark version depending on the state
    private fun updateSwitchColor(
        switchCompat: SwitchCompat, thumbColorRes: Int, trackColorRes: Int
    ) {
        switchCompat.thumbTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                this,
                if (switchCompat.isChecked) thumbColorRes else trackColorRes
            )
        )
        switchCompat.trackTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, trackColorRes))
    }


    // Below are all of the Listener initializing functions

    // Set up the switch listener
    private fun setSwitchListener(
        switchCompat: SwitchCompat,
        seekBar: SeekBar,
        input: EditText,
        thumbColorRes: Int,
        trackColorRes: Int
    ) {
        switchCompat.setOnCheckedChangeListener { _, _ ->
            updateSwitchColor(switchCompat, thumbColorRes, trackColorRes)

            // Toggle enabled based on switch
            seekBar.isEnabled = switchCompat.isChecked
            input.isEnabled = switchCompat.isChecked

            // Update color when switch changes
            updateColor()
        }
    }

    // Set up seekbar listener
    private fun setSeekBarListener(seekBar: SeekBar, input: EditText) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                // Check if fromUser to avoid circular loop
                if (fromUser) {

                    // Update color when SeekBar changes
                    input.setText(String.format(Locale.ROOT, "%.2f", progress.toFloat() / 100.0))
                    updateColor()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setEditTextListener(input: EditText, seekBar: SeekBar) {
        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    val inputValue = s.toString().toFloatOrNull() ?: 0f
                    val progress = (inputValue * 100).coerceIn(0f, 100f).toInt()
                    seekBar.progress = progress
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
