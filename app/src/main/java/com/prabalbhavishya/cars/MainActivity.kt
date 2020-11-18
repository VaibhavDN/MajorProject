package com.prabalbhavishya.cars
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_bottomsheet.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomSheet = findViewById<ConstraintLayout>(R.id.layoutBottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val recyclerView = findViewById<RecyclerView>(R.id.appDrawer_RecyclerView)
        val recyclerViewHotSeat = findViewById<RecyclerView>(R.id.hotSeat_RecyclerView)
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
                WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                    workTag,
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicWorkRequest
                )
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d("BottomSheet slideOffset: ", (slideOffset).toString())
                //bottomSheet.setBackgroundColor(Color.argb((slideOffset * 255).toInt().coerceAtLeast(120), 255, 255, 255))
                appDrawer_RecyclerView.alpha = slideOffset
                hotSeat_RecyclerView.alpha = 1 - slideOffset
                homeScreen_LinearLayout2.alpha = 1 - slideOffset
                homeScreen_LinearLayout3.alpha = 1 - slideOffset

                if (slideOffset > 0.95) {
                    hotSeat_RecyclerView.visibility = View.GONE
                } else {
                    hotSeat_RecyclerView.visibility = View.VISIBLE
                }

                if (slideOffset < 0.05) {
                    appDrawer_RecyclerView.visibility = View.GONE
                } else {
                    appDrawer_RecyclerView.visibility = View.VISIBLE
                }
            }
        })

        val fetchList = FetchAppsList()
        val myAppsList: ArrayList<AppObject> = fetchList.fetchList(this)
        val predApplist = App_Prediction().predict_app(this)

        val gridLayoutManager = GridLayoutManager(this, 5)
        val gridLayoutManager2 = GridLayoutManager(this, 5)
        recyclerView.layoutManager = gridLayoutManager
        //recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL))
        val recyclerViewAdapter = RecyclerViewAdapter(myAppsList)
        recyclerView.adapter = recyclerViewAdapter

        //Prediction RecyclerView
        val predictionRecyclerView = findViewById<RecyclerView>(R.id.appPrediction_RecyclerView)
        val linearLayoutManagerPredictionRecyclerView =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        predictionRecyclerView.layoutManager = gridLayoutManager2
        val predictionRecyclerViewAdapter = PredictionRecyclerViewAdapter(predApplist)
        predictionRecyclerView.adapter = predictionRecyclerViewAdapter

        val hotSeatAppsName: HashSet<String> = HashSet()

        hotSeatAppsName.add("com.brave.browser_nightly")
        hotSeatAppsName.add("com.google.android.GoogleCamera")
        hotSeatAppsName.add("com.google.android.gm")
        hotSeatAppsName.add("com.android.messaging")
        hotSeatAppsName.add("com.android.dialer")

        val hotSeatAppList : ArrayList<AppObject> = ArrayList()
        for (app in myAppsList){
            if(hotSeatAppsName.contains(app.get_packageName())){
                hotSeatAppList.add(app)
            }
        }
        hotSeatAppList.reverse()

        val gridLayoutManagerHotSeat = GridLayoutManager(this, 5)
        recyclerViewHotSeat.layoutManager = gridLayoutManagerHotSeat
        recyclerViewHotSeat.isNestedScrollingEnabled = false
        //recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL))
        val recyclerViewAdapterHotSeat = HotSeatRecyclerViewAdapter(hotSeatAppList)
        recyclerViewHotSeat.adapter = recyclerViewAdapterHotSeat

        //Launch App usage stats activity
        val homeScreenFAB = findViewById<FloatingActionButton>(R.id.homeScreen_FAB)
        homeScreenFAB.setOnClickListener {
            val intentUsageStats = Intent(this, UsageStats::class.java)
            startActivity(intentUsageStats)
        }
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
                Toast.makeText(holder.context, predictedAppsList[position].get_packageName(), Toast.LENGTH_SHORT).show()
                val appLaunchIntent = holder.context.packageManager.getLaunchIntentForPackage(predictedAppsList[position].get_packageName())
                if(appLaunchIntent != null){
                    holder.context.applicationContext.startActivity(appLaunchIntent)
                }
            }
        }

        override fun getItemCount(): Int {
            return predictedAppsList.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val context: Context = itemView.context
            var appIconConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.layoutConstraint_appIcon)

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
                Toast.makeText(holder.context, myAppsList[position].get_packageName(), Toast.LENGTH_SHORT).show()
                val appLaunchIntent = holder.context.packageManager.getLaunchIntentForPackage(myAppsList[position].get_packageName())
                if(appLaunchIntent != null){
                    holder.context.applicationContext.startActivity(appLaunchIntent)
                }
            }
        }

        override fun getItemCount(): Int {
            return myAppsList.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val context: Context = itemView.context
            var appIconConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.layoutConstraint_appIcon)

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
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_appicon, parent, false)
                return ViewHolder(view)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                //var width = Resources.getSystem().displayMetrics.widthPixels
                //var height = Resources.getSystem().displayMetrics.heightPixels
                //holder.appIconImageView.layoutParams = ConstraintLayout.LayoutParams(width/50, height/50)
                holder.appIconImageView.setImageDrawable(myAppsList[position].get_appIcon())

                holder.appIconTextView.text = myAppsList[position].get_appName()
                holder.appIconConstraintLayout.setOnClickListener {
                    Toast.makeText(holder.context, myAppsList[position].get_packageName(), Toast.LENGTH_SHORT).show()
                    val appLaunchIntent = holder.context.packageManager.getLaunchIntentForPackage(myAppsList[position].get_packageName())
                    if(appLaunchIntent != null){
                        holder.context.applicationContext.startActivity(appLaunchIntent)
                    }
                }
            }

            override fun getItemCount(): Int {
                return myAppsList.size
            }

            class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
                val context: Context = itemView.context
                var appIconConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.layoutConstraint_appIcon)
                var appIconTextView: TextView = itemView.findViewById(R.id.appIcon_TextView)
                var appIconImageView: ImageView = itemView.findViewById(R.id.appIcon_ImageView)
            }
        }
}