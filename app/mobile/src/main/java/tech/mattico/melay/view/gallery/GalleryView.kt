package tech.mattico.melay.view.gallery

import io.reactivex.Observable
import tech.mattico.melay.view.base.MelayView

interface GalleryView : MelayView<GalleryState> {

    val screenTouchedIntent: Observable<Unit>
    val optionsItemSelectedIntent: Observable<Int>

}