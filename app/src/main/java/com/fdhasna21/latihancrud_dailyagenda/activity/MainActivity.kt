package com.fdhasna21.latihancrud_dailyagenda.activity

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdhasna21.latihancrud_dailyagenda.AppConstant
import com.example.latihancrud_dailyagenda.R
import com.fdhasna21.latihancrud_dailyagenda.activity.base.BaseActivity
import com.fdhasna21.latihancrud_dailyagenda.adapter.DiaryAdapter
import com.example.latihancrud_dailyagenda.databinding.ActivityMainBinding
import com.fdhasna21.latihancrud_dailyagenda.model.DiaryModel

class MainActivity : BaseActivity<ActivityMainBinding>(){
    override var TAG : String = "MainActivity"
    override var screenTitle: String = "Diary"
    override var isBackButtonEnabled: Boolean = false
    override fun setViewBinding() = ActivityMainBinding.inflate(layoutInflater)
    override fun setOptionsMenu(): Int? = null

    override fun setupUI() {
        binding.floatingAdd.setOnClickListener{
            val intent = Intent(this@MainActivity, AddDiary::class.java)
            activityForResult.launch(intent)
        }
    }

    override fun setupData() {
        showAllDiaries()
    }

    private fun showAllDiaries(){
        val outputArray = getOutputArray()
        binding.apply {
            if(outputArray.size == 0){
                txtNoRecords.visibility = View.VISIBLE
                diaryRecycler.visibility = View.INVISIBLE
            }
            else{
                txtNoRecords.visibility = View.INVISIBLE
                diaryRecycler.visibility = View.VISIBLE}
        }
    }

    private fun getOutputArray():ArrayList<DiaryModel>{
        val dataOut = db.showDiary()
        val diaryAdapter = DiaryAdapter(dataOut)
        diaryAdapter.setOnItemClickListener(object : DiaryAdapter.OnItemClickListener{
            override fun onItemClicked(position: Int, item: DiaryModel) {
                val intent = Intent(this@MainActivity, EditDiary::class.java)
                intent.putExtra(AppConstant.SELECTED_DIARY, item)
                activityForResult.launch(intent)
            }
        })
        binding.diaryRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = diaryAdapter

        }
        return dataOut
    }

    override fun getResultFromActivity(resultCode: Int) {
        when(resultCode){
            AppConstant.ADD_EDIT -> {
                showAllDiaries()
            }
        }
    }
}