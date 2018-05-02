package tech.mattico.melay.repository

import android.net.Uri

interface ImageRepository {

    fun saveImage(uri: Uri)

}