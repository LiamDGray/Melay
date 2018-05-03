package tech.mattico.melay.view.compose

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

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.RelativeLayout

import com.uber.autodispose.android.scope
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.contact_chip_detailed.view.*
import tech.mattico.melay.R
import tech.mattico.melay.injection.appComponent
import tech.mattico.melay.model.Contact
import tech.mattico.melay.utils.Colors
import tech.mattico.melay.utils.extensions.setBackgroundTint
import javax.inject.Inject


class DetailedChipView(context: Context) : RelativeLayout(context) {

    @Inject lateinit var colors: Colors

    init {
        View.inflate(context, R.layout.contact_chip_detailed, this)
        appComponent.inject(this)

        setOnClickListener { hide() }

        visibility = View.GONE

        isFocusable = true
        isFocusableInTouchMode = true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        colors.theme
                .autoDisposable(scope())
                .subscribe { color -> card.setBackgroundTint(color) }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setContact(contact: Contact) {
        avatar.setContact(contact)
        name.text = contact.name
        info.text = contact.numbers.map { it.address }.toString()
    }

    fun show() {
        startAnimation(AlphaAnimation(0f, 1f).apply { duration = 200 })

        visibility = View.VISIBLE
        requestFocus()
        isClickable = true
    }

    fun hide() {
        startAnimation(AlphaAnimation(1f, 0f).apply { duration = 200 })

        visibility = View.GONE
        clearFocus()
        isClickable = false
    }

    fun setOnDeleteListener(listener: (View) -> Unit) {
        delete.setOnClickListener(listener)
    }

}
