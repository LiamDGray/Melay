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
import tech.mattico.melay.manager.PermissionManager
import tech.mattico.melay.repository.ISyncRepository
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

open class PartialSync @Inject constructor(
        private val syncManager: ISyncRepository,
        private val permissions: PermissionManager
) : Interactor<Unit>() {

    override fun buildObservable(params: Unit): Flowable<Long> {
        return Flowable.just(System.currentTimeMillis())
                .skipWhile { !permissions.hasSmsAndContacts() }
                .doOnNext { syncManager.syncMessages() }
                .map { startTime -> System.currentTimeMillis() - startTime }
                .map { elapsed -> TimeUnit.MILLISECONDS.toSeconds(elapsed) }
                .doOnNext { seconds -> Timber.v("Completed sync in $seconds seconds") }
    }

}