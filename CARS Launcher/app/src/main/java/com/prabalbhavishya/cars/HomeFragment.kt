package com.prabalbhavishya.cars

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ShortcutInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.system.Os
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_bottomsheet.*
import org.jsoup.Jsoup
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val googleButton = view.findViewById<MaterialCardView>(R.id.material_google_btn)
        googleButton.setOnClickListener { view ->
            when (view.id) {
                R.id.material_google_btn -> GoogleSearchLaunch()
            }
        }

        val textw = view.findViewById<TextView>(R.id.textweather)
        var tempString = "--"
        var timeInterval: Long = 1

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val handler = Handler(Looper.getMainLooper())
        var runn = Runnable {}
        runn = Runnable {
            kotlin.run {
                Thread {
                    kotlin.run {
                        handler.postDelayed(runn, timeInterval * 1000)

                        if (!prefs.getString("City Name", "c").toString().equals("c")) {
                            val url =
                                "https://api.openweathermap.org/data/2.5/weather?q=" + prefs.getString(
                                    "City Name",
                                    "c"
                                )
                                    .toString() + "&appid=58f0c9e655cd9d832a1a6d2863f2d2f3&units=metric"


                            try {
                                val doc = Jsoup.connect(url).ignoreContentType(true)
                                //Log.println(Log.ASSERT, "Open", doc.execute().body().toString())
                                val htmlJson = Gson()
                                val mp: MutableMap<String, Any> = htmlJson.fromJson(
                                    doc.execute().body().toString(),
                                    object : TypeToken<MutableMap<String, Any>>() {}.type
                                )
                                mp["weather"] = mp["weather"].toString().removePrefix("[")
                                mp["weather"] = mp["weather"].toString().removeSuffix("]")
                                val weather: Map<String, Any> = htmlJson.fromJson(
                                    mp["weather"].toString(),
                                    object : TypeToken<Map<String, Any>>() {}.type
                                )
                                val temp: Map<String, Any> = htmlJson.fromJson(
                                    mp["main"].toString(),
                                    object : TypeToken<Map<String, Any>>() {}.type
                                )
                                //Log.println(Log.ASSERT, "Preferences work", weather["main"].toString() + " " + temp["temp"].toString())
                                tempString =
                                    weather["main"].toString() + " | " + temp["temp"].toString() + "°C"
                            } catch (e: Exception) {

                            }
                            timeInterval = 100

                        }
                    }

                }.start()
                textw.text = tempString
            }
        }
        handler.postDelayed(runn, 1 * 1000)

        var setblur = 1

        val bottomSheet = view.findViewById<ConstraintLayout>(R.id.layoutBottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val recyclerView = view.findViewById<RecyclerView>(R.id.appDrawer_RecyclerView)
        val recyclerViewHotSeat = view.findViewById<RecyclerView>(R.id.hotSeat_RecyclerView)
        val drawerSearchBar = view.findViewById<LinearLayout>(R.id.drawerSearch_LinearLayout)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        //bottomSheet.setBackgroundColor(Color.argb(200, 0, 0, 0))
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        //bottomSheet.setBackgroundColor(Color.argb(120, 0, 0, 0))
                        setblur = 1
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        //Skip
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        //Skip
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        //Skip
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        //Skip
                    }
                }
                //val periodicWorkRequest = OneTimeWorkRequestBuilder<GetAppUsageWorker>().build()
                //WorkManager.getInstance(applicationContext).enqueue(periodicWorkRequest)
                val periodicWorkRequest =
                    PeriodicWorkRequestBuilder<GetAppUsageWorker>(15, TimeUnit.MINUTES).build()
                val workTag = "UsageWork"
                WorkManager.getInstance(context!!).enqueueUniquePeriodicWork(
                    workTag,
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicWorkRequest
                )
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //Log.d("BottomSheet slideOffset: ", (slideOffset).toString())
                bottomSheet.setBackgroundColor(Color.argb((slideOffset * 200).toInt().coerceAtLeast(120), 0, 0, 0))
//                if (setblur == 1) {
//                    val wallp = WallpaperManager.getInstance(context)
//                    var wbmp = wallp.drawable.toBitmap()
//                    wbmp =
//                        Bitmap.createScaledBitmap(wbmp, wbmp.height / 512, wbmp.width / 512, true)
//                    bottomSheet.background = BitmapDrawable(resources, wbmp)
//                    setblur = 0
//                }
                appDrawer_RecyclerView.alpha = slideOffset
                drawerSearchBar.alpha = slideOffset
                bottomSheet.background.alpha = (slideOffset * 255).toInt()
                hotSeat_LinearView.alpha = 1 - slideOffset
                homeScreen_LinearLayout1.alpha = 1 - slideOffset
                homeScreen_LinearLayout2.alpha = 1 - slideOffset
                homeScreen_LinearLayout3.alpha = 1 - slideOffset

                if (slideOffset > 0.95) {
                    hotSeat_LinearView.visibility = View.GONE
                } else {
                    hotSeat_LinearView.visibility = View.VISIBLE
                }

                if (slideOffset < 0.05) {
                    appDrawer_RecyclerView.visibility = View.GONE
                    drawerSearchBar.visibility = View.GONE
                } else {
                    appDrawer_RecyclerView.visibility = View.VISIBLE
                    drawerSearchBar.visibility = View.VISIBLE
                }
            }
        })

        val fetchList = FetchAppsList()
        val myFullAppList: ArrayList<AppObject> = fetchList.fetchList(requireContext())
        val myAppsList: ArrayList<AppObject> = ArrayList()
        myAppsList.addAll(myFullAppList)
        val hotSeatAppsName: HashSet<String> = HashSet()
        val hots = ArrayList<String>()

        hotSeatAppsName.add("com.android.chrome")
        hotSeatAppsName.add("com.android.contacts")
        hotSeatAppsName.add("com.google.android.youtube")
        hotSeatAppsName.add("com.google.android.gm")
        hotSeatAppsName.add("com.google.android.apps.maps")

        hots.add("com.android.chrome")
        hots.add("com.android.contacts")
        hots.add("com.google.android.youtube")
        hots.add("com.google.android.gm")
        hots.add("com.google.android.apps.maps")
        val predApplist = context?.let { App_Prediction().predict_app(myFullAppList, hots, it) }

        val gridLayoutManager = GridLayoutManager(requireContext(), 4)
        val gridLayoutManager2 = GridLayoutManager(requireContext(), 5)
        recyclerView.layoutManager = gridLayoutManager
        //recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL))
        val recyclerViewAdapter = RecyclerViewAdapter(myAppsList)
        recyclerView.adapter = recyclerViewAdapter

        //Prediction RecyclerView
        val predictionRecyclerView =
            view.findViewById<RecyclerView>(R.id.appPrediction_RecyclerView)
        //val linearLayoutManagerPredictionRecyclerView =
        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        predictionRecyclerView.layoutManager = gridLayoutManager2
        val predictionRecyclerViewAdapter = predApplist?.let { PredictionRecyclerViewAdapter(it) }
        predictionRecyclerView.adapter = predictionRecyclerViewAdapter



        val hotSeatAppList: ArrayList<AppObject> = ArrayList()
        for (app in myAppsList) {
            if (hotSeatAppsName.contains(app.get_packageName())) {
                hotSeatAppList.add(app)
            }
        }
        hotSeatAppList.reverse()

        val gridLayoutManagerHotSeat = GridLayoutManager(requireContext(), 5)
        recyclerViewHotSeat.layoutManager = gridLayoutManagerHotSeat
        recyclerViewHotSeat.isNestedScrollingEnabled = false
        //recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL))
        val recyclerViewAdapterHotSeat = HotSeatRecyclerViewAdapter(hotSeatAppList)
        recyclerViewHotSeat.adapter = recyclerViewAdapterHotSeat

        //Launch App usage stats activity
//        val homeScreenFAB = findViewById<FloatingActionButton>(R.id.homeScreen_FAB)
//        homeScreenFAB.setOnClickListener {
//            val intentUsageStats = Intent(this, UsageStats::class.java)
//            startActivity(intentUsageStats)
//        }

        val drawerSearchEditText = view.findViewById<EditText>(R.id.drawerSearch_EditText)
        drawerSearchEditText.addTextChangedListener {
            val searchText = drawerSearchEditText.text.toString().trim()
            //Toast.makeText(requireContext(), searchText, Toast.LENGTH_SHORT).show()
            Log.d("TextWatcher: ", searchText)

            if (searchText == "") {
                myAppsList.addAll(myFullAppList)
            } else {
                myAppsList.clear()
                for (app in myFullAppList) {
                    Log.d("app: ", app.get_appName())
                    if (app.get_appName().contains(searchText, true)) {
                        myAppsList.add(app)
                    }
                }
            }
            recyclerViewAdapter.notifyDataSetChanged()
        }
        return view
    }

    private fun GoogleSearchLaunch() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")))
    }

    class PredictionRecyclerViewAdapter(private var predictedAppsList: ArrayList<AppObject>) :
        RecyclerView.Adapter<PredictionRecyclerViewAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_predicted_appicon, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //var width = Resources.getSystem().displayMetrics.widthPixels
            //var height = Resources.getSystem().displayMetrics.heightPixels
            //holder.appIconImageView.layoutParams = ConstraintLayout.LayoutParams(width/50, height/50)
            holder.appIconImageView.setImageDrawable(predictedAppsList[position].get_appIcon())

            //holder.appIconTextView.text = predictedAppsList[position].get_appName()
            holder.appIconConstraintLayout.setOnClickListener {
                Toast.makeText(
                    holder.context,
                    predictedAppsList[position].get_packageName(),
                    Toast.LENGTH_SHORT
                ).show()
                val appLaunchIntent = holder.context.packageManager.getLaunchIntentForPackage(
                    predictedAppsList[position].get_packageName()
                )
                if (appLaunchIntent != null) {
                    holder.context.applicationContext.startActivity(appLaunchIntent)
                }
            }
        }

        override fun getItemCount(): Int {
            return predictedAppsList.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val context: Context = itemView.context
            var appIconConstraintLayout: ConstraintLayout =
                itemView.findViewById(R.id.layoutConstraint_appIcon)

            //var appIconTextView: TextView = itemView.findViewById(R.id.appIcon_TextView)
            var appIconImageView: ImageView = itemView.findViewById(R.id.appIcon_ImageView)
        }
    }

    class HotSeatRecyclerViewAdapter(private var myAppsList: ArrayList<AppObject>) :
        RecyclerView.Adapter<HotSeatRecyclerViewAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_predicted_appicon, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.appIconImageView.setImageDrawable(myAppsList[position].get_appIcon())

            //holder.appIconTextView.text = myAppsList[position].get_appName()
            holder.appIconConstraintLayout.setOnClickListener {
                Toast.makeText(
                    holder.context,
                    myAppsList[position].get_packageName(),
                    Toast.LENGTH_SHORT
                ).show()
                val appLaunchIntent = holder.context.packageManager.getLaunchIntentForPackage(
                    myAppsList[position].get_packageName()
                )
                if (appLaunchIntent != null) {
                    holder.context.applicationContext.startActivity(appLaunchIntent)
                }
            }
        }

        override fun getItemCount(): Int {
            return myAppsList.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val context: Context = itemView.context
            var appIconConstraintLayout: ConstraintLayout =
                itemView.findViewById(R.id.layoutConstraint_appIcon)

            //var appIconTextView: TextView = itemView.findViewById(R.id.appIcon_TextView)
            var appIconImageView: ImageView = itemView.findViewById(R.id.appIcon_ImageView)
        }
    }

    class RecyclerViewAdapter(private var myAppsList: ArrayList<AppObject>) :
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.layout_appicon,
                parent,
                false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //var width = Resources.getSystem().displayMetrics.widthPixels
            //var height = Resources.getSystem().displayMetrics.heightPixels
            //holder.appIconImageView.layoutParams = ConstraintLayout.LayoutParams(width/50, height/50)
            holder.appIconImageView.setImageDrawable(myAppsList[position].get_appIcon())

            holder.appIconTextView.text = myAppsList[position].get_appName()
            holder.appIconConstraintLayout.setOnClickListener {
                Toast.makeText(
                    holder.context,
                    myAppsList[position].get_packageName(),
                    Toast.LENGTH_SHORT
                ).show()
                val appLaunchIntent = holder.context.packageManager.getLaunchIntentForPackage(
                    myAppsList[position].get_packageName()
                )
                if (appLaunchIntent != null) {
                    holder.context.applicationContext.startActivity(appLaunchIntent)
                }
            }

            holder.appIconConstraintLayout.setOnLongClickListener {
                //Toast.makeText(holder.context, "Long press", Toast.LENGTH_SHORT).show()
                val appUtility = AppUtility(holder.context)
                val popup = PopupMenu(holder.context, holder.appIconConstraintLayout)
                val menuDrawerAppPopup = R.menu.menu_drawer_app_popup
                popup.menuInflater.inflate(menuDrawerAppPopup, popup.menu)

                val packageName = myAppsList[position].get_packageName()
                var shortcuts: MutableList<ShortcutInfo>? = ArrayList()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    shortcuts = appUtility.getAppShortcuts(packageName)
                    if (shortcuts!!.size > 0) {
                        for (shortcut in shortcuts)
                            popup.menu.add(shortcut.shortLabel.toString())
                    }
                }
                popup.setOnMenuItemClickListener {
                    when (it.title) {
                        "App Info" -> appUtility.getAppInfo(packageName)
                        "Uninstall" -> appUtility.uninstall(packageName)
                        else -> Toast.makeText(holder.context, it.title, Toast.LENGTH_SHORT).show()
                    }

                    return@setOnMenuItemClickListener true
                }
                popup.show()
                return@setOnLongClickListener true
            }
        }

        override fun getItemCount(): Int {
            return myAppsList.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val context: Context = itemView.context
            var appIconConstraintLayout: ConstraintLayout =
                itemView.findViewById(R.id.layoutConstraint_appIcon)
            var appIconTextView: TextView = itemView.findViewById(R.id.appIcon_TextView)
            var appIconImageView: ImageView = itemView.findViewById(R.id.appIcon_ImageView)
        }
    }
}