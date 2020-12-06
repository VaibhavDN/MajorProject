package com.prabalbhavishya.cars

import android.util.Log
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader


class Predictor {
    fun pred(slot:String):ArrayList<AppObject> {
        val ret =  ArrayList<AppObject>()
        csvReader().open("src/main/resources/test.csv") {
            readAllAsSequence().forEach { row ->
                //Do something
                Log.println(Log.ASSERT, "Time", row[0]) //[a, b, c]
            }
        }
        return ret
    }
}