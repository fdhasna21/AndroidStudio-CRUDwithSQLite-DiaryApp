package com.fdhasna21.latihancrud_dailyagenda

import android.widget.EditText

open class ViewUtils {
    private var editTexts : ArrayList<EditText> = arrayListOf()

    fun setupView(editTexts : ArrayList<EditText>){
        editTexts.let {
            this.editTexts = editTexts
        }
    }

    fun getEditTextData(editText: EditText): String {
        return editText.text.toString()
    }

    fun resetEditText(editText: EditText){
        editText.text.clear()
    }

    fun resetEditText(){
        for(editText in editTexts){
            resetEditText(editText)
        }
    }

    fun focusableEditText(boolean: Boolean, editText: EditText){
        editText.isFocusable = boolean
    }

    fun focusableEditText(boolean: Boolean){
        for(editText in editTexts){
            focusableEditText(boolean, editText)
        }
    }
}