package tech.mattico.melay.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import tech.mattico.melay.R
import timber.log.Timber
import java.util.logging.Logger

/**
 * Schedules background services to run at specified intervals. This is not a service in itself.
 *
 * @author Henry Addo
 */
class Scheduler(context: Context, intent: Intent, requestCode: Int,
                flags: Int) {

    private val mAlarmManager: AlarmManager?

    private val mPendingIntent: PendingIntent?

    private val mContext: Context

    init {
        mContext = context.getApplicationContext()
        Timber.d(CLASS_TAG, "ScheduleServices() executing scheduled services ")
        mAlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        mPendingIntent = PendingIntent.getBroadcast(mContext, requestCode, intent, flags)
    }


    /**
     * Stops the scheduled service
     */
    fun stopScheduler() {
        if (mAlarmManager != null && mPendingIntent != null) {
            Timber.d(CLASS_TAG, "Stop scheduler")
            //TODO re-enable file manager?
            //mFileManager.append(mContext.getString(R.string.stopping_scheduler))
            mAlarmManager.cancel(mPendingIntent)
        }
    }

    /**
     * Updates the interval of the scheduled service
     *
     * @param interval The interval at which the service should run.
     */
    fun updateScheduler(interval: Long) {
        Timber.d(CLASS_TAG, "updating scheduler")
        if (mAlarmManager != null && mPendingIntent != null) {
            Timber.d(CLASS_TAG, "Update scheduler to $interval")
            //TODO re-enable file manager?
            //mFileManager.append(mContext.getString(R.string.scheduler_updated_to))
            mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 60000, interval, mPendingIntent)
        }
    }

    companion object {

        private val CLASS_TAG = Scheduler::class.java
                .simpleName
    }
}