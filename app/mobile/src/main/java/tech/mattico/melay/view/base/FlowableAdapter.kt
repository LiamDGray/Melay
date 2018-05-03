package tech.mattico.melay.view.base
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

import android.support.annotation.CallSuper
import android.support.v7.widget.RecyclerView
import android.view.View
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import tech.mattico.melay.utils.extensions.setVisible

/**
 * Base RecyclerView.Adapter that provides some convenience when creating a new Adapter, such as
 * data list handing and item animations
 */
abstract class FlowableAdapter<T> : MelayAdapter<T>() {

    /**
     * This view can be set, and the adapter will automatically control the visibility of this view
     * based on the data
     */
    var emptyView: View? = null
        set(value) {
            field = value
            value?.setVisible(false)
        }

    var flowable: Flowable<List<T>>? = null
        set(value) {
            if (field === value) return

            field = value

            // Stop listening for updates on the old flowable
            dispose()

            // If we're attached to any RecyclerViews, then subscribe to updates
            if (recyclerViews.isNotEmpty()) {
                subscribe()
            }
        }

    private val recyclerViews = ArrayList<RecyclerView>()
    private var disposable: Disposable? = null

    @CallSuper
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {

        // If this is the first RecyclerView to be attached, then start listening to updates
        if (recyclerViews.isEmpty() || disposable == null) {
            subscribe()
        }

        recyclerViews.add(recyclerView)
    }

    @CallSuper
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerViews.remove(recyclerView)

        // If no more RecyclerViews are attached, stop listening to updates
        if (recyclerViews.isEmpty()) {
            dispose()
        }
    }

    private fun subscribe() {
        disposable = flowable?.subscribe {
            data = it
            emptyView?.setVisible(data.isEmpty())
        }
    }

    private fun dispose() {
        disposable?.dispose()
    }

}