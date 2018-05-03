package tech.mattico.melay.view.base

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

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.arch.lifecycle.Lifecycle
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.support.annotation.RequiresApi
import android.support.v4.graphics.drawable.DrawableCompat.setTint
import tech.mattico.melay.R
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import tech.mattico.melay.utils.Colors
import tech.mattico.melay.utils.extensions.setBackgroundTint
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.toolbar.*
import tech.mattico.melay.view.MelayActivity
import javax.inject.Inject

/**
 * Base activity that automatically applies any necessary theme theme settings and colors
 *
 * In most cases, this should be used instead of the base MelayActivity, except for when
 * an activity does not depend on the theme
 */
abstract class MelayThemedActivity<VM : MelayViewModel<*, *>> : MelayActivity<VM>() {

    @Inject lateinit var colors: Colors

    /**
     * In case the activity should be themed for a specific conversation, the selected conversation
     * can be changed by pushing the threadId to this subject
     */
    protected val threadId: Subject<Long> = BehaviorSubject.createDefault(0)

    /**
     * Switch the theme if the threadId changes
     */
    protected val theme = threadId
            .distinctUntilChanged()
            .switchMap { threadId -> colors.themeForConversation(threadId) }

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getAppThemeResourcesObservable()
                .autoDisposable(scope())
                .subscribe { res -> setTheme(res) }

        colors.systemBarIcons
                .autoDisposable(scope())
                .subscribe { systemUiVisibility -> window.decorView.systemUiVisibility = systemUiVisibility }

        colors.statusBar
                .autoDisposable(scope())
                .subscribe { color -> window.statusBarColor = color }

        colors.navigationBar
                .autoDisposable(scope())
                .subscribe { color -> window.navigationBarColor = color }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Update the colours of the menu items
        Observables.combineLatest(menu, theme, colors.textTertiary, { menu, theme, textTertiary ->
            (0 until menu.size())
                    .map { position -> menu.getItem(position) }
                    .forEach { menuItem ->
                        menuItem?.icon?.run {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                setTint(when (menuItem.itemId) {
                                    R.id.info -> textTertiary
                                    else -> theme
                                })
                            }

                            menuItem.icon = this
                        }
                    }
        }).autoDisposable(scope(Lifecycle.Event.ON_DESTROY)).subscribe()

        colors.textTertiary
                .doOnNext { color -> toolbar?.overflowIcon = toolbar?.overflowIcon?.apply { setTint(color) } }
                .doOnNext { color -> toolbar?.navigationIcon = toolbar?.navigationIcon?.apply { setTint(color) } }
                .autoDisposable(scope(Lifecycle.Event.ON_DESTROY))
                .subscribe()

        colors.popupThemeResource
                .autoDisposable(scope(Lifecycle.Event.ON_DESTROY))
                .subscribe { res -> toolbar?.popupTheme = res }

        colors.toolbarColor
                .doOnNext { color -> toolbar?.setBackgroundTint(color) }
                .doOnNext { color ->
                    // Set the color for the recent apps title
                    val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
                    val taskDesc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityManager.TaskDescription(getString(R.string.app_name), icon, color)
                    } else {
                        TODO("VERSION.SDK_INT < LOLLIPOP")
                    }
                    setTaskDescription(taskDesc)
                }
                .autoDisposable(scope(Lifecycle.Event.ON_DESTROY))
                .subscribe { color -> toolbar?.setBackgroundTint(color) }
    }

    /**
     * This can be overridden in case an activity does not want to use the default themes
     */
    open fun getAppThemeResourcesObservable() = colors.appThemeResources

}