package tech.mattico.melay.receiver

import android.content.BroadcastReceiver
import tech.mattico.melay.interactor.RetrySending
import tech.mattico.melay.repository.IMessageRepository
import javax.inject.Inject
import android.content.Context
import android.content.Intent
import tech.mattico.melay.injection.appComponent

class SendSmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var messageRepo: IMessageRepository
    @Inject lateinit var retrySending: RetrySending

    override fun onReceive(context: Context, intent: Intent) {
        appComponent.inject(this)

        val id = intent.getLongExtra("id", 0L)
        messageRepo.getMessage(id)?.let { message ->
            val result = goAsync()
            retrySending.execute(message) { result.finish() }
        }
    }

}