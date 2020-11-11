package com.prabalbhavishya.cars

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.layout_bottomsheet.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bottomSheet = findViewById<ConstraintLayout>(R.id.layoutBottomSheet)
        var bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object:BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Toast.makeText(applicationContext, "Expanded", Toast.LENGTH_SHORT).show()
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Toast.makeText(applicationContext, "Collapsed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //Do nothing
            }

        })

        var myAppsList = Array<Int>(100){0}
        for (i in 0..99 step 1){
            myAppsList[i] = i
        }

        var recyclerView = findViewById<RecyclerView>(R.id.appDrawer_RecyclerView)
        var  gridLayoutManager = GridLayoutManager(this, 5)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        var recyclerViewAdapter = RecyclerViewAdapter(myAppsList)
        recyclerView.adapter = recyclerViewAdapter
    }

    class RecyclerViewAdapter(var myAppsList: Array<Int>) :
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerViewAdapter.ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_appicon, parent, false)
                return ViewHolder(view)
            }
            override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
                holder.appIconTextView.text = myAppsList[position].toString()
            }

            override fun getItemCount(): Int {
                return myAppsList.size
            }

            class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
                var appIconTextView: TextView = itemView.findViewById(R.id.appIcon_TextView)
            }
        }
}