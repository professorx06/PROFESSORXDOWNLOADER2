package com.example.otgauto

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Settings kholta hai -> "Additional settings" -> "OTG connection" -> toggle ON.
 */
class OtgAccessibilityService : AccessibilityService() {

    companion object {
        @Volatile private var pending = false
        @Volatile private var startedAt = 0L
        private const val TIMEOUT_MS = 25_000L
        private val NEXT_LABELS = listOf("Additional settings", "Additional Settings", "More settings")
        private val OTG_LABELS = listOf("OTG connection", "OTG")

        fun request(ctx: Context) {
            pending = true
            startedAt = System.currentTimeMillis()
            ctx.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!pending) return
        if (System.currentTimeMillis() - startedAt > TIMEOUT_MS) { pending = false; return }
        val root = rootInActiveWindow ?: return

        find(root, OTG_LABELS)?.let { label ->
            val row = clickableParent(label)
            val sw = row?.let { findSwitch(it) }
            if (sw != null && sw.isChecked) {
                finish()
            } else if (row != null) {
                row.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                finish()
            }
            return
        }
        find(root, NEXT_LABELS)?.let {
            clickableParent(it)?.performAction(AccessibilityNodeInfo.ACTION_CLICK); return
        }
        scrollable(root)?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
    }

    private fun finish() {
        pending = false
        performGlobalAction(GLOBAL_ACTION_HOME)
    }

    private fun find(root: AccessibilityNodeInfo, labels: List<String>): AccessibilityNodeInfo? {
        for (l in labels) root.findAccessibilityNodeInfosByText(l)?.firstOrNull()?.let { return it }
        return null
    }

    private fun clickableParent(n: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        var c = n
        while (c != null && !c.isClickable) c = c.parent
        return c
    }

    private fun findSwitch(n: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (n.isCheckable) return n
        for (i in 0 until n.childCount) n.getChild(i)?.let { findSwitch(it)?.let { s -> return s } }
        return null
    }

    private fun scrollable(n: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (n.isScrollable) return n
        for (i in 0 until n.childCount) n.getChild(i)?.let { scrollable(it)?.let { s -> return s } }
        return null
    }

    override fun onInterrupt() {}
}
