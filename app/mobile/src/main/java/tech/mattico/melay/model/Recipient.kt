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
package tech.mattico.melay.model

import android.os.Build
import android.telephony.PhoneNumberUtils
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Recipient : RealmObject() {

    @PrimaryKey var id: Long = 0
    var address: String = ""
    var contact: Contact? = null
    var lastUpdate: Long = 0

    /**
     * Return a string that can be displayed to represent the name of this contact
     */
    fun getDisplayName(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        contact?.name?.takeIf { it.isNotBlank() }
                ?: PhoneNumberUtils.formatNumber(address, Locale.getDefault().country)
                ?: address
    } else {
        TODO("VERSION.SDK_INT < LOLLIPOP")
    }

}