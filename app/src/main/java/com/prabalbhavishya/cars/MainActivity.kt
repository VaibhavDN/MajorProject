package com.prabalbhavishya.cars

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val ic1: ImageView = findViewById(R.id.imageView)
        val ic2: ImageView = findViewById(R.id.imageView2)
        val ic3: ImageView = findViewById(R.id.imageView3)
        val ic4: ImageView = findViewById(R.id.imageView4)

        ic1.setImageDrawable(
            get_app_icon(
                this,
                "com.android.chrome",
                "com.google.android.apps.chrome.Main"
            )
        )
        ic2.setImageDrawable(
            get_app_icon(
                this,
                "com.File.manager",
                "com.File.manager.SplashActivity"
            )
        )
        ic3.setImageDrawable(
            get_app_icon(
                this,
                "com.android.deskclock",
                "com.android.deskclock.DeskClock"
            )
        )
        ic4.setImageDrawable(
            get_app_icon(
                this,
                "com.google.android.youtube",
                "com.google.android.apps.youtube.app.honeycomb.phone.NewVersionAvailableActivity"
            )
        )

    }

    fun get_app_icon(context: Context, app_name: String, app_activity_name: String): Drawable {
        val pm: PackageManager = context.packageManager
        val intent = Intent()
        intent.component = ComponentName(app_name, app_activity_name)
        val resolveinfo: ResolveInfo = pm.resolveActivity(intent, 0)
        return resolveinfo.loadIcon(pm)
    }
}
