package com.prabalbhavishya.cars

import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.layout_appicon.view.*
import kotlinx.android.synthetic.main.layout_bottomsheet.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bottomSheet = findViewById<ConstraintLayout>(R.id.layoutBottomSheet)
        var bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object:BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bottomSheet.setBackgroundColor(Color.argb(255, 255, 255, 255))
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomSheet.setBackgroundColor(Color.argb(120, 255, 255, 255))
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d("BottomSheet slideOffset: ", (slideOffset).toString())
                val appIconTextView = findViewById<TextView>(R.id.appIcon_TextView)
                appIconTextView.setTextColor(Color.BLACK)
                bottomSheet.setBackgroundColor(Color.argb((slideOffset * 255).toInt().coerceAtLeast(120), 255, 255, 255))
            }
        })

        var myAppsList : ArrayList<AppObject> = fetchAppsList()

        var recyclerView = findViewById<RecyclerView>(R.id.appDrawer_RecyclerView)
        var  gridLayoutManager = GridLayoutManager(this, 5)
        recyclerView.layoutManager = gridLayoutManager
        //recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL))

        var recyclerViewAdapter = RecyclerViewAdapter(myAppsList)
        recyclerView.adapter = recyclerViewAdapter

        //Launch App usage stats activity
        val homeScreenFAB = findViewById<FloatingActionButton>(R.id.homeScreen_FAB)
        homeScreenFAB.setOnClickListener(View.OnClickListener {
            val intentUsageStats = Intent(this, UsageStats::class.java)
            startActivity(intentUsageStats)
        })
    }

    private fun fetchAppsList() : ArrayList<AppObject>{
        var intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        var untreatedAppList : List<ResolveInfo> = applicationContext.packageManager.queryIntentActivities(intent, 0)

        var finalAppsList: ArrayList<AppObject> = ArrayList()

        for(app : ResolveInfo in untreatedAppList){
            var appName : String = app.activityInfo.loadLabel(packageManager).toString()
            var packageName : String = app.activityInfo.packageName
            var appIcon : Drawable = app.activityInfo.loadIcon(packageManager)
            var appObject : AppObject = AppObject(appName, packageName, appIcon)

            if(appName == "CARS"){
                continue
            }

            finalAppsList.add(appObject)
        }
        return finalAppsList
    }

    class RecyclerViewAdapter(var myAppsList: ArrayList<AppObject>) :
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerViewAdapter.ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_appicon, parent, false)
                return ViewHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
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
                var context = itemView.context
                var appIconConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.layoutConstraint_appIcon)
                var appIconTextView: TextView = itemView.findViewById(R.id.appIcon_TextView)
                var appIconImageView: ImageView = itemView.findViewById(R.id.appIcon_ImageView)
            }
        }
}