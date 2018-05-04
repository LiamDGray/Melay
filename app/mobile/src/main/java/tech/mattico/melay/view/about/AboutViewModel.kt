package tech.mattico.melay.view.about

import tech.mattico.melay.R
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import tech.mattico.melay.injection.appComponent
import tech.mattico.melay.view.Navigator
import tech.mattico.melay.view.base.MelayViewModel
import javax.inject.Inject

class AboutViewModel : MelayViewModel<AboutView, Unit>(Unit) {

    @Inject lateinit var navigator: Navigator

    init {
        appComponent.inject(this)
    }

    override fun bindView(view: AboutView) {
        super.bindView(view)

        view.preferenceClickIntent
                .autoDisposable(view.scope())
                .subscribe { preference ->
                    when(preference.id) {
                        R.id.developer -> navigator.showDeveloper()

                        R.id.source -> navigator.showSourceCode()

                        R.id.changelog -> navigator.showChangelog()

                        R.id.contact -> navigator.showSupport()

                        R.id.license -> navigator.showLicense()
                    }
                }
    }

}