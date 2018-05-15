package tech.mattico.melay.interactor
import io.reactivex.Flowable
import tech.mattico.melay.repository.IMessageRepository
import javax.inject.Inject

class CancelDelayedMessage @Inject constructor(private val messageRepo: IMessageRepository) : Interactor<Long>() {

    override fun buildObservable(params: Long): Flowable<*> {
        return Flowable.just(params)
                .doOnNext { id -> messageRepo.cancelDelayedSms(id) }
                .doOnNext { id -> messageRepo.deleteMessages(id) }
    }

}