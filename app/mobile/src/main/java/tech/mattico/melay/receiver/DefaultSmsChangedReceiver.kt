package tech.mattico.melay.receiver

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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.support.annotation.RequiresApi
import tech.mattico.melay.injection.appComponent
import tech.mattico.melay.interactor.PartialSync
import tech.mattico.melay.utils.Preferences
import javax.inject.Inject

class DefaultSmsChangedReceiver : BroadcastReceiver() {

    @Inject lateinit var partialSync: PartialSync
    @Inject lateinit var prefs: Preferences

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onReceive(context: Context, intent: Intent) {
        appComponent.inject(this)

        val isDefaultSmsApp = intent.getBooleanExtra(Telephony.Sms.Intents.EXTRA_IS_DEFAULT_SMS_APP, false)
        prefs.defaultSms.set(isDefaultSmsApp)

        if (isDefaultSmsApp) {
            val pendingResult = goAsync()
            partialSync.execute(Unit, { pendingResult.finish() })
        }
    }

}