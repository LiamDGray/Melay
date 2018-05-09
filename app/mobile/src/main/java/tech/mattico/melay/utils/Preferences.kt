/*
 * Copyright (C) 2017 Moez Bhatti <moez.bhatti@gmail.com>
 *
 * This file is part of Melay SMS.
 *
 * Melay SMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Melay SMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Melay SMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package tech.mattico.melay.utils

import android.os.Build
import android.provider.Settings
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(private val rxPrefs: RxSharedPreferences) {

    companion object {
        const val NIGHT_MODE_OFF = 0
        const val NIGHT_MODE_ON = 1
        const val NIGHT_MODE_AUTO = 2

        const val TEXT_SIZE_SMALL = 0
        const val TEXT_SIZE_NORMAL = 1
        const val TEXT_SIZE_LARGE = 2
        const val TEXT_SIZE_LARGER = 3

        const val NOTIFICATION_PREVIEWS_ALL = 0
        const val NOTIFICATION_PREVIEWS_NAME = 1
        const val NOTIFICATION_PREVIEWS_NONE = 2

        const val SEND_DELAY_NONE = 0
        const val SEND_DELAY_SHORT = 1
        const val SEND_DELAY_MEDIUM = 2
        const val SEND_DELAY_LONG = 3

        const val TELEMETRY_NONE = 0
        const val TELEMETRY_CRASHES = 1
        const val TELEMETRY_USAGE = 2
        const val TELEMETRY_DEBUG = 3
    }

    val defaultSms = rxPrefs.getBoolean("defaultSms", false)
    val night = rxPrefs.getBoolean("night", false)
    val nightMode = rxPrefs.getInteger("nightModeSummary", NIGHT_MODE_OFF)
    val nightStart = rxPrefs.getString("nightStart", "6:00 PM")
    val nightEnd = rxPrefs.getString("nightEnd", "6:00 AM")
    val black = rxPrefs.getBoolean("black", false)
    val sia = rxPrefs.getBoolean("sia", false)
    val autoEmoji = rxPrefs.getBoolean("autoEmoji", true)
    val delivery = rxPrefs.getBoolean("delivery", false)
    val systemFont = rxPrefs.getBoolean("systemFont", false)
    val textSize = rxPrefs.getInteger("textSize", TEXT_SIZE_NORMAL)
    val qkreply = rxPrefs.getBoolean("qkreply", Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
    val qkreplyTapDismiss = rxPrefs.getBoolean("qkreplyTapDismiss", true)
    val unicode = rxPrefs.getBoolean("unicode", false)
    val mmsSize = rxPrefs.getInteger("mmsSize", 100)
    val sendDelay = rxPrefs.getInteger("sendDelay", SEND_DELAY_NONE)
    val telemetryLevel = rxPrefs.getInteger("telemetryLevel", TELEMETRY_USAGE)

    fun theme(threadId: Long = 0): Preference<Int> {
        val default = rxPrefs.getInteger("theme", 0xFF0097A7.toInt())

        return when (threadId) {
            0L -> default
            else -> rxPrefs.getInteger("theme_$threadId", default.get())
        }
    }

    fun notifications(threadId: Long = 0): Preference<Boolean> {
        val default = rxPrefs.getBoolean("notifications", true)

        return when (threadId) {
            0L -> default
            else -> rxPrefs.getBoolean("notifications_$threadId", default.get())
        }
    }

    fun notificationPreviews(threadId: Long = 0): Preference<Int> {
        val default = rxPrefs.getInteger("notification_previews", 0)

        return when (threadId) {
            0L -> default
            else -> rxPrefs.getInteger("notification_previews_$threadId", default.get())
        }
    }

    fun vibration(threadId: Long = 0): Preference<Boolean> {
        val default = rxPrefs.getBoolean("vibration", true)

        return when (threadId) {
            0L -> default
            else -> rxPrefs.getBoolean("vibration$threadId", default.get())
        }
    }

    fun ringtone(threadId: Long = 0): Preference<String> {
        val default = rxPrefs.getString("ringtone", Settings.System.DEFAULT_NOTIFICATION_URI.toString())

        return when (threadId) {
            0L -> default
            else -> rxPrefs.getString("ringtone_$threadId", default.get())
        }
    }
}