package tech.mattico.melay

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
import android.app.Application
import android.support.text.emoji.EmojiCompat
import android.support.text.emoji.FontRequestEmojiCompatConfig
import android.support.v4.provider.FontRequest
import tech.mattico.melay.BuildConfig

import tech.mattico.melay.utils.NightModeManager
import tech.mattico.melay.injection.AppComponentManager
import tech.mattico.melay.injection.appComponent
import io.realm.Realm
import io.realm.RealmConfiguration
import tech.mattico.melay.R
//import tech.mattico.melay.manager.AnalyticsManager
import tech.mattico.melay.migration.MelayRealmMigration
import timber.log.Timber
import javax.inject.Inject

class MelayApplication : Application() {

    /**
     * Inject this so that it is forced to initialize
     */
    @Suppress("unused")
    @Inject lateinit var nightModeManager: NightModeManager

    override fun onCreate() {
        super.onCreate()


        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
                .compactOnLaunch()
                .migration(MelayRealmMigration())
                .schemaVersion(1)
                .build())

        AppComponentManager.init(this)
        appComponent.inject(this)

        //TODO move the AppCenter init here?

        nightModeManager.updateCurrentTheme()

        //TODO: remove this?
        val fontRequest = FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs)

        EmojiCompat.init(FontRequestEmojiCompatConfig(this, fontRequest))

        Timber.plant(Timber.DebugTree())
    }

}