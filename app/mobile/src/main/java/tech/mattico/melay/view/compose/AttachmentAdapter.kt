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
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.attachment_list_item.view.*
import tech.mattico.melay.R
import tech.mattico.melay.utils.Colors
import tech.mattico.melay.view.base.MelayAdapter
import tech.mattico.melay.view.base.MelayViewHolder
import javax.inject.Inject

class AttachmentAdapter @Inject constructor(
        private val context: Context,
        private val colors: Colors
) : MelayAdapter<Uri>() {

    val attachmentDeleted: Subject<Uri> = PublishSubject.create()

    private val disposables = CompositeDisposable()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MelayViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.attachment_list_item, parent, false)

        view.thumbnailBounds.clipToOutline = true

        disposables += colors.bubble
                .subscribe { color -> view.detach.setBackgroundTint(color) }

        disposables += colors.textSecondary
                .subscribe { color -> view.detach.setTint(color) }

        return MelayViewHolder(view)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposables.clear()
    }

    override fun onBindViewHolder(holder: MelayViewHolder, position: Int) {
        val uri = getItem(position)
        val view = holder.itemView

        view.clicks().subscribe {
            attachmentDeleted.onNext(uri)
        }

        Glide.with(context).load(uri).into(view.thumbnail)
    }

}