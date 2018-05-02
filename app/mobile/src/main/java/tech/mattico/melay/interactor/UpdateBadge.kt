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
package tech.mattico.melay.interactor

import io.reactivex.Flowable
import tech.mattico.melay.manager.WidgetManager
import tech.mattico.melay.repository.IMessageRepository
import javax.inject.Inject

class UpdateBadge @Inject constructor(
        private val messageRepo: IMessageRepository,
        private val widgetManager: WidgetManager
) : Interactor<Unit>() {

    override fun buildObservable(params: Unit): Flowable<*> {
        return Flowable.just(params)
                .map { messageRepo.getUnreadCount() }
                .map { count -> count.toInt() }
                .doOnNext { count -> widgetManager.updateUnreadCount(count) }
    }

}