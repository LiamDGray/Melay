package tech.mattico.melay.view.main
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

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import com.jakewharton.rxbinding2.support.v4.widget.drawerOpen
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges

import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.drawer_view.*
import kotlinx.android.synthetic.main.main_activity.*
import tech.mattico.melay.R
import tech.mattico.melay.injection.appComponent
import tech.mattico.melay.utils.extensions.*
import tech.mattico.melay.view.MelayDialog
import tech.mattico.melay.view.Navigator
import tech.mattico.melay.view.base.MelayThemedActivity
import tech.mattico.melay.view.compose.ConversationsAdapter
import tech.mattico.melay.view.conversations.ConversationItemTouchCallback
import tech.mattico.melay.view.widget.MelayEditText
import javax.inject.Inject

class MainActivity : MelayThemedActivity<MainViewModel>(), MainView {

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var conversationsAdapter: ConversationsAdapter
    @Inject lateinit var dialog: MelayDialog
    @Inject lateinit var itemTouchCallback: ConversationItemTouchCallback

    override val viewModelClass = MainViewModel::class
    override val queryChangedIntent by lazy { toolbarSearch.textChanges() }
    override val queryCancelledIntent: PublishSubject<Unit> = PublishSubject.create()
    override val composeIntent by lazy { compose.clicks() }
    override val drawerOpenIntent: Observable<Boolean> by lazy {
        drawerLayout
                .drawerOpen(Gravity.START)
                .doOnNext { dismissKeyboard() }
    }
    override val drawerItemIntent: Observable<DrawerItem> by lazy {
        Observable.merge(listOf(
                inbox.clicks().map { DrawerItem.INBOX },
                archived.clicks().map { DrawerItem.ARCHIVED },
                scheduled.clicks().map { DrawerItem.SCHEDULED },
                settings.clicks().map { DrawerItem.SETTINGS },
                plus.clicks().map { DrawerItem.PLUS },
                help.clicks().map { DrawerItem.HELP }))
    }
    override val dismissRatingIntent by lazy { rateDismiss.clicks() }
    override val rateIntent by lazy { rateOkay.clicks() }
    override val conversationClickIntent by lazy { conversationsAdapter.clicks }
    override val conversationLongClickIntent by lazy { conversationsAdapter.longClicks }
    override val conversationMenuItemIntent by lazy { dialog.adapter.menuItemClicks }
    override val confirmDeleteIntent: Subject<Unit> = PublishSubject.create()
    override val swipeConversationIntent by lazy { itemTouchCallback.swipes }
    override val undoSwipeConversationIntent: Subject<Unit> = PublishSubject.create()

    private val itemTouchHelper by lazy { ItemTouchHelper(itemTouchCallback) }
    private val archiveSnackbar by lazy {
        Snackbar.make(drawerLayout, R.string.toast_archived, Snackbar.LENGTH_INDEFINITE).apply {
            setAction(R.string.button_undo, { undoSwipeConversationIntent.onNext(Unit) })
        }
    }

    init {
        appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        viewModel.bindView(this)
        toolbarSearch.setHint(R.string.title_conversations)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0).apply { syncState() }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Don't allow clicks to pass through the drawer layout
        drawer.clicks().subscribe()

        scheduled.isEnabled = false

        colors.background
                .doOnNext { color -> window.decorView.setBackgroundColor(color) }
                .doOnNext { color -> drawer.setBackgroundColor(color) }
                .autoDisposable(scope())
                .subscribe()

        val states = arrayOf(
                intArrayOf(android.R.attr.state_selected),
                intArrayOf(-android.R.attr.state_selected))

        val rowBackground = { separator: Int ->
            StateListDrawable().apply {
                addState(intArrayOf(android.R.attr.state_selected), ColorDrawable(separator))
                addState(intArrayOf(-android.R.attr.state_selected), getDrawable(R.drawable.ripple))
                mutate()
            }
        }

        // Set the theme color tint to the progressbar and FAB
        colors.theme
                .doOnNext { color -> syncingProgress.indeterminateTintList = ColorStateList.valueOf(color) }
                .doOnNext { color -> itemTouchCallback.color = color }
                .doOnNext { color -> rateIcon.setTint(color) }
                .doOnNext { color -> compose.setBackgroundTint(color) }
                .autoDisposable(scope())
                .subscribe()

        // Set the FAB compose icon color
        colors.textPrimaryOnTheme
                .doOnNext { color -> compose.setTint(color) }
                .doOnNext { color -> itemTouchCallback.iconColor = color }
                .autoDisposable(scope())
                .subscribe()

        // Set the hamburger icon color
        colors.textSecondary
                .autoDisposable(scope())
                .subscribe { color -> toggle.drawerArrowDrawable.color = color }

        // Set the color for the drawer icons
        Observables
                .combineLatest(colors.theme, colors.textSecondary, { theme, textSecondary ->
                    ColorStateList(states, intArrayOf(theme, textSecondary))
                })
                .doOnNext { tintList -> inboxIcon.imageTintList = tintList }
                .doOnNext { tintList -> archivedIcon.imageTintList = tintList }
                .doOnNext { tintList -> scheduledIcon.imageTintList = tintList }
                .doOnNext { tintList -> settingsIcon.imageTintList = tintList }
                .doOnNext { tintList -> plusIcon.imageTintList = tintList }
                .doOnNext { tintList -> helpIcon.imageTintList = tintList }
                .autoDisposable(scope())
                .subscribe()

        // Set the background highlight for the drawer options
        colors.separator
                .doOnNext { color -> inbox.background = rowBackground(color) }
                .doOnNext { color -> archived.background = rowBackground(color) }
                .doOnNext { color -> scheduled.background = rowBackground(color) }
                .doOnNext { color -> rateLayout.setBackgroundTint(color) }
                .autoDisposable(scope())
                .subscribe()

        conversationsAdapter.autoScrollToStart(recyclerView)
        conversationsAdapter.emptyView = empty
    }

    override fun render(state: MainState) {
        toolbarSearch.isEnabled = state.page is Inbox
        toolbarSearch.setTextSize(if (state.page is Inbox) MelayEditText.SIZE_PRIMARY else MelayEditText.SIZE_TOOLBAR)

        toolbar.menu.findItem(R.id.clear)?.run {
            isVisible = state.page is Inbox && state.page.showClearButton
        }

        syncing.setVisible(state.syncing)
        synced.setVisible(!state.syncing)
        rateLayout.setVisible(state.showRating)

        when (state.page) {
            is Inbox -> {
                if (!inbox.isSelected) toolbarSearch.text = null
                if (recyclerView.adapter !== conversationsAdapter) recyclerView.adapter = conversationsAdapter
                conversationsAdapter.flowable = state.page.data
                itemTouchHelper.attachToRecyclerView(recyclerView)
                empty.setText(when (state.page.showClearButton) {
                    true -> R.string.inbox_search_empty_text
                    false -> R.string.inbox_empty_text
                })
                compose.setVisible(true)
            }

            is Archived -> {
                if (!archived.isSelected) toolbarSearch.setText(R.string.title_archived)
                if (recyclerView.adapter !== conversationsAdapter) recyclerView.adapter = conversationsAdapter
                conversationsAdapter.flowable = state.page.data
                itemTouchHelper.attachToRecyclerView(null)
                empty.setText(R.string.archived_empty_text)
                compose.setVisible(true)
            }

            is Scheduled -> {
                if (!scheduled.isSelected) toolbarSearch.setText(R.string.title_scheduled)
                recyclerView.adapter = null
                itemTouchHelper.attachToRecyclerView(null)
                empty.setText(R.string.scheduled_empty_text)
                compose.setVisible(false)
            }
        }

        when (state.page is Inbox && state.page.showArchivedSnackbar) {
            true -> archiveSnackbar.show()
            false -> archiveSnackbar.dismiss()
        }

        inbox.isSelected = state.page is Inbox
        inboxIcon.isSelected = state.page is Inbox
        archived.isSelected = state.page is Archived
        archivedIcon.isSelected = state.page is Archived
        scheduled.isSelected = state.page is Scheduled
        scheduledIcon.isSelected = state.page is Scheduled

        if (drawerLayout.isDrawerOpen(Gravity.START) && !state.drawerOpen) drawerLayout.closeDrawer(Gravity.START)
        else if (!drawerLayout.isDrawerVisible(Gravity.START) && state.drawerOpen) drawerLayout.openDrawer(Gravity.START)
    }

    override fun clearSearch() {
        dismissKeyboard()
        toolbarSearch.text = null
    }

    override fun showDialog(menuItems: List<tech.mattico.melay.view.MenuItem>) {
        dialog.adapter.data = menuItems
        dialog.show(this)
    }

    override fun showDeleteDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton(R.string.button_delete, { _, _ -> confirmDeleteIntent.onNext(Unit) })
                .setNegativeButton(R.string.button_cancel, null)
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> queryCancelledIntent.onNext(Unit)
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

}
