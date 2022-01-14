package com.example.latihancrud_dailyagenda.activity

import android.text.format.DateFormat.is24HourFormat
import android.widget.EditText
import android.widget.Toast
import com.example.latihancrud_dailyagenda.AppConstant
import com.example.latihancrud_dailyagenda.activity.base.BaseActivity
import com.example.latihancrud_dailyagenda.databinding.ActivityAddDiaryBinding
import com.example.latihancrud_dailyagenda.model.DiaryModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*


class AddDiary : BaseActivity<ActivityAddDiaryBinding>() {
    override var TAG: String = "AddDiary"
    override var screenTitle: String = "Add Diary"
    override var isBackButtonEnabled: Boolean = true
    override fun setViewBinding(): ActivityAddDiaryBinding = ActivityAddDiaryBinding.inflate(layoutInflater)
    override fun setOptionsMenu(): Int? = null
    override fun setupData() {}
    override fun setupUI() {
        binding.apply {
            addContainer.apply {
                editTexts = arrayListOf(inputDate, inputTime, inputTitle, inputContent)
                inputDate.setOnClickListener {
                    addDate()
                }

                inputTime.setOnClickListener {
                    addTime()
                }

                addButtonSave.setOnClickListener {
                    if (vu.getEditTextData(inputDate).isEmpty() || vu.getEditTextData(inputTime).isEmpty()) {
                        Toast.makeText(this@AddDiary, "Date and Time cannot be empty.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        addDiaryToDatabase(inputDate, inputTime, inputTitle, inputContent)
                        setResult(AppConstant.ADD_EDIT)
                        finish()
                    }
                }
            }
        }
    }

    /* ====== DATABASE CRUD FUNCTION =================================================================== */
    private fun addDiaryToDatabase(addDate: EditText, addTime : EditText, addTitle: EditText, addContent: EditText){
        db.addDiary(DiaryModel(0, vu.getEditTextData(addDate), vu.getEditTextData(addTime), vu.getEditTextData(addTitle), vu.getEditTextData(addContent)))
        vu.resetEditText()
    }

    private fun addDate(){
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
            binding.addContainer.inputDate.setText(sdf.format(datePicker.selection))
        }
        datePicker.addOnNegativeButtonClickListener{}

    }

    private fun addTime(){
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
            binding.addContainer.inputTime.setText(sdf.format(calendar.time))
        }
        timePicker.addOnNegativeButtonClickListener{}
    }
}