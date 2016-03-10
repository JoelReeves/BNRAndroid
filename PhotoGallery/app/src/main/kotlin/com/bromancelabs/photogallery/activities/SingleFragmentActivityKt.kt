package com.bromancelabs.photogallery.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bromancelabs.photogallery.R

abstract class SingleFragmentActivityKt : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        supportActionBar?.title = "${title}Kt"

        Toast.makeText(this, "Activity created with Kotlin", Toast.LENGTH_LONG).show()

        if (null == supportFragmentManager.findFragmentById(R.id.fragment_container)) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, createFragment())
                    .commit()
        }
    }

    abstract fun createFragment(): Fragment
}