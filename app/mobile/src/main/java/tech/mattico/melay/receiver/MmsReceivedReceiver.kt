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

import android.content.Context
import android.net.Uri
import com.klinker.android.send_message.MmsReceivedReceiver
import com.microsoft.appcenter.analytics.Analytics
import tech.mattico.melay.injection.appComponent
import tech.mattico.melay.interactor.ReceiveMms
import timber.log.Timber
import javax.inject.Inject

class MmsReceivedReceiver : com.klinker.android.send_message.MmsReceivedReceiver() {
    /*override fun onMessageReceived(p0: Context?, messageUri: Uri?) {
        appComponent.inject(this)

        messageUri?.let { uri ->
            val pendingResult = goAsync()
            receiveMms.execute(uri) { pendingResult.finish() }
        }
    }*/
    @Inject lateinit var receiveMms: ReceiveMms
    override fun onMessageReceived(p0: Context?, messageUri: Uri?){
        appComponent.inject(this)
        Timber.d("Recieved MMS")
        Analytics.trackEvent("Recieved MMS ");

        messageUri?.let { uri -> receiveMms.execute(uri) }
    }

    override fun onError(p0: Context?, p1: String?) {
        TODO("Not implemented");
        Timber.d("MMS RECEIVE Failure $p1")
        Analytics.trackEvent("RECEIVE MMS FAILURE $p1");
    }



}