@file:Suppress("DEPRECATION")

package com.prabalbhavishya.cars

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset

class UsageStats : AppCompatActivity() {
    private var MY_USAGE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_stats)

        Log.d("AppsUsageStats: ", "Reading file")
        val usageStatsTextView = findViewById<TextView>(R.id.usageStats_TextView1)
        var textInFile: String

        //Read usage stats from disk
        try {
            val path = getExternalFilesDir("UsageDir")
            val file = File(path, "appUsageData.csv")
            if(!file.isFile){
                throw Exception("File doesn't exist. Please wait for 15 minutes")
            }
            else{
                val fileInputStream = FileInputStream(file)
                textInFile = fileInputStream.readBytes().toString(Charset.defaultCharset())
                fileInputStream.close()
                Log.d("AppsUsageStats: ", "Read successful")
            }
        }
        catch (e: Exception){
            textInFile = e.toString()
            Log.d("AppsUsageStats: ", "Read failed")
            requestPermissionFromUser()
        }
        usageStatsTextView.text = textInFile
    }

    private fun requestPermissionFromUser(){
        val appOps: AppOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName) != PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_USAGE_REQUEST_CODE)
                Log.d("AppsUsageStats: ", "Asking permission Q")
            }
        } else if (appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName) != PackageManager.PERMISSION_GRANTED) {
            /*val permissionString = Array<String>(1){""}
            permissionString[0] = android.Manifest.permission.PACKAGE_USAGE_STATS
            ActivityCompat.requestPermissions(this, permissionString, MY_USAGE_REQUEST_CODE)*/
            startActivityForResult(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_USAGE_REQUEST_CODE)
            Log.d("AppsUsageStats: ", "Asking permission legacy")
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_USAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}