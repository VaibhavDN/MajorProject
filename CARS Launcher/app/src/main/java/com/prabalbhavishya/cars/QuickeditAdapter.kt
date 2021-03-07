package com.prabalbhavishya.cars
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class QuickeditAdapter(private val context: Activity, val arr1:ArrayList<String>, val arr2:ArrayList<String>):ArrayAdapter<String>(context, R.layout.quicksave_layout, arr1) {
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.quicksave_layout, null, true)

        val titleText = rowView.findViewById(R.id.body) as TextView
        val subtitleText = rowView.findViewById(R.id.ts) as TextView

        titleText.text = arr1[position]
        subtitleText.text = arr2[position]

        return rowView

    }
}