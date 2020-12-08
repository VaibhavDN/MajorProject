package com.prabalbhavishya.cars


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prabalbhavishya.cars.adap.viewholder

class adap(private val array: ArrayList<pojo>) : RecyclerView.Adapter<adap.viewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_item, parent, false)
        return viewholder(view)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        val displayName = array[position]?.dName
        val date = array[position]?.date
        var dtm = array[position]?.dtm
        var life = array[position]?.life
        holder.txt.text = "$displayName on $date"
        if(System.currentTimeMillis() + life > dtm) {
            holder.bg.setBackgroundColor(Color.GREEN)
            holder.txt.setTextColor(Color.BLACK)
        }
        else {
            holder.bg.setBackgroundColor(Color.RED)
            holder.txt.setTextColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return array.size
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt: TextView
        var bg: LinearLayout

        init {
            txt = itemView.findViewById(R.id.text)
            bg = itemView.findViewById(R.id.bgevent)
        }
    }
}