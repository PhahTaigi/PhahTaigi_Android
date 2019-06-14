package com.taccotap.taigidictparser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taccotap.taigidictparser.tailo.parser.input.KipParseIntentService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        KipParseIntentService.startParsing(this)
        finish()
    }
}
