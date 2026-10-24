package com.example.otgauto

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 48)
        }
        status = TextView(this).apply { textSize = 16f }
        root.addView(status)
        fun btn(label: String, action: () -> Unit) = root.addView(Button(this).apply {
            text = label; setOnClickListener { action() }
        })
        btn("1. Accessibility ON karo") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        btn("2. Display over other apps allow karo") {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
        }
        btn("3. Battery: Allow background activity") {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")))
        }
        btn("4. Service START") {
            if (Build.VERSION.SDK_INT >= 33) requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            startForegroundService(Intent(this, TriggerService::class.java))
        }
        btn("Test: OTG abhi ON karo") { OtgAccessibilityService.request(this) }
        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val a11y = am.getEnabledAccessibilityServiceList(-1).any { it.id.contains(packageName) }
        val overlay = Settings.canDrawOverlays(this)
        status.text = "Accessibility: " + (if (a11y) "ON" else "OFF") + "\nOverlay: " + (if (overlay) "OK" else "NO")
    }
}
