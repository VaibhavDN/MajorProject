@file:Suppress("DEPRECATION")

package com.prabalbhavishya.cars

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

import java.lang.reflect.Field
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    val WRITE_STORAGE_PERMISSION_CODE = 1
    val READ_CALENDAR_PERMISSION_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        try {
            val path = getExternalFilesDir("UsageDir")
            val file = File(path, "appUsageData.csv")
            if (!file.isFile) {
                throw Exception("File doesn't exist. Please wait for 15 minutes")
            } else {
                val fileOutputStream = FileOutputStream(file, true)
                fileOutputStream.write("".toByteArray())
                fileOutputStream.close()
            }
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissionString = Array(1) { "" }
            permissionString[0] = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ActivityCompat.requestPermissions(this, permissionString, WRITE_STORAGE_PERMISSION_CODE)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissionString = Array(1) { "" }
            permissionString[0] = android.Manifest.permission.READ_CALENDAR
            ActivityCompat.requestPermissions(this, permissionString, READ_CALENDAR_PERMISSION_CODE)
        }

        val viewpager = findViewById<ViewPager>(R.id.home_screen_viewpager)
        val pageAdapter = ViewPagerAdapter(supportFragmentManager)
        viewpager.adapter = pageAdapter
        viewpager.setCurrentItem(1, false)
        val mFlingDistance: Field
        mFlingDistance = ViewPager::class.java.getDeclaredField("mFlingDistance")
        mFlingDistance.isAccessible = true
        mFlingDistance.set(viewpager, 30)
    }

    class ViewPagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            when (position) {
                2 -> return SettingsFragment() //Settings fragment at position 2
                1 -> return HomeFragment() //Homefrag at position 0
                0 -> return LeftFragment() //Leftfrag at position 1
            }
            return HomeFragment() //does not happen
        }

        override fun getCount(): Int {
            return 3 //three fragments
        }
    }

    override fun onBackPressed() {
        val bottomSheet: ConstraintLayout = findViewById(R.id.layoutBottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val drawerSearchEditText = findViewById<EditText>(R.id.drawerSearch_EditText)

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            if (drawerSearchEditText.text.toString().trim() == "") {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                val emptyString = ""
                drawerSearchEditText.setText(emptyString)
            }
        } else {
            Toast.makeText(applicationContext, "Already Home", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRestart() {
        super.onRestart()
        Toast.makeText(applicationContext, "Welcome Back", Toast.LENGTH_SHORT).show()
    }

    private fun checkIfPermissionGranted(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_STORAGE_PERMISSION_CODE) {
            checkIfPermissionGranted(grantResults)
        } else if (requestCode == READ_CALENDAR_PERMISSION_CODE) {
            checkIfPermissionGranted(grantResults)
        }
    }
}