package com.prabalbhavishya.cars
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.alpha
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomSheet = findViewById<ConstraintLayout>(R.id.layoutBottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object:BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bottomSheet.setBackgroundColor(Color.argb(255, 255, 255, 255))
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomSheet.setBackgroundColor(Color.argb(120, 255, 255, 255))
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
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d("BottomSheet slideOffset: ", (slideOffset).toString())
                val appIconTextView = findViewById<TextView>(R.id.appIcon_TextView)
                appIconTextView.setTextColor(Color.BLACK)
                bottomSheet.setBackgroundColor(Color.argb((slideOffset * 255).toInt().coerceAtLeast(120), 255, 255, 255))
            }
        })

        val fetchList = FetchAppsList()
        val myAppsList : ArrayList<AppObject> = fetchList.fetchList(this)

        val recyclerView = findViewById<RecyclerView>(R.id.appDrawer_RecyclerView)
        val  gridLayoutManager = GridLayoutManager(this, 5)
        recyclerView.layoutManager = gridLayoutManager
        //recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL))

        val recyclerViewAdapter = RecyclerViewAdapter(myAppsList)
        recyclerView.adapter = recyclerViewAdapter

        //Launch App usage stats activity
        val homeScreenFAB = findViewById<FloatingActionButton>(R.id.homeScreen_FAB)
        homeScreenFAB.setOnClickListener {
            val intentUsageStats = Intent(this, UsageStats::class.java)
            startActivity(intentUsageStats)
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