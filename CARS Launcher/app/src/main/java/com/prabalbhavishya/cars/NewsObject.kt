package com.prabalbhavishya.cars

import android.graphics.drawable.Drawable

class NewsObject(newAppName: String, newAppUrl: String, url: String) {
    private var title: String = newAppName
    private var image: String = newAppUrl
    private  var Url: String = url

    fun get_articleName(): String {
        return this.title
    }

    fun get_image(): String {
        return this.image
    }

    fun get_url(): String {
        return this.Url
    }
}