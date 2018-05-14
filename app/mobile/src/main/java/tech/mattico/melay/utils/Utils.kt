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
package tech.mattico.melay.utils

import android.content.Context
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager


fun <T> tryOrNull(body: () -> T?): T? {
    return try {
        body()
    } catch (e: Exception) {
        null
    }
}

/**
 * Checks if there is Internet connection or data connection on the device.
 *
 * @param context - The activity calling this method.
 * @return boolean
 */
fun isConnected(context: Context): Boolean {

    val connectivity = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkInfo = connectivity.activeNetworkInfo

    return networkInfo != null && networkInfo.isConnectedOrConnecting

}