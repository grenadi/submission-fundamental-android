package com.example.githubuser

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.githubuser.AlarmReceiver.AlarmReceiver
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    companion object{
        const val PREFS_NAME = "SettingPref"
        private const val DAILY = "daily"
    }

    private lateinit var alarmReceiver: AlarmReceiver
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.setting)

        alarmReceiver = AlarmReceiver()
        mSharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        setSwitch()
        swDaily.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                alarmReceiver.setDailyReminder(
                    this,
                    AlarmReceiver.TYPE_DAILY,
                    getString(R.string.daily_message)
                )
            } else {
                alarmReceiver.cancelAlarm(this)
            }
            saveChange(isChecked)
        }

    }

    private fun saveChange(value: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(DAILY, value)
        editor.apply()
    }

    private fun setSwitch() {
        swDaily.isChecked = mSharedPreferences.getBoolean(DAILY, false)
    }
}