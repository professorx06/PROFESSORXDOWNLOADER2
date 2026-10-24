package com.example.otgauto

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.IBinder
import androidx.core.content.ContextCompat

/** Foreground service: headset / USB / unlock events sunta hai. */
class TriggerService : Service() {

    private var lastFire = 0L

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            val fire = when (intent.action) {
                Intent.ACTION_HEADSET_PLUG -> intent.getIntExtra("state", 0) == 1
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> true
                Intent.ACTION_USER_PRESENT -> true
                else -> false
            }
            val now = System.currentTimeMillis()
            if (fire && now - lastFire > 15_000) {
                lastFire = now
                OtgAccessibilityService.request(ctx)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(NotificationChannel("otg", "OTG Auto", NotificationManager.IMPORTANCE_MIN))
        startForeground(1, Notification.Builder(this, "otg")
            .setContentTitle("OTG Auto chal raha hai")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .build())
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
    }

    override fun onStartCommand(i: Intent?, f: Int, id: Int) = START_STICKY
    override fun onDestroy() { unregisterReceiver(receiver); super.onDestroy() }
    override fun onBind(i: Intent?): IBinder? = null
}
