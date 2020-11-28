package com.prabalbhavishya.cars
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_bottomsheet.*
import java.text.SimpleDateFormat
import java.util.*
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
        googleButton.setOnClickListener(View.OnClickListener { view ->
            when (view.id) {
                R.id.material_google_btn -> GoogleSearchLaunch()
            }
        })

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
                        //bottomSheet.setBackgroundColor(Color.argb(255, 255, 255, 255))
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        //bottomSheet.setBackgroundColor(Color.argb(120, 255, 255, 255))
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
                //bottomSheet.setBackgroundColor(Color.argb((slideOffset * 255).toInt().coerceAtLeast(120), 255, 255, 255))
                appDrawer_RecyclerView.alpha = slideOffset
                drawerSearchBar.alpha = slideOffset
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
        val myFullAppList: ArrayList<AppObject> = fetchList.fetchList(context!!)
        var myAppsList: ArrayList<AppObject> = ArrayList()
        myAppsList.addAll(myFullAppList)
        val predApplist = App_Prediction().predict_app(context!!)

        val gridLayoutManager = GridLayoutManager(context!!, 5)
        val gridLayoutManager2 = GridLayoutManager(context!!, 5)
        recyclerView.layoutManager = gridLayoutManager
        //recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL))
        val recyclerViewAdapter = RecyclerViewAdapter(myAppsList)
        recyclerView.adapter = recyclerViewAdapter

        //Prediction RecyclerView
        val predictionRecyclerView =
            view.findViewById<RecyclerView>(R.id.appPrediction_RecyclerView)
        //val linearLayoutManagerPredictionRecyclerView =
        LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        predictionRecyclerView.layoutManager = gridLayoutManager2
        val predictionRecyclerViewAdapter = PredictionRecyclerViewAdapter(predApplist)
        predictionRecyclerView.adapter = predictionRecyclerViewAdapter

        val hotSeatAppsName: HashSet<String> = HashSet()

        hotSeatAppsName.add("com.android.chrome")
        hotSeatAppsName.add("com.android.contacts")
        hotSeatAppsName.add("com.google.android.youtube")
        hotSeatAppsName.add("com.google.android.gm")
        hotSeatAppsName.add("com.google.android.apps.maps")

        val hotSeatAppList: ArrayList<AppObject> = ArrayList()
        for (app in myAppsList) {
            if (hotSeatAppsName.contains(app.get_packageName())) {
                hotSeatAppList.add(app)
            }
        }
        hotSeatAppList.reverse()

        val gridLayoutManagerHotSeat = GridLayoutManager(context!!, 5)
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
            Toast.makeText(context!!, searchText, Toast.LENGTH_SHORT).show()
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