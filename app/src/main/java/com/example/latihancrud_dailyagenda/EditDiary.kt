package com.example.latihancrud_dailyagenda

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.activity_edit_diary.*
import kotlinx.android.synthetic.main.add_edit_layout.*
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class EditDiary : AppCompatActivity() {
    var editID : Int = 0

    fun showDiaryToApp(diaryModel: DiaryModel){
        input_date.isFocusable = false
        input_time.isFocusable = false
        input_title.isFocusable = false
        input_content.isFocusable = false

        input_date.setText(diaryModel.date)
        input_time.setText(diaryModel.time)
        input_title.setText(diaryModel.title)
        input_content.setText(diaryModel.content)
    }

    fun editable(condition: Boolean){
        when(condition){
            true -> {
                edit_button_edit.visibility = View.GONE
                edit_button_save.visibility = View.VISIBLE
                edit_button_discard.visibility = View.VISIBLE
                input_date.setOnClickListener { addDate() }
                input_date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, R.drawable.ic_dropdown, 0)
                input_time.setOnClickListener { addTime() }
                input_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time, 0, R.drawable.ic_dropdown, 0)
                input_title.isFocusableInTouchMode = true
                input_content.isFocusableInTouchMode = true
            }
            false -> {
                edit_button_edit.visibility = View.VISIBLE
                edit_button_save.visibility = View.GONE
                edit_button_discard.visibility = View.GONE
                input_date.setOnClickListener { null }
                input_date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, R.drawable.ic_dropdown2, 0)
                input_time.setOnClickListener { null }
                input_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time, 0, R.drawable.ic_dropdown2, 0)
                input_title.isFocusableInTouchMode = false
                input_content.isFocusableInTouchMode = false
            }
        }
    }

    fun addDate(){
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("WIB"))

        val pos = ParsePosition(0)
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US)
        val lastDate = sdf.parse(input_date.text.toString(), pos).time

        calendar[Calendar.MONTH] = Calendar.DECEMBER
        val decThisYear = calendar.timeInMillis
        val constraintBuilder = CalendarConstraints.Builder()
        constraintBuilder.setValidator(DateValidatorPointBackward.now()).setEnd(decThisYear)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .setSelection(lastDate + TimeUnit.DAYS.toMillis(1))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setCalendarConstraints(constraintBuilder.build())
            .build()
        datePicker.show(supportFragmentManager, "DATE_PICKER")
        datePicker.addOnPositiveButtonClickListener{
            val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US)
            input_date.setText(sdf.format(datePicker.selection))}
        datePicker.addOnNegativeButtonClickListener{}
        datePicker.isCancelable = false
    }

    fun addTime(){
        //Clock format based on device system
        val isSystem24Hour = DateFormat.is24HourFormat(this)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val calendar = Calendar.getInstance(TimeZone.getDefault())

        val pos = ParsePosition(0)
        val sdf = SimpleDateFormat(if(isSystem24Hour) "HH:mm" else "KK:mm a", Locale.US)
        calendar.time = sdf.parse(input_time.text.toString(), pos)

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

    fun gotoMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("refresh", "ok")
        this.startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diary)
        supportActionBar!!.setTitle("Dear Diary")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        editID = intent.getIntExtra("editID", 0)
        val editDate = intent.getStringExtra("editDate").toString()
        val editTime = intent.getStringExtra("editTime").toString()
        val editTitle = intent.getStringExtra("editTitle").toString()
        val editContent = intent.getStringExtra("editContent").toString()
        showDiaryToApp(DiaryModel(0, editDate, editTime, editTitle, editContent))
        editable(false)

        edit_button_edit.setOnClickListener {
            editable(true)
        }

        edit_button_save.setOnClickListener {
            editable(false)
            val fixDate = input_date.text.toString()
            val fixTime = input_time.text.toString()
            val fixTitle = input_title.text.toString()
            val fixContent = input_content.text.toString()
            val databaseHandler = DatabaseHandler(this)
            databaseHandler.updateDiary(DiaryModel(editID, fixDate, fixTime, fixTitle, fixContent))
        }

        edit_button_discard.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            builder.setTitle("Discard Changes")
            builder.setMessage("Do you want to discard or save them?")
            builder.setCancelable(false)
            builder.setIcon(R.drawable.ic_warning)

            builder.setNeutralButton("Cancel"){dialog: DialogInterface, which ->
                dialog.dismiss()
            }

            builder.setPositiveButton("Save") { dialog: DialogInterface, which ->
                edit_button_save.performClick()
                dialog.dismiss()
            }

            builder.setNegativeButton("Discard") { dialog: DialogInterface, which ->
                dialog.dismiss()
                gotoMainActivity()
            }

            builder.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_diary_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.topbar_delete -> {
                val builder = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                builder.setTitle("Delete Diary")
                builder.setMessage("Are you sure?")
                builder.setCancelable(false)
                builder.setIcon(R.drawable.ic_warning)

                builder.setPositiveButton("Yes") { dialog: DialogInterface, which ->
                    val fixDate = input_date.text.toString()
                    val fixTime = input_time.text.toString()
                    val fixTitle = input_title.text.toString()
                    val fixContent = input_content.text.toString()
                    val databaseHandler = DatabaseHandler(this)
                    databaseHandler.deleteDiary(DiaryModel(editID, fixDate, fixTime, fixTitle, fixContent))
                    dialog.dismiss()
                    gotoMainActivity()
                }

                builder.setNegativeButton("No") { dialog: DialogInterface, which ->
                    dialog.dismiss()
                }

                builder.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        gotoMainActivity()
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