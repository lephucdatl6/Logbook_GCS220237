package com.example.logbook.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.logbook.R
import com.example.logbook.activities.TodoList
import com.example.logbook.activities.UnitConverter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonGoToTodoList: Button = findViewById(R.id.buttonGoToTodoList)
        val buttonGoToUnitConverter: Button = findViewById(R.id.buttonGoToUnitConverter)

        buttonGoToTodoList.setOnClickListener {
            val intent = Intent(this, TodoList::class.java)
            startActivity(intent)
        }

        buttonGoToUnitConverter.setOnClickListener {
            val intent = Intent(this, UnitConverter::class.java)
            startActivity(intent)
        }
    }
}