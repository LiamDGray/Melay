package tech.mattico.melay.interactor

import io.reactivex.Flowable
import javax.inject.Inject
import tech.mattico.melay.repository.IMessageRepository

class DeleteConversations @Inject constructor(
        private val messageRepo: IMessageRepository,
        private val updateBadge: UpdateBadge
) : Interactor<List<Long>>() {

    override fun buildObservable(params: List<Long>): Flowable<*> {
        return Flowable.just(params.toLongArray())
                .doOnNext { threadIds -> messageRepo.deleteConversations(*threadIds) }
                .flatMap { updateBadge.buildObservable(Unit) } // Update the badge
    }

}