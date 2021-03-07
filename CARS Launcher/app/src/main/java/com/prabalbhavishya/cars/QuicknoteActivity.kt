package com.prabalbhavishya.cars

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_quicknote.*

class QuicknoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quicknote)
        setSupportActionBar(findViewById(R.id.tbr1))


        var tdb = TinyDB(this)

        val myListAdapter = QuickeditAdapter(this, tdb.getListString("qs"), tdb.getListString("qd"))
        listview.adapter = myListAdapter

    }
}