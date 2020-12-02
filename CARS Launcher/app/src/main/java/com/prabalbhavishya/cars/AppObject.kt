package com.prabalbhavishya.cars

import android.graphics.drawable.Drawable

class AppObject(newAppName: String, newPackageName: String, newAppIcon: Drawable) {
    private var appName: String = newAppName
    private var packageName: String = newPackageName
    private var appIcon: Drawable = newAppIcon

    fun get_appName(): String {
        return this.appName
    }

    fun get_packageName(): String {
        return this.packageName
    }

    fun get_appIcon(): Drawable {
        return this.appIcon
    }
}