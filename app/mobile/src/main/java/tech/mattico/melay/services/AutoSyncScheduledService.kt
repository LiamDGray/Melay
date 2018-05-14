package tech.mattico.melay.services

/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

import android.content.Context
import android.content.Intent

import javax.inject.Inject

import tech.mattico.melay.injection.appComponent

/**
 * @author Ushahidi Team <team></team>@ushahidi.com>
 */
class AutoSyncScheduledService : BaseWakefulIntentService(CLASS_TAG) {

    // holds the status of the sync and sends it to the pending messages
    // activity to update the ui
    private val statusIntent: Intent

    /*@Inject
    internal var mProcessMessage: PostMessage? = null*/

    init {
        statusIntent = Intent(ServiceConstants.AUTO_SYNC_ACTION)
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
    }

    override fun executeTask(intent: Intent) {
        log(CLASS_TAG, "doWakefulWork() executing $CLASS_TAG")
        //mProcessMessage!!.syncPendingMessages()
        //statusIntent.putExtra("status", mProcessMessage!!.getErrorMessage())
        sendBroadcast(statusIntent)
    }

    companion object {

        private val CLASS_TAG = AutoSyncScheduledService::class.java
                .simpleName

    }
}
