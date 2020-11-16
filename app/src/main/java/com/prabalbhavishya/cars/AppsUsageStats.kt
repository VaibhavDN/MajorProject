@file:Suppress("DEPRECATION")

package com.prabalbhavishya.cars

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateUtils
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
            val usageStatsManager =
                getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            Log.d("StartTime: ", (System.currentTimeMillis() - 1000 * 60 * 15).toString())
            Log.d("End Time: ", (System.currentTimeMillis() + 10000).toString())
            val usageEvents: UsageEvents = usageStatsManager.queryEvents(
                System.currentTimeMillis() - 1000 * 60 * 15,
                System.currentTimeMillis() + 10000
            )

            val fetchList = FetchAppsList()
            val myAppsList: ArrayList<AppObject> = fetchList.fetchList(this)
            Toast.makeText(applicationContext, myAppsList.size.toString(), Toast.LENGTH_SHORT)
                .show()
            val usageStatsTextView = findViewById<TextView>(R.id.usageStats_TextView1)
            val hashMap: HashMap<String, EventData> = HashMap()
            val allEvents: ArrayList<UsageEvents.Event> = ArrayList()

            //Get all events where an app was resumed or paused
            while(usageEvents.hasNextEvent()) {
                val currentEvent = UsageEvents.Event()
                usageEvents.getNextEvent(currentEvent)
                if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED || currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                    allEvents.add(currentEvent)
                    val packageName = currentEvent.packageName
                    val eventData = EventData()
                    hashMap[packageName] = eventData
                }
            }

            /*
            A two element sliding window, scanning for consecutive app resume and pause activities
            Consecutive resume and pause means the app was in foreground and we can get the duration
            using the event time stamps
            */
            var textToSave = ""
            for (itr in 0 until allEvents.size-1 step 1){
                val event1: UsageEvents.Event = allEvents[itr]
                val event2: UsageEvents.Event = allEvents[itr+1]
                Log.d("Event 1: ", event1.eventType.toString())
                Log.d("Event 2: ", event2.eventType.toString())
                Log.d("Event 1 PackageName: ", event1.packageName.toString())
                Log.d("Event 2 PackageName: ", event2.packageName.toString())
                Log.d("Event 1 ClassName: ", event1.className.toString())
                Log.d("Event 2 ClassName: ", event2.className.toString())

                if(event1.eventType == UsageEvents.Event.ACTIVITY_RESUMED && event2.eventType == UsageEvents.Event.ACTIVITY_PAUSED && event1.className == event2.className){
                    val time = DateUtils.formatDateTime(this, event1.timeStamp, DateUtils.FORMAT_SHOW_TIME)
                    val date = DateUtils.formatDateTime(this, event1.timeStamp, DateUtils.FORMAT_SHOW_DATE)
                    val timeInForeground = (event2.timeStamp - event1.timeStamp)/1000

                    hashMap[event1.packageName]!!.timeInForeground.plus(timeInForeground) //!! for not null
                    hashMap[event1.packageName]!!.time = time
                    hashMap[event1.packageName]!!.date = date
                    hashMap[event1.packageName]!!.packageName = event1.packageName
                }
            }

            /*
            hashMap has info about the system apps too but we only need the info about apps shown in drawer, so
            we will use FetchAppsList functionality to get the app list and extract the info for only these apps
            */
            val fetchAppsList = FetchAppsList()
            val appList: ArrayList<AppObject> = fetchAppsList.fetchList(this)
            for (itr in 0 until appList.size step 1){
                if (hashMap[appList[itr].get_packageName()] != null){
                    val eventDataItem = hashMap[appList[itr].get_packageName()]
                    val time = eventDataItem!!.time
                    val date = eventDataItem.date
                    val timeInForeground = eventDataItem.timeInForeground
                    val packageName = eventDataItem.packageName
                    textToSave += "$date,$time,$packageName,$timeInForeground\n"
                }
            }

            //Save usage stats on disk
            val path = getExternalFilesDir("UsageDir")
            val file = File(path, "appUsageData.csv")
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(textToSave.toByteArray())
            fileOutputStream.close()

            //Read usage stats from disk
            val fileInputStream = FileInputStream(file)
            val textInFile = fileInputStream.readBytes().toString(Charset.defaultCharset())
            fileInputStream.close()

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