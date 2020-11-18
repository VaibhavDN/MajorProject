package com.prabalbhavishya.cars

import android.content.Context
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
}