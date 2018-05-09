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
package tech.mattico.melay.view.main

import io.reactivex.Observable
import tech.mattico.melay.view.MenuItem
import tech.mattico.melay.view.base.MelayView

interface MainView : MelayView<MainState> {

    val queryChangedIntent: Observable<CharSequence>
    val composeIntent: Observable<Unit>
    val drawerOpenIntent: Observable<Boolean>
    val homeIntent: Observable<*>
    val drawerItemIntent: Observable<DrawerItem>
    val optionsItemIntent: Observable<Int>
    val dismissRatingIntent: Observable<*>
    val rateIntent: Observable<*>
    val conversationsSelectedIntent: Observable<List<Long>>
    val confirmDeleteIntent: Observable<Unit>
    val swipeConversationIntent: Observable<Long>
    val undoSwipeConversationIntent: Observable<Unit>
    val backPressedIntent: Observable<Unit>

    fun clearSearch()
    fun clearSelection()
    fun showDeleteDialog()

}

enum class DrawerItem { INBOX, ARCHIVED, SCHEDULED, BLOCKED, SETTINGS, PLUS, HELP }