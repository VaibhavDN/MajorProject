package com.prabalbhavishya.cars

import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
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
        if(Tdb.getListDouble("battdata").size == 0) {
            val bm = context?.getSystemService(BATTERY_SERVICE) as BatteryManager
            val batman:Int = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            //Log.println(Log.ASSERT, "perc", batman.toString())
            val battdata = ArrayList<Double>()
            val batdiffdata = ArrayList<Double>()
            for(i in 0..14) {
                battdata.add(batman.toDouble())
            }
            for(i in 0..14) {
                batdiffdata.add(0.toDouble())
            }
            Tdb.putListDouble("battdata", battdata)
            Tdb.putListDouble("battdiffdata", batdiffdata)
        }

        val handle = Handler()
        var runn2 = Runnable { kotlin.run {  } }
        runn2 = Runnable {
            kotlin.run {
                handle.postDelayed(runn2, 480 * 1000)
                val Tdb = TinyDB(context)
                val arr1 = Tdb.getListDouble("battdata")
                val arr2 = Tdb.getListDouble("battdiffdata")
                val bm = context?.getSystemService(BATTERY_SERVICE) as BatteryManager
                val batman:Int = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                arr1.removeAt(14)
                arr2.removeAt(14)
                arr1.add(0, batman.toDouble())
                arr2.add(0, ((arr1[0] - arr1[1])/8).toDouble())
                Tdb.putListDouble("battdata", arr1)
                Tdb.putListDouble("battdiffdata", arr2)

                if((arr1[0] - arr1[1])/8 > 0) {
                    view.findViewById<TextView>(R.id.txt2).setTextColor(Color.parseColor("#7fff00"))
                    view.findViewById<TextView>(R.id.txt2).text = "Battery charge rate per minute : " + ((arr1[0] - arr1[1])/8).toString() + " %"
                }
                else {
                    view.findViewById<TextView>(R.id.txt2).setTextColor(Color.parseColor("#ff2500"))
                    view.findViewById<TextView>(R.id.txt2).text = "Battery drain rate per minute : " + ((arr1[0] - arr1[1])/8).toString() + " %"
                }

                val bdata = ArrayList<BarEntry>()
                for(i in 0..14) {
                    bdata.add(BarEntry(i.toFloat(), Tdb.getListDouble("battdiffdata")[i].toFloat()))
                }
                val bdtset = BarDataSet(bdata, "Drain rate")
                bdtset.setColor(Color.parseColor("#ff8c00"))
                bdtset.setValueTextColor(Color.parseColor("#f3f0ff"))
                val desc = Description()
                desc.text = ""
                bdtset.valueFormatter = DefaultValueFormatter(1)
                val finaldata= BarData(bdtset)

                val mpchart = view.findViewById<BarChart>(R.id.barchart)
                mpchart.data = finaldata
                mpchart.axisRight.textColor = Color.parseColor("#f3f0ff")
                mpchart.axisLeft.textColor = Color.parseColor("#f3f0ff")
                mpchart.description = desc
                mpchart.invalidate()


            }
        }
        handle.post(runn2)

        return view
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
        val bdata = ArrayList<BarEntry>()
        for(i in 0..14) {
            bdata.add(BarEntry(i.toFloat(), Tdb.getListDouble("battdiffdata")[i].toFloat()))
        }
        val bdtset = BarDataSet(bdata, "Drain rate")
        bdtset.setColor(Color.parseColor("#ff8c00"))
        bdtset.setValueTextColor(Color.parseColor("#f3f0ff"))
        bdtset.valueFormatter = DefaultValueFormatter(1)
        val desc = Description()
        desc.text = ""
        val finaldata= BarData(bdtset)

        val mpchart = view?.findViewById<BarChart>(R.id.barchart)
        mpchart?.data = finaldata
        mpchart?.axisRight?.textColor = Color.parseColor("#f3f0ff")
        mpchart?.axisLeft?.textColor = Color.parseColor("#f3f0ff")
        mpchart?.description = desc
        mpchart?.invalidate()

    }

}