package com.taccotap.phahtaigi.preferences

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.taccotap.phahtaigi.databinding.ActivityPrefsBinding

class PrefsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrefsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrefsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.step1Button.setOnClickListener{
            onClickStep1Button()
        }
        binding.step2Button.setOnClickListener{
            onClickStep2Button()
        }
        binding.supportButton.setOnClickListener{
            onClickSupportButton()
        }
        binding.feedbackButton.setOnClickListener{
            onClickFeedbackButton()
        }

        val pInfo: PackageInfo?
        try {
            pInfo =
                baseContext.packageManager.getPackageInfo(baseContext.packageName, 0)
            val versionName = pInfo.versionName
            title = "PhahTaigi $versionName pán"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun onClickStep1Button() {
        val startSettings = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
        startSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startSettings.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startSettings.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(startSettings)
    }

    private fun onClickStep2Button() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showInputMethodPicker()
    }

    private fun onClickSupportButton() {
        val myIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.zeczec.com/projects/taibun-kesimi"))
        startActivity(myIntent)
    }

    private fun onClickFeedbackButton() {
        val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/PhahTaigi"))
        startActivity(myIntent)
    }
}