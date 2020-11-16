@file:Suppress("DEPRECATION")

package com.prabalbhavishya.cars

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset

class UsageStats : AppCompatActivity() {
    private var MY_USAGE_REQUEST_CODE = 1

    class EventData{
        var time = ""
        var date = ""
        var timeInForeground = 0
        var packageName = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_stats)

        val appOps: AppOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    packageName
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                startActivityForResult(
                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                    MY_USAGE_REQUEST_CODE
                )
            }
        } else if (appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            /*val permissionString = Array<String>(1){""}
            permissionString[0] = android.Manifest.permission.PACKAGE_USAGE_STATS
            ActivityCompat.requestPermissions(this, permissionString, MY_USAGE_REQUEST_CODE)*/
            startActivityForResult(
                Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                MY_USAGE_REQUEST_CODE
            )
        } else {
            val usageStatsTextView = findViewById<TextView>(R.id.usageStats_TextView1)
            var textInFile: String

            //Read usage stats from disk
            try {
                val path = getExternalFilesDir("UsageDir")
                val file = File(path, "appUsageData.csv")
                val fileInputStream = FileInputStream(file)
                textInFile = fileInputStream.readBytes().toString(Charset.defaultCharset())
                fileInputStream.close()
            }
            catch (e: Exception){
                textInFile = e.toString()
            }
            usageStatsTextView.text = textInFile
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
                return
            }

            Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}