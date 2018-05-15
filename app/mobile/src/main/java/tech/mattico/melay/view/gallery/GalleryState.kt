package tech.mattico.melay.view.gallery

import android.net.Uri

data class GalleryState(
        val navigationVisible: Boolean = true,
        val title: String = "",
        val imageUri: Uri? = null
)