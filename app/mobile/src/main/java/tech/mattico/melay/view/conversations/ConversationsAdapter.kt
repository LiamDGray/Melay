/*
 * Copyright (C) 2017 Moez Bhatti <moez.bhatti@gmail.com>
 *
 * This file is part of MelaySMS.
 *
 * MelaySMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MelaySMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MelaySMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package tech.mattico.melay.conversations

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.longClicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import tech.mattico.melay.utils.Colors
import tech.mattico.melay.utils.DateFormatter
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.conversation_list_item.view.*
import tech.mattico.melay.R
import tech.mattico.melay.model.Conversation
import tech.mattico.melay.view.base.FlowableAdapter
import tech.mattico.melay.view.base.MelayViewHolder
import javax.inject.Inject
import tech.mattico.melay.view.Navigator

class ConversationsAdapter @Inject constructor(
        private val context: Context,
        private val colors: Colors,
        private val dateFormatter: DateFormatter,
        private val navigator: Navigator
) : FlowableAdapter<Conversation>() {

    private val disposables = CompositeDisposable()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MelayViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.conversation_list_item, parent, false)

        disposables += colors.ripple
                .subscribe { res -> view.setBackgroundResource(res) }

        if (viewType == 1) {
            view.title.setTypeface(view.title.typeface, Typeface.BOLD)

            view.snippet.setTypeface(view.snippet.typeface, Typeface.BOLD)
            view.snippet.textColorObservable = colors.textPrimary
            view.snippet.maxLines = 5

            view.date.setTypeface(view.date.typeface, Typeface.BOLD)
            view.date.textColorObservable = colors.textPrimary
        }

        return MelayViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MelayViewHolder, position: Int) {
        val conversation = getItem(position)
        val view = viewHolder.itemView

        view.clicks().subscribe {
            when (toggleSelection(conversation.id, false)) {
                true -> view.isSelected = isSelected(conversation.id)
                false -> navigator.showConversation(conversation.id)
            }
        }
        view.longClicks().subscribe {
            toggleSelection(conversation.id)
            view.isSelected = isSelected(conversation.id)
        }

        view.isSelected = isSelected(conversation.id)

        view.avatars.contacts = conversation.recipients
        view.title.text = conversation.getTitle()
        view.date.text = dateFormatter.getConversationTimestamp(conversation.date)
        view.snippet.text = when (conversation.me) {
            true -> context.getString(R.string.main_sender_you, conversation.snippet)
            false -> conversation.snippet
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposables.clear()
    }

    override fun getItemId(index: Int): Long {
        return getItem(index).id
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).read) 0 else 1
    }

    override fun areItemsTheSame(old: Conversation, new: Conversation): Boolean {
        return old.id == new.id
    }
}