package tech.mattico.melay.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import tech.mattico.melay.utils.extensions.dpToPx
import tech.mattico.melay.utils.extensions.setPadding
import tech.mattico.melay.injection.appComponent
import javax.inject.Inject

/**
 * Wrapper around AlertDialog which makes it easier to display lists that use our UI
 */
class MelayDialog @Inject constructor(private val context: Context, val adapter: MenuItemAdapter) {

    var title: String? = null

    init {
        appComponent.inject(this)
    }

    fun show(activity: Activity) {
        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.setPadding(top = 8.dpToPx(context), bottom = 8.dpToPx(context))

        val dialog = AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(recyclerView)
                .create()

        val clicks = adapter.menuItemClicks
                .subscribe { dialog.dismiss() }

        dialog.setOnDismissListener {
            clicks.dispose()
        }

        dialog.show()
    }

    fun setTitle(@StringRes title: Int) {
        this.title = context.getString(title)
    }

}