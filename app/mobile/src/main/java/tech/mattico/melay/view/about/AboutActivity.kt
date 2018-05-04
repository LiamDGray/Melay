package tech.mattico.melay.view.about

import android.os.Bundle
import com.jakewharton.rxbinding2.view.clicks
import tech.mattico.melay.BuildConfig
import tech.mattico.melay.R
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.about_activity.*
import tech.mattico.melay.injection.appComponent
import tech.mattico.melay.view.base.MelayThemedActivity
import tech.mattico.melay.view.widget.PreferenceView

class AboutActivity : MelayThemedActivity<AboutViewModel>(), AboutView {

    override val viewModelClass = AboutViewModel::class
    override val preferenceClickIntent: Subject<PreferenceView> = PublishSubject.create()

    init {
        appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_activity)
        setTitle(R.string.about_title)
        showBackButton(true)
        viewModel.bindView(this)

        version.summary = BuildConfig.VERSION_NAME

        colors.background
                .autoDisposable(scope())
                .subscribe { color -> window.decorView.setBackgroundColor(color) }

        // Listen to clicks for all of the preferences
        (0 until preferences.childCount)
                .map { index -> preferences.getChildAt(index) }
                .mapNotNull { view -> view as? PreferenceView }
                .forEach { preference ->
                    preference.clicks().map { preference }.subscribe(preferenceClickIntent)
                }
    }

    override fun render(state: Unit) {
        // No special rendering required
    }


}