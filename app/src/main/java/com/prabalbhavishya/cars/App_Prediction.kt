package com.prabalbhavishya.cars

import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class App_Prediction {
    fun predict_app(context: Context): ArrayList<AppObject> {
        val appsFetched = FetchAppsList().fetchList(context)
        val rnd = Random()
        val rndints = IntArray(10) { rnd.nextInt(appsFetched.size) }
        val appsReturned = ArrayList<AppObject>()
        for (i in rndints) {
            appsReturned.add(appsFetched[i])
        }
        return appsReturned
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
                Log.println(
                    Log.VERBOSE,
                    "pkg name : ",
                    i.packageName + " " + i.totalTimeInForeground
                )
            }
        }
        for (i in appsFetched) {
            if (i.get_packageName() in OldAppName) {
                OldUserAppName.add(i)
            }
        }
        if (OldUserAppName.size > 5) {
            val rnd = Random()
            val rndints = IntArray(5) { rnd.nextInt(OldUserAppName.size) }
            val appsReturned = ArrayList<AppObject>()
            for (i in rndints) {
                appsReturned.add(appsFetched[i])
            }
            return appsReturned
        }

        return OldUserAppName
    }
}