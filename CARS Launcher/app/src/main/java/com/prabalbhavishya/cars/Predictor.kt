package com.prabalbhavishya.cars

import android.util.Log
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Predictor {
    fun pred(slot: String, applist: ArrayList<String>, hots: ArrayList<String>):ArrayList<String> {
        val ret =  ArrayList<AppObject>()
        var map = mutableMapOf<String, Int>()
        var map2 = mutableMapOf<String, Int>()
        csvReader().open("/sdcard/Android/data/com.prabalbhavishya.cars/files/UsageDir/appUsageData.csv") {
            readAllAsSequence().forEach { row ->
                //Do something
                if(row[1].length != 0) {
                    if(map[row[2]] == null) {
                        map.put(row[2], 0)
                    }
                    if(map2[row[2]] == null) {
                        map2.put(row[2], row[3].toInt())
                    }
                    //Log.println(Log.ASSERT, "Time", row[1].substring(row[1].length - 2)) //[a, b, c]

                    //Log.println(Log.ASSERT, "LTime", localTime) //[a, b, c]
                    val r1 = row[1].split(":")
                    val r2 = slot.split(":")
                    //Log.println(Log.ASSERT, "first", slot.substring(slot.length - 2) + " " + row[1].substring(row[1].length - 2))
                    if(slot.substring(slot.length - 2).equals(row[1].substring(row[1].length - 2)) && r1[0].equals(r2[0])) {
                        map[row[2]] = map.getOrDefault(row[2], 0) + 1
                        //Log.println(Log.ASSERT, "rec", "yes")
                    }
                }
            }

        }
        val result = map.toList().sortedBy { (_, value) -> value}.toMap()
        val result2 = map2.toList().sortedBy { (_, value) -> value}.toMap()
        val lst = Stack<String>()
        val lst2 = Stack<String>()
        for(i in result.keys) {
            lst.add(i)
        }
        for(i in result2.keys) {
            lst2.add(i)
        }
        var prt1 = ArrayList<String>()
        while(prt1.size < 5) {
            val packagename = lst.pop()
            Log.println(Log.ASSERT, "LTime", packagename + " in slot ") //[a, b, c]
            if(packagename in applist && !(packagename in hots)) {
                prt1.add(packagename)
            }
        }
        while(prt1.size < 10) {
            val packagename = lst2.pop()
            Log.println(Log.ASSERT, "LDur", packagename + " in slot ") //[a, b, c]
            if(packagename in applist && !(packagename in prt1) && !(packagename in hots)) {
                prt1.add(packagename)
            }
        }
        return prt1
    }
}