package com.example.latihancrud_dailyagenda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    fun showAllDiaries(){
        val outputArray = getOutputArray()
        if(outputArray.size == 0){
            txt_noRecords.visibility = View.VISIBLE
            diary_recycler.visibility = View.INVISIBLE
        }
        else{
            txt_noRecords.visibility = View.INVISIBLE
            diary_recycler.visibility = View.VISIBLE}
    }

    private fun getOutputArray():ArrayList<DiaryModel>{
        val databaseHandler = DatabaseHandler(this)
        val dataOut = databaseHandler.showDiary()
        diary_recycler.layoutManager = LinearLayoutManager(this)
        diary_recycler.adapter = DiaryAdapter(dataOut, this)
        return dataOut
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.setTitle("Diary")

        showAllDiaries()
        val intent = intent
        if(intent.getStringExtra("refresh")=="ok"){showAllDiaries()}

        floating_add.setOnClickListener{
            val intent = Intent(this, AddDiary::class.java)
            this.startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.topbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.topbar_refresh -> {
                showAllDiaries()
                Toast.makeText(this,"Refresh database", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}