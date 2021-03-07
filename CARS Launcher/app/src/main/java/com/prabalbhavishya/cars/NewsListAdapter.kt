package com.prabalbhavishya.cars

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.*


class NewsListAdapter(private var Newslist: ArrayList<NewsObject>) :
    RecyclerView.Adapter<NewsListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context: Context = itemView.context
        var root: CardView = itemView.findViewById(R.id.new_layout)
        var appIconImageView: ImageView = itemView.findViewById(R.id.newsimage)
        var titleName: TextView = itemView.findViewById(R.id.title)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Newslist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.titleName.text = Newslist[position].get_articleName()
            Picasso.with(holder.context).load(Newslist[position].get_image()).into(holder.appIconImageView)
            Log.println(Log.ASSERT, "imgurl", Newslist[position].get_image())
            holder.root.setOnClickListener {
                val url = Newslist[position].get_url()
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(holder.context, i, null)
            }

        }

    }
