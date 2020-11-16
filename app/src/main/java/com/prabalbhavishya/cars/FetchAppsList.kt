package com.prabalbhavishya.cars
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable

class FetchAppsList {
    fun fetchList(context: Context) : ArrayList<AppObject>{
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val untreatedAppList : List<ResolveInfo> = context.packageManager.queryIntentActivities(intent, 0)

        val finalAppsList: ArrayList<AppObject> = ArrayList()
        val finalAppsSet: HashSet<AppObject> = HashSet()

        for(app : ResolveInfo in untreatedAppList){
            val appName : String = app.activityInfo.loadLabel(context.packageManager).toString()
            val packageName : String = app.activityInfo.packageName
            val appIcon : Drawable = app.activityInfo.loadIcon(context.packageManager)
            val appObject = AppObject(appName, packageName, appIcon)

            if(appName == "CARS"){
                continue
            }

            finalAppsList.add(appObject)
        }
        return finalAppsList
    }
}