package com.example.latihancrud_dailyagenda

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.activity_add_diary.*
import kotlinx.android.synthetic.main.add_edit_layout.*
import java.text.SimpleDateFormat
import java.util.*


class AddDiary : AppCompatActivity() {
/* ===== GENERAL MAIN ACTIVITY'S FUNCTIONS INITIALIZATION ====================================== */
    private fun getDataEntry(data: EditText):String{
        return data.text.toString()
    }

/* ====== DATABASE CRUD FUNCTION =================================================================== */
    fun addDiaryToDatabase(addDate: EditText, addTime : EditText, addTitle: EditText, addContent: EditText){
        val databaseHandler = DatabaseHandler(this)
        databaseHandler.addDiary(DiaryModel(0, getDataEntry(addDate), getDataEntry(addTime), getDataEntry(addTitle), getDataEntry(addContent)))
        addDate.text.clear()
        addTitle.text.clear()
        addContent.text.clear()
    }

    fun addDate(){
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        calendar[Calendar.MONTH] = Calendar.DECEMBER
        val decThisYear = calendar.timeInMillis
        val constraintBuilder = CalendarConstraints.Builder()
        constraintBuilder.setValidator(DateValidatorPointBackward.now()).setEnd(decThisYear)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .setSelection(today)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setCalendarConstraints(constraintBuilder.build())
            .build()
        datePicker.show(supportFragmentManager, "DATE_PICKER")
        datePicker.isCancelable = false

        datePicker.addOnPositiveButtonClickListener{
            val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US)
            input_date.setText(sdf.format(datePicker.selection))
        }
        datePicker.addOnNegativeButtonClickListener{}

    }

    fun addTime(){
        //Clock format based on device system
        val isSystem24Hour = is24HourFormat(this)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText("Select a time")
                .build()
        timePicker.show(supportFragmentManager, "TIME_PICKER")
        timePicker.isCancelable = false

        timePicker.addOnPositiveButtonClickListener{
            val timeFormat = if(isSystem24Hour) "HH:mm" else "KK:mm a"
            val sdf = SimpleDateFormat(timeFormat, Locale.US)
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)
            input_time.setText(sdf.format(calendar.time))
        }
        timePicker.addOnNegativeButtonClickListener{}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_diary)
        supportActionBar!!.setTitle("Add Diary")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val addDate = findViewById<EditText>(R.id.input_date)
        val addTime = findViewById<EditText>(R.id.input_time)
        val addTitle = findViewById<EditText>(R.id.input_title)
        val addContent = findViewById<EditText>(R.id.input_content)

        input_date.setOnClickListener {
            addDate()
        }

        input_time.setOnClickListener{
            addTime()
        }

        add_button_save.setOnClickListener {
            if(getDataEntry(addDate).isEmpty() || getDataEntry(addTime).isEmpty()){
                Toast.makeText(this,"Date and Time cannot be empty.", Toast.LENGTH_SHORT).show()
            }
            else{
                addDiaryToDatabase(addDate, addTime, addTitle, addContent)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("refresh", "ok")
                this.startActivity(intent)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}