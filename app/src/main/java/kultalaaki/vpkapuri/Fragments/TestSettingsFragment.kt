/*
 * Created by Kultala Aki on 6/26/22, 6:18 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 6/26/22, 6:02 PM
 */

package kultalaaki.vpkapuri.Fragments

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_test_settings.*
import kultalaaki.vpkapuri.R

//import kotlinx.android.synthetic.main.fragment_test_settings.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private val green = "#0EB82A"

/**
 * A simple [Fragment] subclass.
 * Use the [TestSettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TestSettingsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_settings, container, false)
    }

    override fun onStart() {
        super.onStart()
        testSettings()
    }

    private fun testSettings() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
            == PackageManager.PERMISSION_GRANTED) {
            storageOK.setTextColor(Color.parseColor(green))
        }
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            == PackageManager.PERMISSION_GRANTED) {
            storageOK.setTextColor(Color.parseColor(green))
        }

        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                )
            }
            == PackageManager.PERMISSION_GRANTED) {
            cameraOK.setTextColor(Color.parseColor(green))
        }

        if ((context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_SMS
                )
            }
                    == PackageManager.PERMISSION_GRANTED)) {
            SMSOK.setTextColor(Color.parseColor(green))
        }
        if ((context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.SEND_SMS
                )
            }
                    == PackageManager.PERMISSION_GRANTED)) {
            SMSOK.setTextColor(Color.parseColor(green))
        }
        if ((context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.RECEIVE_SMS
                )
            }
                    == PackageManager.PERMISSION_GRANTED)) {
            SMSOK.setTextColor(Color.parseColor(green))
        }


        val notificationManager =
            activity?.applicationContext?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.isNotificationPolicyAccessGranted) {
            doNotDisturbOK.setTextColor(Color.parseColor(green))
        }

        val powerManager =
            activity?.applicationContext?.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager.isIgnoringBatteryOptimizations(context?.packageName)) {
            batteryOK.setTextColor(Color.parseColor(green))
        }

        if (Settings.canDrawOverlays(activity)) {
            showOnTopOK.setTextColor(Color.parseColor(green))
        }

        val sharedPreferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
            ?: return
        val alarmVolume = sharedPreferences.getInt("SEEKBAR_VALUE", -1)
        alarmVolumeVal.text = alarmVolume.toString()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TestSettingsFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TestSettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}