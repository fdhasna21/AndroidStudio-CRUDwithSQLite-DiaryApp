package com.fdhasna21.latihancrud_dailyagenda.activity

import android.content.DialogInterface
import android.text.format.DateFormat
import android.view.MenuItem
import android.view.View
import com.fdhasna21.latihancrud_dailyagenda.AppConstant
import com.example.latihancrud_dailyagenda.R
import com.fdhasna21.latihancrud_dailyagenda.activity.base.BaseActivity
import com.example.latihancrud_dailyagenda.databinding.ActivityEditDiaryBinding
import com.fdhasna21.latihancrud_dailyagenda.model.DiaryModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class EditDiary : BaseActivity<ActivityEditDiaryBinding>() {
    private lateinit var diaryModel : DiaryModel
    override var TAG: String = "EditDiary"
    override var screenTitle: String = "Edit Diary"
    override var isBackButtonEnabled: Boolean = true
    override fun setViewBinding(): ActivityEditDiaryBinding = ActivityEditDiaryBinding.inflate(layoutInflater)
    override fun setOptionsMenu(): Int = R.menu.edit_diary_menu

    override fun setupData() {
        val intent = intent
        diaryModel = intent.getParcelableExtra<DiaryModel>(AppConstant.SELECTED_DIARY) ?: DiaryModel()
        binding.editContainer.apply {
            vu.focusableEditText(false)
            binding.editContainer.apply {
                inputDate.setText(diaryModel.date)
                inputTime.setText(diaryModel.time)
                inputTitle.setText(diaryModel.title)
                inputContent.setText(diaryModel.content)
            }
        }
        editable = false
    }
    override fun setupUI() {
        binding.apply {
            editContainer.apply {
                editTexts = arrayListOf(inputDate, inputTime, inputTitle, inputContent)
                editButtonEdit.setOnClickListener {
                    editable = true
                }

                editButtonSave.setOnClickListener {
                    editable = false
                    db.updateDiary(
                        DiaryModel(
                            diaryModel.id,
                            vu.getEditTextData(inputDate),
                            vu.getEditTextData(inputTime),
                            vu.getEditTextData(inputTitle),
                            vu.getEditTextData(inputContent)
                        )
                    )
                }

                editButtonDiscard.setOnClickListener {
                    val builder =
                        MaterialAlertDialogBuilder(this@EditDiary, R.style.AlertDialogTheme)
                    builder.setTitle("Discard Changes")
                    builder.setMessage("Do you want to discard or save them?")
                    builder.setCancelable(false)
                    builder.setIcon(R.drawable.ic_warning)

                    builder.setNeutralButton("Cancel") { dialog: DialogInterface, _ ->
                        dialog.dismiss()
                    }

                    builder.setPositiveButton("Save") { dialog: DialogInterface, _ ->
                        editButtonSave.performClick()
                        dialog.dismiss()
                    }

                    builder.setNegativeButton("Discard") { dialog: DialogInterface, _ ->
                        dialog.dismiss()
                        gotoMainActivity()
                    }

                    builder.show()
                }
            }
        }
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
                    diaryModel.id?.let { db.deleteDiary(it) }
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

    private var editable : Boolean = false
        set(value) {
        binding.apply {
            editContainer.apply {
                when(value){
                    true -> {
                        editButtonEdit.visibility = View.GONE
                        editButtonSave.visibility = View.VISIBLE
                        editButtonDiscard.visibility = View.VISIBLE
                        inputDate.setOnClickListener { addDate() }
                        inputDate.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_calendar, 0,
                            R.drawable.ic_dropdown, 0)
                        inputTime.setOnClickListener { addTime() }
                        inputTime.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_time, 0,
                            R.drawable.ic_dropdown, 0)
                        inputTitle.isFocusableInTouchMode = true
                        inputContent.isFocusableInTouchMode = true
                    }
                    false -> {
                        editButtonEdit.visibility = View.VISIBLE
                        editButtonSave.visibility = View.GONE
                        editButtonDiscard.visibility = View.GONE
                        inputDate.setOnClickListener { null }
                        inputDate.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_calendar, 0,
                            R.drawable.ic_dropdown2, 0)
                        inputTime.setOnClickListener { null }
                        inputTime.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_time, 0,
                            R.drawable.ic_dropdown2, 0)
                        inputTitle.isFocusableInTouchMode = false
                        inputContent.isFocusableInTouchMode = false
                    }
                }
            }
        }
        field = value
    }


    private fun addDate(){
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("WIB"))

        val pos = ParsePosition(0)
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US)
        val lastDate = sdf.parse(vu.getEditTextData(binding.editContainer.inputDate), pos).time

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
            binding.editContainer.inputDate.setText(sdf.format(datePicker.selection))}
        datePicker.addOnNegativeButtonClickListener{}
        datePicker.isCancelable = false
    }

    private fun addTime(){
        //Clock format based on device system
        val isSystem24Hour = DateFormat.is24HourFormat(this)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val calendar = Calendar.getInstance(TimeZone.getDefault())

        val pos = ParsePosition(0)
        val sdf = SimpleDateFormat(if(isSystem24Hour) "HH:mm" else "KK:mm a", Locale.US)
        calendar.time = sdf.parse(vu.getEditTextData(binding.editContainer.inputTime), pos)

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
            binding.editContainer.inputTime.setText(sdf.format(calendar.time))
        }
        timePicker.addOnNegativeButtonClickListener{}
    }

    private fun gotoMainActivity(){
        setResult(AppConstant.ADD_EDIT)
        finish()
    }
}