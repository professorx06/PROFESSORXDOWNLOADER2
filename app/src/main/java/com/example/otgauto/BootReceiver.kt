package com.example.otgauto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        ctx.startForegroundService(Intent(ctx, TriggerService::class.java))
    }
}
