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
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UsageStats : AppCompatActivity() {
    private var MY_USAGE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_stats)

        val appOps: AppOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q) {
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
            val hashMap: HashMap<String, Long> = HashMap()
            val allEvents: ArrayList<UsageEvents.Event> = ArrayList()

            //Get all events where an app was resumed or paused
            while(usageEvents.hasNextEvent()) {
                val currentEvent = UsageEvents.Event()
                usageEvents.getNextEvent(currentEvent)
                if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED || currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                    allEvents.add(currentEvent)
                    val packageName = currentEvent.packageName
                    hashMap[packageName] = 0
                }
            }

            /*
            A two element sliding window, scanning for consecutive app resume and pause activities
            Consecutive resume and pause means the app was in foreground and we can get the duration
            using the event time stamps
            */
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
                    val timeInForeground = event2.timeStamp - event1.timeStamp
                    hashMap[event1.packageName] = hashMap[event1.packageName]!!.plus(timeInForeground/1000) //!! for not null
                }
            }

            usageStatsTextView.text = hashMap.toString()
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