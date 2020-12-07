package com.prabalbhavishya.cars

import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class App_Prediction {
    fun predict_app(appsFetched:ArrayList<AppObject>, hots:ArrayList<String>): ArrayList<AppObject> {
//        val rnd = Random()
//        val rndints = IntArray(10) { rnd.nextInt(appsFetched.size) }
//        val appsReturned = ArrayList<AppObject>()
//        for (i in rndints) {
//            appsReturned.add(appsFetched[i])
//        }
        val cal: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"))
        val currentLocalTime: Date = cal.getTime()
        val date: DateFormat = SimpleDateFormat("h:mm a")
        date.setTimeZone(TimeZone.getTimeZone("GMT+10:30"))

        val localTime: String = date.format(currentLocalTime)
        val arr = ArrayList<String>()
        for(i in appsFetched) {
            arr.add(i.get_packageName())
        }
        val ret = Predictor().pred(localTime, arr, hots)
        val fret = ArrayList<AppObject>()
        for(i in appsFetched) {
            if(i.get_packageName() in ret) {
                fret.add(i)
                ret.remove(i.get_packageName())
            }
        }
        return fret
    }

    fun RemoveAppList(context: Context): ArrayList<AppObject> {
        val appsFetched = FetchAppsList().fetchList(context)
        val time = Calendar.getInstance()
        time.set(Calendar.DAY_OF_MONTH, -1)
        val start = time.timeInMillis
        val end = System.currentTimeMillis()

        val us = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        var stats = us.queryUsageStats(2, start, end)
        val OldAppName = ArrayList<String>()
        val OldUserAppName = ArrayList<AppObject>()
        for (i in stats) {
            val comp: Long = 0
            if (i.totalTimeInForeground.equals(comp)) {
                OldAppName.add(i.packageName)
//                Log.println(
//                    Log.VERBOSE,
//                    "pkg name : ",
//                    i.packageName + " " + i.totalTimeInForeground
//                )
            }
        }
        for (i in appsFetched) {
            if (i.get_packageName() in OldAppName) {
                OldUserAppName.add(i)
            }
        }
        if (OldUserAppName.size > 5) {
            var i = 0
            val appsReturned = ArrayList<AppObject>()
            while (i < 5) {
                appsReturned.add(OldUserAppName[i])
                i++
            }
            return appsReturned
        }
        return OldUserAppName
    }
}