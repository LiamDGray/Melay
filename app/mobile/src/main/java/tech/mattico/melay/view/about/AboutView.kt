package tech.mattico.melay.view.about

import io.reactivex.subjects.Subject
import tech.mattico.melay.view.base.MelayView
import tech.mattico.melay.view.widget.PreferenceView

interface AboutView : MelayView<Unit> {

    val preferenceClickIntent: Subject<PreferenceView>

}