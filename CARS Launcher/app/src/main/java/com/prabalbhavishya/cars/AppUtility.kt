package com.prabalbhavishya.cars

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.ShortcutInfo
import android.net.Uri
import android.os.Build
import android.os.Process
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.util.*

//Batch operation friendly class
class AppUtility(val context: Context) {

    fun uninstall(packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:$packageName")
        context.startActivity(intent)
    }

    fun getAppInfo(packageName: String) {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        context.startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun getAppShortcuts(packageName: String): MutableList<ShortcutInfo>? {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val shortcutQuery = LauncherApps.ShortcutQuery()
        shortcutQuery.setQueryFlags(LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC or LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST or LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED)
        shortcutQuery.setPackage(packageName)

        var shortcuts: MutableList<ShortcutInfo>?
        try {
            shortcuts = launcherApps.getShortcuts(shortcutQuery, Process.myUserHandle())
        } catch (e: Exception) {
            shortcuts = Collections.emptyList<ShortcutInfo>()
        }

        return shortcuts
    }
}