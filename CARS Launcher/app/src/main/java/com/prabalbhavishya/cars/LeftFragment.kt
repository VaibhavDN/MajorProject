package com.prabalbhavishya.cars

import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_left.*
import kotlinx.android.synthetic.main.fragment_left.view.*
import me.everything.providers.android.calendar.CalendarProvider
import org.jsoup.Jsoup
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

internal class NewsDTO {
    var status: String? = null
    var totalResults = 0
    var articles: ArrayList<ArticleDTO>? = null
}

internal class ArticleDTO {
    var source: SourceDTO? = null
    var author: String? = null
    var title: String? = null
    var description: String? = null
    var url: String? = null
    var urlToImage: String? = null
    var publishedAt: String? = null
    var content: String? = null

}


internal class SourceDTO {
    var id: String? = null
    var name: String? = null
}


class LeftFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_left, container, false)

//        val intent = Intent(ACTION_USAGE_ACCESS_SETTINGS)
//        startActivity(intent)

        val usageStatsButton = view.findViewById<Button>(R.id.showUsageStatsButton)
        usageStatsButton.setOnClickListener {
            val intent = Intent(context, UsageStats::class.java)
            startActivity(intent)
        }

        val removerecview = view.findViewById<RecyclerView>(R.id.removeapps)
        var predApplist = App_Prediction().RemoveAppList(requireContext())
        var removeappadap = RemoveAppListAdapter(predApplist)
        val grid = GridLayoutManager(context, 5)
        removerecview.layoutManager = grid
        removerecview.adapter = removeappadap
        val refresh_removeapp_list = view.findViewById<ImageView>(R.id.refresh_removeapplist)
        refresh_removeapp_list.setOnClickListener {
            predApplist = App_Prediction().RemoveAppList(requireContext())
            removeappadap = RemoveAppListAdapter(predApplist)
            removerecview.adapter = removeappadap
        }

        val Tdb = TinyDB(context)


        val handle = Handler()
        var runn2 = Runnable { kotlin.run {  } }
        runn2 = Runnable {
            kotlin.run {
                handle.postDelayed(runn2, 120 * 1000)
                var ans = 0.0
                val Tdb = TinyDB(context)
                val arr1 = Tdb.getListDouble("batt")
                val arr2 = Tdb.getListLong("battdiff")
                val bm = context?.getSystemService(BATTERY_SERVICE) as BatteryManager
                val batman:Int = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

                arr1.add(0, batman.toDouble())
                arr2.add(0, System.currentTimeMillis())

                if(arr1.size > 30){
                    arr1.removeAt(arr1.size - 1)
                    arr2.removeAt(arr1.size - 1)
                }
                Tdb.putListDouble("batt", arr1)
                Tdb.putListLong("battdiff", arr2)

                if(arr1.size < 2){
                    tx1.text = "Battery will last for " + batman +" hrs"
                    Tdb.putFloat("life", batman.toFloat())
                }
                else{
                    var diff = (arr2[0] - arr2[arr2.size - 2]).toInt()
                    var bdiff = arr1[arr1.size - 2] - arr1[0]
                    println(arr2[0])
                    println(arr2[arr2.size - 1])
                    for(i in 0 until arr1.size - 1){
                        var d = (arr1[i + 1] - arr1[i])/(((arr2[i] - arr2[i + 1])/1000).toDouble()/3600)
                        if(d > 0) {
                            ans += d * ((arr2[i] - arr2[i + 1]).toDouble() / diff)
                        }
                        println(arr1[i].toString() + " " + arr2[i].toString())

                    }
                    //ans /= arr1.size
                    ans = (bdiff / ((diff/1000).toDouble()/3600))
                    println(diff)

                    if(ans > 0){
                        tx1.text = "Battery will last for " + ((batman/ans)).toInt().toString() + " hrs"
                        Tdb.putFloat("life", (batman/ans).toFloat())
                    }
                    else{
                        tx1.text = "Battery will last for " + batman +" hrs"
                        Tdb.putFloat("life", batman.toFloat())
                    }
                }

                val calendarProvider = CalendarProvider(context)
                val events = calendarProvider.getInstances(System.currentTimeMillis(), System.currentTimeMillis() + 86400 * 2000).list
                Log.d("check 1", events.size.toString() + "f")
                var i = 0
                for (model in events) {
                    if (calendarProvider.getEvent(model.eventId).allDay == false) {
                        i++
                        println(calendarProvider.getEvent(model.eventId).displayName + "," + convert(model.begin))
                    }
                }
                val sz = i
                //val array = arrayOfNulls<pojo>(sz)
                var array = ArrayList<pojo>()
                i = 0
                println(array.size)
                for (model in events) {
                    if (!calendarProvider.getEvent(model.eventId).allDay) {
                        //array[i]!!.dName=(calendarProvider.getEvent(model.eventId).displayName)
                        array.add(pojo(calendarProvider.getEvent(model.eventId).displayName,convert(model.begin),
                            (Tdb.getFloat("life") * 3600000).toLong(), model.begin))
                        //array[i]!!.date = convert(model.begin)
                        i++
                    }
//            if(array[i]==null){
//                println(i.toString()+"YES")
//            }
                }
                val recyclerView = view?.findViewById<RecyclerView>(R.id.recylerview)
                recyclerView?.layoutManager = LinearLayoutManager(context)
                recyclerView?.adapter = adap(array)


            }
        }
        handle.post(runn2)

        if(Tdb.getListString("NT").isEmpty() || (System.currentTimeMillis() - Tdb.getLong("time")) > 3600000){

        }
        else{
            Log.println(Log.ASSERT, "title", Tdb.getListString("NT").toString())
            var arr1 = Tdb.getListString("NT")
            var arr2 = Tdb.getListString("ND")
            var arr4 = Tdb.getListString("NU")
            var arr3 = ArrayList<NewsObject>()

            for(i in 0..arr1.size - 1){
                arr3.add(NewsObject(arr1[i], arr2[i], arr4[i]))
            }

            val newsview = view.findViewById<RecyclerView>(R.id.newsview)
            val gridLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            newsview.layoutManager = gridLayout
            newsview.adapter = NewsListAdapter(arr3)

        }

        return view
    }

    fun convert(milli: Long): String {

        // Creating date format
        val simple: DateFormat = SimpleDateFormat("dd/MM/yyyy h:mm a")

        // Creating date from milliseconds
        // using Date() constructor
        val result = Date(milli)

        // Formatting Date according to the
        // given format
        return simple.format(result)
    }

    class RemoveAppListAdapter(private var RemoveApplist: ArrayList<AppObject>) :
            RecyclerView.Adapter<RemoveAppListAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_predicted_appicon, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return RemoveApplist.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.appIconImageView.setImageDrawable(RemoveApplist[position].get_appIcon())

            holder.appIconConstraintLayout.setOnClickListener {
                val appUtility = AppUtility(holder.context)
                val popup = PopupMenu(holder.context, holder.appIconConstraintLayout)
                val menuDrawerAppPopup = R.menu.menu_drawer_app_popup
                popup.menuInflater.inflate(menuDrawerAppPopup, popup.menu)

                val packageName = RemoveApplist[position].get_packageName()

                popup.menu.add(0, 0, 0, "Launch")
                popup.setOnMenuItemClickListener {
                    when (it.title) {
                        "App Info" -> appUtility.getAppInfo(packageName)
                        "Uninstall" -> {
                            appUtility.uninstall(packageName)
                            RemoveApplist.removeAt(position)
                            notifyItemRemoved(position)
                            notifyDataSetChanged()
                        }
                        "Launch" -> {
                            val appLaunchIntent = holder.context.packageManager.getLaunchIntentForPackage(packageName)
                            if (appLaunchIntent != null) {
                                holder.context.startActivity(appLaunchIntent)
                            }
                        }
                        else -> Toast.makeText(holder.context, it.title, Toast.LENGTH_SHORT).show()
                    }

                    return@setOnMenuItemClickListener true
                }
                popup.show()

                //val intent = Intent(Intent.ACTION_DELETE)
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //intent.data = Uri.parse("package:" + RemoveApplist[position].get_packageName())
                //holder.context.startActivity(intent)

            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val context: Context = itemView.context
            var appIconConstraintLayout: ConstraintLayout =
                    itemView.findViewById(R.id.layoutConstraint_appIcon)
            var appIconImageView: ImageView = itemView.findViewById(R.id.appIcon_ImageView)
        }
    }

    override fun onResume() {
        super.onResume()
        val Tdb = TinyDB(context)

        val calendarProvider = CalendarProvider(context)
        val events = calendarProvider.getInstances(System.currentTimeMillis(), System.currentTimeMillis() + 86400 * 2000).list
        Log.d("check 1", events.size.toString() + "f")
        var i = 0
        for (model in events) {
            if (calendarProvider.getEvent(model.eventId).allDay == false) {
                i++
                println(calendarProvider.getEvent(model.eventId).displayName + "," + convert(model.begin))
            }
        }
        val sz = i
        //val array = arrayOfNulls<pojo>(sz)
        var array = ArrayList<pojo>()
        i = 0
        println(array.size)
        for (model in events) {
            if (!calendarProvider.getEvent(model.eventId).allDay) {
                //array[i]!!.dName=(calendarProvider.getEvent(model.eventId).displayName)
                array.add(pojo(calendarProvider.getEvent(model.eventId).displayName,convert(model.begin),
                    (Tdb.getFloat("life") * 3600000).toLong(), model.begin))
                //array[i]!!.date = convert(model.begin)
                i++
            }
//            if(array[i]==null){
//                println(i.toString()+"YES")
//            }
        }
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recylerview)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adap(array)





//        val cal = CalendarProvider(context)
//        val inst = cal.getInstances(System.currentTimeMillis(), System.currentTimeMillis() + 186400000)
//        for(i in inst.list) {
//            val eve = cal.getEvent(i.eventId)
//            Log.println(Log.ASSERT, "Events", (i.begin -System.currentTimeMillis()).toString() + " " + eve.displayName)
//        }

        recyclerView?.invalidate()


        var tdb = TinyDB(context)
        savebtn.setOnClickListener {

            Log.println(Log.ASSERT, "save", "thain thain")
            var arr = tdb.getListString("qs")
            var arrd = tdb.getListString("qd")
            var sdf = SimpleDateFormat("MMMM dd yyyy k:mm")
            arr.add(0, edt1.text.toString())
            arrd.add(0, sdf.format(Date()).toString())
            tdb.putListString("qs", arr)
            tdb.putListString("qd", arrd)
            Toast.makeText(context, "Note Saved", Toast.LENGTH_SHORT).show()
        }

        reviewbtn.setOnClickListener {
            Log.println(Log.ASSERT, "review", tdb.getListString("qs").toString() + " " + tdb.getListString("qd").toString())
            var intent = Intent(context, QuicknoteActivity::class.java)
            startActivity(intent)
        }


        val handler = Handler(Looper.getMainLooper())
        var runn = Runnable {}
        runn = Runnable {
                kotlin.run {
                    Thread {
                        var url = "https://newsapi.org/v2/top-headlines?country=in&apiKey=a1f1534aaf824034a963f6ef94eb3157"
                        try {
                            val doc = Jsoup.connect(url).ignoreContentType(true)
                            //Log.println(Log.ASSERT, "Open", doc.execute().body().toString())
                            val htmlJson = Gson()
                            val mp = htmlJson.fromJson(doc.execute().body().toString(), NewsDTO().javaClass)

                            var NewsTitle = ArrayList<String>()
                            var NewsDesc= ArrayList<String>()
                            var NewsUrl = ArrayList<String>()


                            for(i in mp.articles!!) {

                                NewsTitle.add(i.title.toString())
                                NewsDesc.add(i.urlToImage.toString())
                                NewsUrl.add(i.url.toString())

                                tdb.putListString("NT", NewsTitle)
                                tdb.putListString("ND", NewsDesc)
                                tdb.putListString("NU", NewsUrl)

                                tdb.putLong("time", System.currentTimeMillis())

                                Log.println(Log.ASSERT, "url", tdb.getListString("NU").toString())

                            }

                        } catch (e: Exception) {
                            Log.println(Log.ASSERT, "title", "nahi chala")
                        }

                    }.start()
                    var arr1 = Tdb.getListString("NT")
                    var arr2 = Tdb.getListString("ND")
                    var arr4 = Tdb.getListString("NU")
                    var arr3 = ArrayList<NewsObject>()

                    for(i in 0..arr1.size - 1){
                        arr3.add(NewsObject(arr1[i], arr2[i],  arr4[i]))
                    }

                    val newsview = view?.findViewById<RecyclerView>(R.id.newsview)
                    val gridLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    newsview?.layoutManager = gridLayout
                    newsview?.adapter = NewsListAdapter(arr3)
                    newscard.visibility = View.VISIBLE
                }

        }

        if(tdb.getListString("NT").isEmpty() || (System.currentTimeMillis() - tdb.getLong("time")) > 360000){
            newscard.visibility = View.GONE
            handler.post(runn)
        }
        else{
            Log.println(Log.ASSERT, "title", tdb.getListString("NT").toString())
        }




    }


}