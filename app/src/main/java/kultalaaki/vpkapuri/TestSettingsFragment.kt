/*
 * Created by Kultala Aki on 3/6/21 12:26 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 3/6/21 10:37 AM
 */

package kultalaaki.vpkapuri

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
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

// TODO: Rename parameter arguments, choose names that match
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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_settings, container, false)
    }

    override fun onStart() {
        super.onStart()
        testSettings()
    }

    private fun testSettings() {
        if (context?.let {
                    ContextCompat.checkSelfPermission(it,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                == PackageManager.PERMISSION_GRANTED) {
            storageOK.setTextColor(Color.parseColor(green))
        }
        if (context?.let {
                    ContextCompat.checkSelfPermission(it,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                == PackageManager.PERMISSION_GRANTED) {
            storageOK.setTextColor(Color.parseColor(green))
        }

        if ((context?.let {
                    ContextCompat.checkSelfPermission(it,
                            Manifest.permission.READ_SMS)
                }
                        == PackageManager.PERMISSION_GRANTED)) {
            SMSOK.setTextColor(Color.parseColor(green))
        }
        if ((context?.let {
                    ContextCompat.checkSelfPermission(it,
                            Manifest.permission.SEND_SMS)
                }
                        == PackageManager.PERMISSION_GRANTED)) {
            SMSOK.setTextColor(Color.parseColor(green))
        }
        if ((context?.let {
                    ContextCompat.checkSelfPermission(it,
                            Manifest.permission.RECEIVE_SMS)
                }
                        == PackageManager.PERMISSION_GRANTED)) {
            SMSOK.setTextColor(Color.parseColor(green))
        }

        if (context?.let {
                    ContextCompat.checkSelfPermission(it,
                            Manifest.permission.READ_PHONE_STATE)
                }
                == PackageManager.PERMISSION_GRANTED) {
            phoneOK.setTextColor(Color.parseColor(green))
        }
        if ((context?.let {
                    ContextCompat.checkSelfPermission(it,
                            Manifest.permission.READ_CALL_LOG)
                }
                        == PackageManager.PERMISSION_GRANTED)) {
            callLogOK.setTextColor(Color.parseColor(green))
        }

        val notificationManager = activity?.applicationContext?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && notificationManager.isNotificationPolicyAccessGranted) {
            doNotDisturbOK.setTextColor(Color.parseColor(green))
        }

        val powerManager = activity?.applicationContext?.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (powerManager.isIgnoringBatteryOptimizations(context?.packageName)) {
                batteryOK.setTextColor(Color.parseColor(green))
            }
        } else {
            batteryOK.setTextColor(Color.parseColor(green))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(activity)) {
                showOnTopOK.setTextColor(Color.parseColor(green))
            }
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context) ?: return
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
        // TODO: Rename and change types and number of parameters
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