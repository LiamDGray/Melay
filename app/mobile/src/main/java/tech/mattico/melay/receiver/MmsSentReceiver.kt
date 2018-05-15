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
package tech.mattico.melay.receiver

import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Telephony
import com.google.android.mms.MmsException
import com.google.android.mms.util_alt.SqliteWrapper
import com.klinker.android.send_message.MmsSentReceiver
import com.microsoft.appcenter.analytics.Analytics
import tech.mattico.melay.injection.appComponent
import tech.mattico.melay.interactor.SyncMessage
import timber.log.Timber
import javax.inject.Inject

class MmsSentReceiver : MmsSentReceiver() {
    @Inject lateinit var syncMessage: SyncMessage

    override fun onMessageStatusUpdated(context: Context, intent: Intent, resultCode: Int) {

        appComponent.inject(this)

        if (resultCode == Activity.RESULT_OK) {
            Timber.d("SENT MMS$resultCode")
            Timber.d("SENT MMS "+intent.dataString)
            Timber.d("SENT MMS "+intent.toString())
            Analytics.trackEvent("SENT MMS "+resultCode);

            Uri.parse(intent.getStringExtra("content_uri")).let { uri ->
                syncMessage.execute(uri)
            }
        }
        else {
            Timber.d("MMS SENT Failure")
            Analytics.trackEvent("SENT MMS FAILURE $resultCode");
            Uri.parse(intent.getStringExtra("content_uri")).let { uri ->
                syncMessage.execute(uri)
            }
        }
    }


}