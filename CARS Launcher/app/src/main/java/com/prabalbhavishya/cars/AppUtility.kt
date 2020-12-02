package com.prabalbhavishya.cars

import android.content.Context
import android.content.Intent
import android.net.Uri

//Batch operation friendly class
class AppUtility(val context: Context) {

    fun uninstall(packageName: String){
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:$packageName")
        context.startActivity(intent)
    }

    fun getAppInfo(packageName: String){
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        context.startActivity(intent)
    }
}