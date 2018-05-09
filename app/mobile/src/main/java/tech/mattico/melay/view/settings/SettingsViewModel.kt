/*
 * Copyright (C) 2017 Moez Bhatti <moez.bhatti@gmail.com>
 *
 * This file is part of QKSMS.
 *
 * QKSMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QKSMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QKSMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package tech.mattico.melay.view.settings

import android.content.Context
import tech.mattico.melay.R
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.rxkotlin.plusAssign
import tech.mattico.melay.injection.appComponent
import tech.mattico.melay.interactor.FullSync
import tech.mattico.melay.utils.Colors
import tech.mattico.melay.utils.DateFormatter
import tech.mattico.melay.utils.NightModeManager
import tech.mattico.melay.utils.Preferences
import tech.mattico.melay.view.Navigator
import tech.mattico.melay.view.base.MelayViewModel
import com.uber.autodispose.android.lifecycle.scope
import io.reactivex.rxkotlin.withLatestFrom
import tech.mattico.melay.manager.BillingManager
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class SettingsViewModel : MelayViewModel<SettingsView, SettingsState>(SettingsState()) {

    @Inject lateinit var context: Context
    @Inject lateinit var billingManager: BillingManager
    @Inject lateinit var colors: Colors
    @Inject lateinit var dateFormatter: DateFormatter
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var nightModeManager: NightModeManager
    @Inject lateinit var prefs: Preferences
    @Inject lateinit var fullSync: FullSync

    init {
        appComponent.inject(this)

        disposables += prefs.defaultSms
                .asObservable()
                .subscribe { isDefaultSmsApp ->
                    newState { it.copy(isDefaultSmsApp = isDefaultSmsApp) }
                }

        disposables += colors.theme
                .subscribe { color -> newState { it.copy(theme = color) } }

        val nightModeLabels = context.resources.getStringArray(R.array.night_modes)
        disposables += prefs.nightMode.asObservable()
                .subscribe { nightMode ->
                    newState { it.copy(nightModeSummary = nightModeLabels[nightMode], nightModeId = nightMode) }
                }

        disposables += prefs.nightStart.asObservable()
                .subscribe { nightStart -> newState { it.copy(nightStart = nightStart) } }

        disposables += prefs.nightEnd.asObservable()
                .subscribe { nightEnd -> newState { it.copy(nightEnd = nightEnd) } }

        disposables += prefs.black.asObservable()
                .subscribe { black -> newState { it.copy(black = black) } }

        disposables += prefs.autoEmoji.asObservable()
                .subscribe { enabled -> newState { it.copy(autoEmojiEnabled = enabled) } }

        disposables += prefs.notifications().asObservable()
                .subscribe { enabled -> newState { it.copy(notificationsEnabled = enabled) } }

        disposables += prefs.delivery.asObservable()
                .subscribe { enabled -> newState { it.copy(deliveryEnabled = enabled) } }

        val textSizeLabels = context.resources.getStringArray(R.array.text_sizes)
        disposables += prefs.textSize.asObservable()
                .subscribe { textSize ->
                    newState { it.copy(textSizeSummary = textSizeLabels[textSize], textSizeId = textSize) }
                }

        val telemetryLevelLabels = context.resources.getStringArray(R.array.telemetry_levels)
        disposables += prefs.telemetryLevel.asObservable()
                .subscribe { level ->
                    newState { it.copy(telemetryLevelSummary = textSizeLabels[level], telemetryLevelId = level) }
                }

        disposables += prefs.systemFont.asObservable()
                .subscribe { enabled -> newState { it.copy(systemFontEnabled = enabled) } }

        disposables += prefs.unicode.asObservable()
                .subscribe { enabled -> newState { it.copy(stripUnicodeEnabled = enabled) } }

        val mmsSizeLabels = context.resources.getStringArray(R.array.mms_sizes)
        val mmsSizeIds = context.resources.getIntArray(R.array.mms_sizes_ids)
        disposables += prefs.mmsSize.asObservable()
                .subscribe { maxMmsSize ->
                    val index = mmsSizeIds.indexOf(maxMmsSize)
                    newState { it.copy(maxMmsSizeSummary = mmsSizeLabels[index], maxMmsSizeId = maxMmsSize) }
                }

        disposables += fullSync
    }

    override fun bindView(view: SettingsView) {
        super.bindView(view)

        view.preferenceClickIntent
                .autoDisposable(view.scope())
                .subscribe {
                    Timber.v("Preference click: ${context.resources.getResourceName(it.id)}")

                    when (it.id) {
                        R.id.defaultSms -> navigator.showDefaultSmsDialog()

                        R.id.theme -> navigator.showThemePicker()

                        R.id.night -> view.showNightModeDialog()

                        R.id.nightStart -> {
                            val date = dateFormatter.parseTime(prefs.nightStart.get())
                            view.showStartTimePicker(date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE))
                        }

                        R.id.nightEnd -> {
                            val date = dateFormatter.parseTime(prefs.nightEnd.get())
                            view.showEndTimePicker(date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE))
                        }

                        R.id.black -> prefs.black.set(!prefs.black.get())

                        R.id.autoEmoji -> prefs.autoEmoji.set(!prefs.autoEmoji.get())

                        R.id.notifications -> navigator.showNotificationSettings()

                        R.id.blocked -> navigator.showBlockedConversations()

                        R.id.delivery -> prefs.delivery.set(!prefs.delivery.get())

                        R.id.textSize -> view.showTextSizePicker()

                        R.id.telemetryLevel -> view.showTelemetryLevelPicker()

                        R.id.systemFont -> prefs.systemFont.set(!prefs.systemFont.get())

                        R.id.unicode -> prefs.unicode.set(!prefs.unicode.get())

                        R.id.mmsSize -> view.showMmsSizePicker()

                        R.id.sync -> {
                            newState { it.copy(syncing = true) }
                            fullSync.execute(Unit, {
                                newState { it.copy(syncing = false) }
                            })
                        }

                        R.id.about -> navigator.showAbout()
                    }
                }

        view.nightModeSelectedIntent
                .withLatestFrom(billingManager.upgradeStatus, { mode, upgraded ->
                    if (!upgraded && mode == Preferences.NIGHT_MODE_AUTO) {
                        view.showMelaysmsPlusSnackbar()
                    } else {
                        nightModeManager.updateNightMode(mode)
                    }
                })
                .autoDisposable(view.scope())
                .subscribe()

        view.viewMelaysmsPlusIntent
                .autoDisposable(view.scope())
                .subscribe { navigator.showMelaysmsPlusActivity() }

        view.startTimeSelectedIntent
                .doOnNext { nightModeManager.updateAlarms() }
                .map { dateFormatter.formatTime(it.first, it.second) }
                .autoDisposable(view.scope())
                .subscribe { prefs.nightStart.set(it) }

        view.endTimeSelectedIntent
                .doOnNext { nightModeManager.updateAlarms() }
                .map { dateFormatter.formatTime(it.first, it.second) }
                .autoDisposable(view.scope())
                .subscribe { prefs.nightEnd.set(it) }

        view.textSizeSelectedIntent
                .autoDisposable(view.scope())
                .subscribe { prefs.textSize.set(it) }

        view.telemetryLevelSelectedIntent
                .autoDisposable(view.scope())
                .subscribe { prefs.telemetryLevel.set(it) }

        view.mmsSizeSelectedIntent
                .autoDisposable(view.scope())
                .subscribe { prefs.mmsSize.set(it) }
    }
}