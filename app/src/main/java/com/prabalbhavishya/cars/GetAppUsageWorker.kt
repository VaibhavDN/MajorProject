package com.prabalbhavishya.cars

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File
import java.io.FileOutputStream

class GetAppUsageWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    override fun doWork(): Result {
        val usageStatsManager =
            applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        Log.d("StartTime: ", (System.currentTimeMillis() - 1000 * 60 * 15).toString())
        Log.d("End Time: ", (System.currentTimeMillis() + 10000).toString())
        val usageEvents: UsageEvents = usageStatsManager.queryEvents(
            System.currentTimeMillis() - 1000 * 60 * 15,
            System.currentTimeMillis() + 10000
        )

        val fetchList = FetchAppsList()
        val myAppsList: ArrayList<AppObject> = fetchList.fetchList(applicationContext)
        //Toast.makeText(applicationContext, myAppsList.size.toString(), Toast.LENGTH_SHORT).show()
        val hashMap: HashMap<String, UsageStats.EventData> = HashMap()
        val allEvents: ArrayList<UsageEvents.Event> = ArrayList()

        //Get all events where an app was resumed or paused
        while(usageEvents.hasNextEvent()) {
            val currentEvent = UsageEvents.Event()
            usageEvents.getNextEvent(currentEvent)
            if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED || currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                allEvents.add(currentEvent)
                val packageName = currentEvent.packageName
                val eventData = UsageStats.EventData()
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
                val time = DateUtils.formatDateTime(applicationContext, event1.timeStamp, DateUtils.FORMAT_SHOW_TIME)
                val date = DateUtils.formatDateTime(applicationContext, event1.timeStamp, DateUtils.FORMAT_SHOW_DATE)
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
        val appList: ArrayList<AppObject> = fetchAppsList.fetchList(applicationContext)
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
        val path = applicationContext.getExternalFilesDir("UsageDir")
        val file = File(path, "appUsageData.csv")
        val fileOutputStream = FileOutputStream(file, true)
        fileOutputStream.write(textToSave.toByteArray())
        fileOutputStream.close()

        //Toast.makeText(applicationContext, "Worker Ran", Toast.LENGTH_LONG).show()
        Log.d("Worker running: ", "Successful")
        return Result.success()
    }
}