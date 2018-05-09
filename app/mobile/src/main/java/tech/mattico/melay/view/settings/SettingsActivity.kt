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

import android.app.ProgressDialog
import android.app.ProgressDialog.show
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import tech.mattico.melay.BuildConfig
import tech.mattico.melay.R
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.settings_activity.*
import kotlinx.android.synthetic.main.settings_switch_widget.view.*
import kotlinx.android.synthetic.main.settings_theme_widget.*
import tech.mattico.melay.injection.appComponent
import tech.mattico.melay.utils.Preferences
import tech.mattico.melay.utils.extensions.setBackgroundTint
import tech.mattico.melay.utils.extensions.setVisible
import tech.mattico.melay.view.MelayDialog
import tech.mattico.melay.view.base.MelayThemedActivity
import tech.mattico.melay.view.widget.PreferenceView
import javax.inject.Inject

class SettingsActivity : MelayThemedActivity<SettingsViewModel>(), SettingsView {

    @Inject lateinit var nightModeDialog: MelayDialog
    @Inject lateinit var textSizeDialog: MelayDialog
    @Inject lateinit var mmsSizeDialog: MelayDialog
    @Inject lateinit var telemetryLevelDialog: MelayDialog

    override val viewModelClass = SettingsViewModel::class
    override val preferenceClickIntent: Subject<PreferenceView> = PublishSubject.create()
    override val nightModeSelectedIntent by lazy { nightModeDialog.adapter.menuItemClicks }
    override val viewMelaysmsPlusIntent: Subject<Unit> = PublishSubject.create()
    override val startTimeSelectedIntent: Subject<Pair<Int, Int>> = PublishSubject.create()
    override val endTimeSelectedIntent: Subject<Pair<Int, Int>> = PublishSubject.create()
    override val textSizeSelectedIntent by lazy { textSizeDialog.adapter.menuItemClicks }
    override val telemetryLevelSelectedIntent by lazy { telemetryLevelDialog.adapter.menuItemClicks }
    override val mmsSizeSelectedIntent: Subject<Int> by lazy { mmsSizeDialog.adapter.menuItemClicks }

    // TODO remove this
    private val progressDialog by lazy {
        ProgressDialog(this).apply {
            isIndeterminate = true
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }

    init {
        appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setTitle(R.string.title_settings)
        showBackButton(true)
        viewModel.bindView(this)

        nightModeDialog.adapter.setData(R.array.night_modes)
        textSizeDialog.adapter.setData(R.array.text_sizes)
        telemetryLevelDialog.adapter.setData(R.array.text_sizes)
        mmsSizeDialog.adapter.setData(R.array.mms_sizes, R.array.mms_sizes_ids)

        about.summary = getString(R.string.settings_version, BuildConfig.VERSION_NAME)

        colors.background
                .autoDisposable(scope())
                .subscribe { color -> window.decorView.setBackgroundColor(color) }

        // Listen to clicks for all of the preferences
        (0 until preferences.childCount)
                .map { index -> preferences.getChildAt(index) }
                .filter { view -> view is PreferenceView }
                .forEach { preference ->
                    preference.clicks().subscribe {
                        preferenceClickIntent.onNext(preference as PreferenceView)
                    }
                }
    }

    override fun render(state: SettingsState) {
        if (progressDialog.isShowing && !state.syncing) progressDialog.dismiss()
        else if (!progressDialog.isShowing && state.syncing) progressDialog.show()

        defaultSms.summary = getString(when (state.isDefaultSmsApp) {
            true -> R.string.settings_default_sms_summary_true
            else -> R.string.settings_default_sms_summary_false
        })

        themePreview.setBackgroundTint(state.theme)
        night.summary = state.nightModeSummary
        nightModeDialog.adapter.selectedItem = state.nightModeId
        nightStart.setVisible(state.nightModeId == Preferences.NIGHT_MODE_AUTO)
        nightStart.summary = state.nightStart
        nightEnd.setVisible(state.nightModeId == Preferences.NIGHT_MODE_AUTO)
        nightEnd.summary = state.nightEnd

        black.setVisible(state.nightModeId != Preferences.NIGHT_MODE_OFF)
        black.checkbox.isChecked = state.black

        autoEmoji.checkbox.isChecked = state.autoEmojiEnabled
        delivery.checkbox.isChecked = state.deliveryEnabled

        textSize.summary = state.textSizeSummary
        textSizeDialog.adapter.selectedItem = state.textSizeId
        systemFont.checkbox.isChecked = state.systemFontEnabled

        textSizeDialog.adapter.selectedItem = state.telemetryLevelId
        telemetryLevel.summary = state.telemetryLevelSummary



        unicode.checkbox.isChecked = state.stripUnicodeEnabled

        mmsSize.summary = state.maxMmsSizeSummary
        mmsSizeDialog.adapter.selectedItem = state.maxMmsSizeId
    }

    // TODO change this to a PopupWindow
    override fun showNightModeDialog() = nightModeDialog.show(this)

    override fun showMelaysmsPlusSnackbar() {
        /*Snackbar.make(contentView, R.string.toast_melaysms_plus, Snackbar.LENGTH_LONG).run {
            setAction(R.string.button_more, { viewMelaysmsPlusIntent.onNext(Unit) })
            show()
        }*/

    }

    override fun showStartTimePicker(hour: Int, minute: Int) {
        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, newHour, newMinute ->
            startTimeSelectedIntent.onNext(Pair(newHour, newMinute))
        }, hour, minute, false).show()
    }

    override fun showEndTimePicker(hour: Int, minute: Int) {
        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, newHour, newMinute ->
            endTimeSelectedIntent.onNext(Pair(newHour, newMinute))
        }, hour, minute, false).show()
    }

    override fun showTextSizePicker() = textSizeDialog.show(this)

    override fun showMmsSizePicker() = mmsSizeDialog.show(this)

    override fun showTelemetryLevelPicker() = telemetryLevelDialog.show(this)

}