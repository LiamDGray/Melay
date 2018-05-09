package tech.mattico.melay.manager

import android.content.Context
import tech.mattico.melay.utils.DateFormatter
import tech.mattico.melay.utils.Preferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
        private val context: Context,
        private val prefs: Preferences) {

//AppCenter.start(getApplication(), "c658bc27-e599-45f5-917f-aed309b79dac", Analytics.class, Crashes.class);
}