package com.baobab.application

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import coil.load
import com.baobab.application.databinding.ActivityMainBinding
import java.io.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    companion object {
        lateinit var prefs : MySharedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = MySharedPreferences(applicationContext)
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        // binding view 영역
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        loadData()
        setCalenders()
        // 바코드 생성
        binding.selectBarcode.setOnClickListener { readBarcode.launch("image/*") }
    }

    private fun loadData(){
        binding.startDateSet.text = prefs.getString("startDate","")
        binding.expDateSet.text = prefs.getString("expDate","")
        binding.dateProgressBar.progress = prefs.getString("dateProgress","0").toInt()
        binding.displayProgress.text = prefs.getString("dateProgress","0") + "%"
        binding.remainDate.text = prefs.getString("remainDate","0")
        binding.viewBarcode.load(filesDir.toString()+"\\"+getString(R.string.fileName_Barcode))
    }

    // 바코드 읽고 저장
    private val readBarcode = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        copyImage(uri, this@MainActivity)
        val barcodePath = File(filesDir, "barcode.png").absolutePath
        binding.viewBarcode.load(barcodePath)


    }

    private fun copyImage(uri: Uri?, context: Context){
        val filename = getString(R.string.fileName_Barcode)
        val inputStream = context.contentResolver.openInputStream(uri!!) // uri 입력
        val outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE) // 저장공간 outputstream 개방
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
    }

    @SuppressLint("SetTextI18n", "NewApi")
    private fun setCalenders() {

        // 시작 날짜
        val calStart = Calendar.getInstance()
        // 종료 날짜
        val calExp = Calendar.getInstance()

        var temp = 0

        if (binding.startDateSet.text == ""){
            binding.expDateButton.isEnabled = false
        }
        binding.startDateButton.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                binding.startDateSet.text = "$y/${m+1}/$d"
                prefs.setString("startDate", binding.startDateSet.text as String)
                binding.expDateButton.isEnabled = true

                if (temp == 1) {progressUpdate()}

            }, calStart.get(Calendar.YEAR), calStart.get(Calendar.MONTH), calStart.get(Calendar.DAY_OF_MONTH)).show()
        }


        binding.expDateButton.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                binding.expDateSet.text = "$y/${m+1}/$d"
                prefs.setString("expDate", binding.expDateSet.text as String)
                temp = 1
                progressUpdate()
            }, calExp.get(Calendar.YEAR), calExp.get(Calendar.MONTH), calExp.get(Calendar.DAY_OF_MONTH)).show()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun progressUpdate(){
        // 변수 호출
        val now = LocalDate.now()
        val strNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        val remainDate = findViewById<TextView>(R.id.remainDate)

        // 날짜 변환
        val sdf = SimpleDateFormat ("yyyy/MM/dd", Locale.KOREAN)
        val startDate = sdf.parse(binding.startDateSet.text as String)
        val expDate = sdf.parse(binding.expDateSet.text as String)
        val nowDate = sdf.parse(strNow)

        val dateProgressBar = findViewById<ProgressBar>(R.id.dateProgressBar)

        val a1 = nowDate!!.time - startDate!!.time
        val a2 = expDate!!.time - startDate.time
        val a3 = (a1 * 100) / a2
        prefs.setString("dateProgress", a3.toString())

        val b1 = expDate.time - nowDate.time
        val b2 = b1 / (60 * 60 * 24 * 1000)
        prefs.setString("remainDate", b2.toString())

        dateProgressBar.progress = a3.toInt()
        remainDate.text = prefs.getString("remainDate","0")
    }



}