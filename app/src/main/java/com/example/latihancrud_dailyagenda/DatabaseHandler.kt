package com.example.latihancrud_dailyagenda

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context)
    :SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private val DATABASE_NAME = "DiaryDatabase"
        private val DATABASE_VERSION = 1

        private val TABLE_NAME = "DiaryTable"
        private val KEY_ID = "id"
        private val KEY_DATE = "date"
        private val KEY_TIME = "time"
        private val KEY_TITLE = "title"
        private val KEY_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME($KEY_ID INTEGER PRIMARY KEY, $KEY_DATE TEXT, $KEY_TIME TEXT, $KEY_TITLE TEXT, $KEY_CONTENT TEXT);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addDiary(diary: DiaryModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(KEY_DATE, diary.date)
        contentValues.put(KEY_TIME, diary.time)
        contentValues.put(KEY_TITLE, diary.title)
        contentValues.put(KEY_CONTENT, diary.content)

        val success = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return success
    }

    fun showDiary(): ArrayList<DiaryModel> {
        val db = this.writableDatabase
        val tempDiary = ArrayList<DiaryModel>()
        val sqlCommad = "SELECT * FROM $TABLE_NAME;"

        var cursor : Cursor? = null
        try{
            cursor = db.rawQuery(sqlCommad, null)
        }
        catch (e:SQLException){
            db.execSQL(sqlCommad)
            return arrayListOf()
        }

        if(cursor.moveToFirst()){
            do{
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val date = cursor.getString(cursor.getColumnIndex(KEY_DATE))
                val time = cursor.getString(cursor.getColumnIndex(KEY_TIME))
                val title = cursor.getString(cursor.getColumnIndex(KEY_TITLE))
                val content = cursor.getString(cursor.getColumnIndex(KEY_CONTENT))
                tempDiary.add(DiaryModel(id, date, time, title, content))
            }while(cursor.moveToNext())
        }
        cursor.close()
        return tempDiary
    }

    fun deleteDiary(diary:DiaryModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(KEY_ID, diary.id)

        val success = db.delete(TABLE_NAME, "$KEY_ID = ${diary.id}", null)
        db.close()
        return success
    }

    fun updateDiary(diary:DiaryModel):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(KEY_ID, diary.id)
        contentValues.put(KEY_DATE, diary.date)
        contentValues.put(KEY_TIME, diary.time)
        contentValues.put(KEY_TITLE, diary.title)
        contentValues.put(KEY_CONTENT, diary.content)

        val success = db.update(TABLE_NAME, contentValues, "$KEY_ID = ${diary.id}", null)
        db.close()
        return success
    }
}