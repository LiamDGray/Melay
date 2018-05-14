package tech.mattico.melay.services

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import com.commonsware.cwac.wakeful.WakefulIntentService
import tech.mattico.melay.injection.appComponent
import timber.log.Timber

import java.util.logging.Logger

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

/**
 * @author Ushahidi Team <team></team>@ushahidi.com>
 */
abstract class BaseWakefulIntentService(name: String) : WakefulIntentService(name) {

    //protected AppServiceComponent mAppServiceComponent;

    /*
     * Subclasses must implement this method so it executes any tasks
     * implemented in it.
     */
    protected abstract fun executeTask(intent: Intent)

    public override fun doWakefulWork(intent: Intent) {
        log("onHandleIntent(): running service")

        //TODO implement is connected utility
        val isConnected = false //Utility.isConnected(this);

        // check if we have internet
        if (!isConnected) {
            // Enable the Connectivity Changed Receiver to listen for
            // connection to a network so we can execute pending messages.
            val pm = packageManager
            //ComponentName connectivityReceiver = new ComponentName(this,ConnectivityChangedReceiver.class);
            //pm.setComponentEnabledSetting(connectivityReceiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        } else {
            // Execute the task
            executeTask(intent)
        }
    }


    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    protected fun log(message: String) {
        Timber.d(javaClass.name,message)
    }

    protected fun log(format: String, vararg args: Any) {
        Timber.d(javaClass.name,format, args)
    }

    protected fun log(message: String, ex: Exception) {
        Timber.d(javaClass.name,message, ex)
    }
}
