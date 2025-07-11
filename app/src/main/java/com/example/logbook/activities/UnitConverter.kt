package com.example.logbook.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.logbook.R

class UnitConverter : AppCompatActivity() {

    // List of units
    val unitNames = listOf(
        "Metre",
        "Millimetre",
        "Mile",
        "Foot"
    )

    val unitToMeter = mapOf(
        "Metre" to 1.0,
        "Millimetre" to 0.001,
        "Mile" to 1609.344,
        "Foot" to 0.3048
    )

    lateinit var editTextFrom: EditText
    lateinit var editTextTo: EditText
    lateinit var listViewFrom: ListView
    lateinit var listViewTo: ListView
    lateinit var textFrom: TextView
    lateinit var textTo: TextView

    var selectedFromIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_converter)

        editTextFrom = findViewById(R.id.editTextFrom)
        editTextTo = findViewById(R.id.editTextTo)
        listViewFrom = findViewById(R.id.listViewFrom)
        listViewTo = findViewById(R.id.listViewTo)
        textFrom = findViewById(R.id.textFrom)
        textTo = findViewById(R.id.textTo)

        // Adapter for "From" ListView
        val fromAdapter = ArrayAdapter(this, R.layout.list_item_unit, R.id.textUnit, unitNames)
        listViewFrom.adapter = fromAdapter
        listViewFrom.choiceMode = ListView.CHOICE_MODE_SINGLE
        listViewFrom.setItemChecked(0, true)

        // Adapter for "To" ListView
        val toAdapter =
            ArrayAdapter(this, R.layout.list_item_unit, R.id.textUnit, getConvertedValues(1.0, 0))
        listViewTo.adapter = toAdapter
        listViewTo.choiceMode = ListView.CHOICE_MODE_SINGLE
        listViewTo.setItemChecked(0, true)

        // Default values
        editTextFrom.setText("1")
        editTextTo.setText("1")
        textFrom.text = unitNames[0]
        textTo.text = unitNames[0]

        // When input value changes
        editTextFrom.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateToList()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // When "From" is selected
        listViewFrom.setOnItemClickListener { parent, view, position, _ ->
            selectedFromIndex = position
            textFrom.text = unitNames[position]
            textTo.text = unitNames[0]
            updateToList()
            listViewFrom.setItemChecked(position, true)
        }

        // When "To" is selected
        listViewTo.setOnItemClickListener { parent, view, position, _ ->
            val inputValue = editTextFrom.text.toString().toDoubleOrNull() ?: 0.0
            val toValue = getConvertedValue(inputValue, selectedFromIndex, position)
            editTextTo.setText(toValue.toString())

            // Update To label
            val toUnit = unitNames[position]
            textTo.text = toUnit

            listViewTo.setItemChecked(position, true)
        }
    }

    private fun updateToList() {
        val inputValue = editTextFrom.text.toString().toDoubleOrNull() ?: 0.0
        val toAdapter = listViewTo.adapter as ArrayAdapter<String>
        toAdapter.clear()
        toAdapter.addAll(getConvertedValues(inputValue, selectedFromIndex))
        toAdapter.notifyDataSetChanged()

        listViewTo.setItemChecked(0, true)

        val firstToValue = getConvertedValue(inputValue, selectedFromIndex, 0)
        editTextTo.setText(firstToValue.toString())
    }

    private fun getConvertedValues(value: Double, fromIndex: Int): List<String> {
        val fromUnit = unitNames[fromIndex]
        val fromFactor = unitToMeter[fromUnit] ?: 1.0
        val valueInMeters = value * fromFactor

        return unitNames.map { toUnit ->
            val toFactor = unitToMeter[toUnit] ?: 1.0
            val converted = if (toFactor != 0.0) valueInMeters / toFactor else 0.0
            if (fromUnit == toUnit) "$toUnit ($value)"
            else "$toUnit (${String.format("%.4f", converted)})"
        }
    }

    private fun getConvertedValue(value: Double, fromIndex: Int, toIndex: Int): Double {
        val fromUnit = unitNames[fromIndex]
        val toUnit = unitNames[toIndex]
        val fromFactor = unitToMeter[fromUnit] ?: 1.0
        val toFactor = unitToMeter[toUnit] ?: 1.0
        val valueInMeters = value * fromFactor
        return if (toFactor != 0.0) valueInMeters / toFactor else 0.0
    }
}