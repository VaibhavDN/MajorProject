package com.prabalbhavishya.cars

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


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
        usageStatsButton.setOnClickListener{
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
                appUtility.uninstall(RemoveApplist[position].get_packageName())

                //val intent = Intent(Intent.ACTION_DELETE)
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //intent.data = Uri.parse("package:" + RemoveApplist[position].get_packageName())
                //holder.context.startActivity(intent)

                RemoveApplist.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val context: Context = itemView.context
            var appIconConstraintLayout: ConstraintLayout =
                itemView.findViewById(R.id.layoutConstraint_appIcon)
            var appIconImageView: ImageView = itemView.findViewById(R.id.appIcon_ImageView)
        }
    }

}