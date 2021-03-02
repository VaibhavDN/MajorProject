package com.prabalbhavishya.cars

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Predictor {
    fun pred(slot: String, applist: ArrayList<String>, hots: ArrayList<String>, context: Context):ArrayList<String> {
        val ret =  ArrayList<AppObject>()
        var map = mutableMapOf<String, Int>()
        var map2 = mutableMapOf<String, Int>()
        var cst = ""
        try {
            csvReader().open("/sdcard/Android/data/com.prabalbhavishya.cars/files/UsageDir/appUsageData.csv") {
                readAllAsSequence().forEach { row ->
                    //Do something
                    cst += row[0] + "," + row[1] + "," + row[2] + "," + row[3] + "\n"
                    if (row[1].length != 0) {
                        if (map[row[2]] == null) {
                            map.put(row[2], 0)
                        }
                        if (map2[row[2]] == null) {
                            map2.put(row[2], row[3].toInt())
                        }
                        //Log.println(Log.ASSERT, "Time", row[1].substring(row[1].length - 2)) //[a, b, c]
                        //Log.println(Log.ASSERT, "LTime", localTime) //[a, b, c]
                        val r1 = row[1].split(":")
                        val r2 = slot.split(":")
                        //Log.println(Log.ASSERT, "first", slot.substring(slot.length - 2) + " " + row[1].substring(row[1].length - 2))
                        if (slot.substring(slot.length - 2)
                                .equals(row[1].substring(row[1].length - 2)) && r1[0].equals(r2[0])
                        ) {
                            map[row[2]] = map.getOrDefault(row[2], 0) + 1
                            //Log.println(Log.ASSERT, "rec", "yes")
                        }
                    }
                }
            }
        }
        catch (e: Exception){
            Log.d("Exception", e.toString())
        }
//        Log.println(Log.ASSERT, "csv", cst)
        var sdf = SimpleDateFormat("MMMM dd")
        val currentDate = sdf.format(Date())
        sdf = SimpleDateFormat("k")
        var currtime = sdf.format(Date())
        var min = SimpleDateFormat("m").format(Date()).toInt()

        if(min>= 0 && min < 15)
        min = 0
        else if(min>= 15 && min < 30)
        min = 15
        else if(min >= 30 && min< 45)
        min = 30
        else if(min < 60)
        min = 45
        else
        min = 0

        currtime += min.toString()

        val tdb = TinyDB(context)


        var queue = Volley.newRequestQueue(context)
        var url = "http://192.168.43.48:8080/classify/"
        var response = "nahi chala"
        val postRequest: StringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String?> { response -> // response
                val res = JSONObject(response)
                Log.d("Response", res.get("0").toString())
                var map: Map<String, ArrayList<String>> = HashMap()
                map = Gson().fromJson(res.toString(), map.javaClass)

                for(i in map){
                    tdb.putListString(i.key, i.value)
//                    Log.println(Log.ASSERT, "currt", currtime + " " + i.key)
                }


//                if(tdb.getListString(currtime.toString()).isNotEmpty()){
//                    Log.println(Log.WARN,"hibi", "chal gaya antim mein")
//                }

            },
            Response.ErrorListener { // error
                Log.d("Error.Response", response)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["csv"] = cst
                params["Day"] = currentDate
                return params
            }
        }
        queue.add(postRequest)

        if(tdb.getListString(currtime).isNotEmpty()){
            Log.println(Log.ASSERT,"hibi", "chal gaya antim mein")
            return tdb.getListString(currtime)
        }




        val result = map.toList().sortedBy { (_, value) -> value}.toMap()
        val result2 = map2.toList().sortedBy { (_, value) -> value}.toMap()
        for(i in result) {
            //Log.println(Log.ASSERT, "Freq", i.key + " " + i.value.toString())
        }
        for(i in result2) {
            //Log.println(Log.ASSERT, "Dur", i.key + " " + i.value.toString())
        }
        val lst = Stack<String>()
        val lst2 = Stack<String>()
        for(i in result.keys) {
            lst.add(i)
        }
        for(i in result2.keys) {
            lst2.add(i)
        }
        var prt1 = ArrayList<String>()
        while(prt1.size < 5 && !lst.empty()) {
            val packagename = lst.pop()
            //Log.println(Log.ASSERT, "LTime", packagename + " in slot ") //[a, b, c]
            if(packagename in applist && !(packagename in hots)) {
                prt1.add(packagename)
            }
        }
        while(prt1.size < 10 && !lst2.empty()) {
            val packagename = lst2.pop()
            //Log.println(Log.ASSERT, "LDur", packagename + " in slot ") //[a, b, c]
            if(packagename in applist && !(packagename in prt1) && !(packagename in hots)) {
                prt1.add(packagename)
            }
        }
        return prt1
    }
}