package com.example.latihancrud_dailyagenda.activity.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.latihancrud_dailyagenda.ViewUtils
import com.example.latihancrud_dailyagenda.database.DatabaseHandler

abstract class BaseActivity<B:ViewBinding> : AppCompatActivity() {
    lateinit var binding : B
    abstract var TAG : String
    abstract var screenTitle : String
    abstract var isBackButtonEnabled : Boolean
    abstract fun setViewBinding() : B
    abstract fun setOptionsMenu() : Int?
    abstract fun setupData()
    abstract fun setupUI()

    open var editTexts : ArrayList<EditText> = arrayListOf()
    var vu = ViewUtils()
    var db = DatabaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setViewBinding()
        setContentView(binding.root)
        supportActionBar?.title = screenTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(isBackButtonEnabled)
        setupData()
        setupUI()
        vu.setupView(editTexts)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        setOptionsMenu()?.let { menuInflater.inflate(it, menu) }
        return true
    }

    fun notifyUser(message:String){
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }

    fun gotoActivity(destActivity : Class<*>) {
        startActivity(Intent(this, destActivity))
    }

    val activityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result -> getResultFromActivity(result.resultCode)
    }

    open fun getResultFromActivity(resultCode : Int){}
}