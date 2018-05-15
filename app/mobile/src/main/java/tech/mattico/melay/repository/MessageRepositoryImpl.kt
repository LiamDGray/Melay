/*
 * Copyright (C) 2017 Moez Bhatti <moez.bhatti@gmail.com>
 *
 * This file is part of Melay SMS.
 *
 * Melay SMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Melay SMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Melay SMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package tech.mattico.melay.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.Telephony
import android.telephony.PhoneNumberUtils
import android.telephony.SmsManager
import com.klinker.android.send_message.BroadcastUtils
import com.klinker.android.send_message.StripAccents
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import tech.mattico.melay.manager.KeyManager
import tech.mattico.melay.mapper.CursorToConversation
import tech.mattico.melay.mapper.CursorToRecipient
import tech.mattico.melay.model.Contact
import tech.mattico.melay.model.Conversation
import tech.mattico.melay.model.Message
import tech.mattico.melay.model.MmsPart
import tech.mattico.melay.utils.MessageUtils
import tech.mattico.melay.utils.Preferences
import tech.mattico.melay.utils.extensions.anyOf
import tech.mattico.melay.utils.extensions.asMaybe
import tech.mattico.melay.utils.extensions.insertOrUpdate
import tech.mattico.melay.utils.extensions.map
import javax.inject.Inject
import javax.inject.Singleton

/*
 * Copyright (C) 2017 Moez Bhatti <moez.bhatti@gmail.com>
 *
 * This file is part of QKSMS.
 *
 * QKSMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QKSMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QKSMS.  If not, see <http://www.gnu.org/licenses/>.
 */
@Singleton
class MessageRepositoryImpl @Inject constructor(
        private val context: Context,
        private val messageIds: KeyManager,
        private val cursorToConversation: CursorToConversation,
        private val cursorToRecipient: CursorToRecipient,
        private val prefs: Preferences) : IMessageRepository {

    /**
     * Gets the unread conversations
     */
    override fun getUnreadConversations(): Flowable<List<Conversation>> {
        val realm = Realm.getDefaultInstance()
        return realm
                .where(Conversation::class.java)
                .notEqualTo("id", 0L)
                .greaterThan("count", 0)
                .equalTo("archived", false)
                .equalTo("read",false)
                .equalTo("blocked", false)
                .isNotEmpty("recipients")
                .sort("date", Sort.DESCENDING)
                .findAllAsync()
                .asFlowable()
                .filter { it.isLoaded }
                .filter { it.isValid }
                .map { conversations -> realm.copyFromRealm(conversations) }
    }


    override fun getUnrespondedConversations(unread: Boolean): Flowable<List<Conversation>> {
        val realm = Realm.getDefaultInstance()
        return realm
                .where(Conversation::class.java)
                .notEqualTo("id", 0L)
                .greaterThan("count", 0)
                .equalTo("read", !unread)
                .equalTo("me", true)
                .equalTo("blocked", false)
                .isNotEmpty("recipients")
                .sort("date", Sort.DESCENDING)
                .findAllAsync()
                .asFlowable()
                .filter { it.isLoaded }
                .filter { it.isValid }
                .map { conversations -> realm.copyFromRealm(conversations) }
    }

    override fun getConversations(archived: Boolean): Flowable<List<Conversation>> {
        val realm = Realm.getDefaultInstance()
        return realm
                .where(Conversation::class.java)
                .notEqualTo("id", 0L)
                .greaterThan("count", 0)
                .equalTo("archived", archived)
                .equalTo("blocked", false)
                .isNotEmpty("recipients")
                .sort("date", Sort.DESCENDING)
                .findAllAsync()
                .asFlowable()
                .filter { it.isLoaded }
                .filter { it.isValid }
                .map { conversations -> realm.copyFromRealm(conversations) }
    }

    override fun getConversationsSnapshot(): List<Conversation> {
        val realm = Realm.getDefaultInstance()
        return realm.copyFromRealm(realm.where(Conversation::class.java)
                .notEqualTo("id", 0L)
                .greaterThan("count", 0)
                .equalTo("archived", false)
                .equalTo("blocked", false)
                .isNotEmpty("recipients")
                .sort("date", Sort.DESCENDING)
                .findAll())
    }

    override fun getBlockedConversations(): RealmResults<Conversation> {
        return Realm.getDefaultInstance()
                .where(Conversation::class.java)
                .equalTo("blocked", true)
                .findAllAsync()
    }

    override fun getConversationAsync(threadId: Long): Conversation {
        return Realm.getDefaultInstance()
                .where(Conversation::class.java)
                .equalTo("id", threadId)
                .findFirstAsync()
    }

    override fun getConversation(threadId: Long): Conversation? {
        return Realm.getDefaultInstance()
                .where(Conversation::class.java)
                .equalTo("id", threadId)
                .findFirst()
    }

    override fun getOrCreateConversation(threadId: Long): Conversation? {
        return getConversation(threadId) ?: getConversationFromCp(threadId)
    }

    override fun getOrCreateConversation(address: String): Maybe<Conversation> {
        return getOrCreateConversation(listOf(address))
    }

    override fun getOrCreateConversation(addresses: List<String>): Maybe<Conversation> {
        return Maybe.just(addresses)
                .map { recipients ->
                    recipients.map { address ->
                        when (MessageUtils.isEmailAddress(address)) {
                            true -> MessageUtils.extractAddrSpec(address)
                            false -> address
                        }
                    }
                }
                .map { recipients ->
                    Uri.parse("content://mms-sms/threadID").buildUpon().apply {
                        recipients.forEach { recipient -> appendQueryParameter("recipient", recipient) }
                    }
                }
                .flatMap { uriBuilder ->
                    context.contentResolver.query(uriBuilder.build(), arrayOf(BaseColumns._ID), null, null, null).asMaybe()
                }
                .map { cursor -> cursor.getLong(0) }
                .filter { threadId -> threadId != 0L }
                .map { threadId ->
                    var conversation = getConversation(threadId)
                    if (conversation != null) conversation = Realm.getDefaultInstance().copyFromRealm(conversation)

                    conversation ?: getConversationFromCp(threadId)?.apply { insertOrUpdate() } ?: Conversation()
                }
                .onErrorReturn { Conversation() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun saveDraft(threadId: Long, draft: String) {
        Realm.getDefaultInstance().use { realm ->
            val conversation = realm.where(Conversation::class.java)
                    .equalTo("id", threadId)
                    .findFirst()

            conversation?.let {
                realm.executeTransaction {
                    conversation.draft = draft
                }
            }
        }
    }

    override fun getMessages(threadId: Long): RealmResults<Message> {
        return Realm.getDefaultInstance()
                .where(Message::class.java)
                .equalTo("threadId", threadId)
                .sort("date")
                .findAllAsync()
    }

    override fun getMessage(id: Long): Message? {
        return Realm.getDefaultInstance()
                .where(Message::class.java)
                .equalTo("id", id)
                .findFirst()
    }

    override fun getMessageForPart(id: Long): Message? {
        return Realm.getDefaultInstance()
                .where(Message::class.java)
                .equalTo("parts.id", id)
                .findFirst()
    }

    override fun getUnreadCount(): Long {
        return Realm.getDefaultInstance()
                .where(Conversation::class.java)
                .equalTo("archived", false)
                .equalTo("blocked", false)
                .equalTo("read", false)
                .count()
    }

    override fun getPart(id: Long): MmsPart? {
        return Realm.getDefaultInstance()
                .where(MmsPart::class.java)
                .equalTo("id", id)
                .findFirst()
    }

    override fun getPartsForConversation(threadId: Long): RealmResults<MmsPart> {
        return Realm.getDefaultInstance()
                .where(MmsPart::class.java)
                .equalTo("messages.threadId", threadId)
                .contains("type", "image/")
                .sort("id", Sort.DESCENDING)
                .findAllAsync()
    }

    /**
     * Retrieves the list of messages which should be shown in the notification
     * for a given conversation
     */
    override fun getUnreadUnseenMessages(threadId: Long): RealmResults<Message> {
        return Realm.getDefaultInstance()
                .also { it.refresh() }
                .where(Message::class.java)
                .equalTo("seen", false)
                .equalTo("read", false)
                .equalTo("threadId", threadId)
                .sort("date")
                .findAll()
    }

    override fun getUnreadMessages(threadId: Long): RealmResults<Message> {
        return Realm.getDefaultInstance()
                .where(Message::class.java)
                .equalTo("read", false)
                .equalTo("threadId", threadId)
                .sort("date")
                .findAll()
    }

    override fun updateConversations(vararg threadIds: Long) {
        Realm.getDefaultInstance().use { realm ->
            realm.refresh()

            threadIds.forEach { threadId ->
                val conversation = realm
                        .where(Conversation::class.java)
                        .equalTo("id", threadId)
                        .findFirst() ?: return

                val messages = realm
                        .where(Message::class.java)
                        .equalTo("threadId", threadId)
                        .sort("date", Sort.DESCENDING)
                        .findAll()

                val message = messages.firstOrNull()

                realm.executeTransaction {
                    conversation.count = messages.size
                    conversation.date = message?.date ?: 0
                    conversation.snippet = message?.getSummary() ?: ""
                    conversation.read = message?.read ?: true
                    conversation.me = message?.isMe() ?: false
                }
            }
        }
    }

    override fun markArchived(vararg threadIds: Long) {
        Realm.getDefaultInstance().use { realm ->
            val conversations = realm.where(Conversation::class.java)
                    .anyOf("id", threadIds)
                    .findAll()

            realm.executeTransaction {
                conversations.forEach { it.archived = true }
            }
        }
    }

    override fun markUnarchived(vararg threadIds: Long) {
        Realm.getDefaultInstance().use { realm ->
            val conversations = realm.where(Conversation::class.java)
                    .anyOf("id", threadIds)
                    .findAll()

            realm.executeTransaction {
                conversations.forEach { it.archived = false }
            }
        }
    }

    override fun markBlocked(vararg threadIds: Long) {
        Realm.getDefaultInstance().use { realm ->
            val conversations = realm.where(Conversation::class.java)
                    .anyOf("id", threadIds)
                    .findAll()

            realm.executeTransaction {
                conversations.forEach { it.blocked = true }
            }
        }
    }

    override fun markUnblocked(vararg threadIds: Long) {
        Realm.getDefaultInstance().use { realm ->
            val conversations = realm.where(Conversation::class.java)
                    .anyOf("id", threadIds)
                    .equalTo("archived", false)
                    .findAll()

            realm.executeTransaction {
                conversations.forEach { it.blocked = false }
            }
        }
    }

    override fun markAllSeen() {
        val realm = Realm.getDefaultInstance()
        val messages = realm.where(Message::class.java).equalTo("seen", false).findAll()
        realm.executeTransaction { messages.forEach { message -> message.seen = true } }
        realm.close()
    }

    override fun markSeen(threadId: Long) {
        val realm = Realm.getDefaultInstance()
        val messages = realm.where(Message::class.java)
                .equalTo("threadId", threadId)
                .equalTo("seen", false)
                .findAll()

        realm.executeTransaction {
            messages.forEach { message ->
                message.seen = true
            }
        }
        realm.close()
    }

    override fun markRead(threadId: Long) {
        Realm.getDefaultInstance()?.use { realm ->
            val messages = realm.where(Message::class.java)
                    .equalTo("threadId", threadId)
                    .beginGroup()
                    .equalTo("read", false)
                    .or()
                    .equalTo("seen", false)
                    .endGroup()
                    .findAll()

            realm.executeTransaction {
                messages.forEach { message ->
                    message.seen = true
                    message.read = true
                }
            }
        }

        val values = ContentValues()
        values.put(Telephony.Sms.SEEN, true)
        values.put(Telephony.Sms.READ, true)

        try {
            val uri = ContentUris.withAppendedId(Telephony.MmsSms.CONTENT_CONVERSATIONS_URI, threadId)
            context.contentResolver.update(uri, values, "${Telephony.Sms.READ} = 0", null)
        } catch (exception: Exception) {

        }
    }

    override fun sendSmsAndPersist(threadId: Long, address: String, body: String) {
        if (prefs.sendDelay.get() != Preferences.SEND_DELAY_NONE) {
            val delay = when (prefs.sendDelay.get()) {
                Preferences.SEND_DELAY_SHORT -> 3000
                Preferences.SEND_DELAY_MEDIUM -> 5000
                Preferences.SEND_DELAY_LONG -> 10000
                else -> 0
            }

            val sendTime = System.currentTimeMillis() + delay
            val message = insertSentSms(threadId, address, body, sendTime)

            val intent = getIntentForDelayedSms(message.id)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, sendTime, intent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, sendTime, intent)
            }
        } else {
            val message = insertSentSms(threadId, address, body, System.currentTimeMillis())
            sendSms(message)
        }
    }

    override fun sendSms(message: Message) {
        val smsManager = SmsManager.getDefault()

        val parts = smsManager.divideMessage(if (prefs.unicode.get()) StripAccents.stripAccents(message.body) else message.body)
                ?: arrayListOf()

        val sentIntents = parts.map {
            val action = "tech.mattico.melay.SMS_SENT"
            val intent = Intent(action).putExtra("id", message.id)
            BroadcastUtils.addClassName(context, intent, action)
            PendingIntent.getBroadcast(context, message.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val deliveredIntents = parts.map {
            val action = "tech.mattico.melay.SMS_DELIVERED"
            val intent = Intent(action).putExtra("id", message.id)
            BroadcastUtils.addClassName(context, intent, action)
            val pendingIntent = PendingIntent.getBroadcast(context, message.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
            if (prefs.delivery.get()) pendingIntent else null
        }

        smsManager.sendMultipartTextMessage(message.address, null, parts, ArrayList(sentIntents), ArrayList(deliveredIntents))
    }

    override fun cancelDelayedSms(id: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getIntentForDelayedSms(id))
    }

    private fun getIntentForDelayedSms(id: Long): PendingIntent {
        val action = "tech.mattico.melay.SEND_SMS"
        val intent = Intent(action).putExtra("id", id)
        BroadcastUtils.addClassName(context, intent, action)
        return PendingIntent.getBroadcast(context, id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun insertSentSms(threadId: Long, address: String, body: String, date: Long): Message {

        // Insert the message to Realm
        val message = Message().apply {
            this.threadId = threadId
            this.address = address
            this.body = body
            this.date = date

            id = messageIds.newId()
            boxId = Telephony.Sms.MESSAGE_TYPE_OUTBOX
            type = "sms"
            read = true
            seen = true
        }
        val realm = Realm.getDefaultInstance()
        var managedMessage: Message? = null
        realm.executeTransaction { managedMessage = realm.copyToRealmOrUpdate(message) }

        // Insert the message to the native content provider
        val values = ContentValues().apply {
            put(Telephony.Sms.ADDRESS, address)
            put(Telephony.Sms.BODY, body)
            put(Telephony.Sms.DATE, System.currentTimeMillis())
            put(Telephony.Sms.READ, true)
            put(Telephony.Sms.SEEN, true)
            put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_OUTBOX)
            put(Telephony.Sms.THREAD_ID, threadId)
        }
        val uri = context.contentResolver.insert(Telephony.Sms.CONTENT_URI, values)

        // Update the contentId after the message has been inserted to the content provider
        // The message might have been deleted by now, so only proceed if it's valid
        realm.executeTransaction { managedMessage?.takeIf { it.isValid }?.contentId = uri.lastPathSegment.toLong() }
        realm.close()

        return message
    }

    override fun insertReceivedSms(address: String, body: String, sentTime: Long): Message {

        // Insert the message to Realm
        val message = Message().apply {
            this.address = address
            this.body = body
            this.dateSent = sentTime
            this.date = System.currentTimeMillis()

            id = messageIds.newId()
            threadId = getOrCreateConversation(address).blockingGet().id
            boxId = Telephony.Sms.MESSAGE_TYPE_INBOX
            type = "sms"
        }
        val realm = Realm.getDefaultInstance()
        var managedMessage: Message? = null
        realm.executeTransaction { managedMessage = realm.copyToRealmOrUpdate(message) }

        // Insert the message to the native content provider
        val values = ContentValues().apply {
            put(Telephony.Sms.ADDRESS, address)
            put(Telephony.Sms.BODY, body)
            put(Telephony.Sms.DATE_SENT, sentTime)
        }
        val uri = context.contentResolver.insert(Telephony.Sms.Inbox.CONTENT_URI, values)

        // Update the contentId after the message has been inserted to the content provider
        realm.executeTransaction { managedMessage?.contentId = uri.lastPathSegment.toLong() }
        realm.close()

        return message
    }

    /**
     * Marks the message as sending, in case we need to retry sending it
     */
    override fun markSending(id: Long) {
        Realm.getDefaultInstance().use { realm ->
            realm.refresh()

            val message = realm.where(Message::class.java).equalTo("id", id).findFirst()
            message?.let {
                // Update the message in realm
                realm.executeTransaction {
                    message.boxId = Telephony.Sms.MESSAGE_TYPE_OUTBOX
                }

                // Update the message in the native ContentProvider
                val values = ContentValues()
                values.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_OUTBOX)
                context.contentResolver.update(message.getUri(), values, null, null)
            }
        }
    }

    override fun markSent(id: Long) {
        Realm.getDefaultInstance().use { realm ->
            realm.refresh()

            val message = realm.where(Message::class.java).equalTo("id", id).findFirst()
            message?.let {
                // Update the message in realm
                realm.executeTransaction {
                    message.boxId = Telephony.Sms.MESSAGE_TYPE_SENT
                }

                // Update the message in the native ContentProvider
                val values = ContentValues()
                values.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_SENT)
                context.contentResolver.update(message.getUri(), values, null, null)
            }
        }
    }

    override fun markFailed(id: Long, resultCode: Int) {
        Realm.getDefaultInstance().use { realm ->
            realm.refresh()

            val message = realm.where(Message::class.java).equalTo("id", id).findFirst()
            message?.let {
                // Update the message in realm
                realm.executeTransaction {
                    message.boxId = Telephony.Sms.MESSAGE_TYPE_FAILED
                    message.errorCode = resultCode
                }

                // Update the message in the native ContentProvider
                val values = ContentValues()
                values.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_FAILED)
                values.put(Telephony.Sms.ERROR_CODE, resultCode)
                context.contentResolver.update(message.getUri(), values, null, null)
            }
        }
    }

    override fun markDelivered(id: Long) {
        Realm.getDefaultInstance().use { realm ->
            realm.refresh()

            val message = realm.where(Message::class.java).equalTo("id", id).findFirst()
            message?.let {
                // Update the message in realm
                realm.executeTransaction {
                    message.deliveryStatus = Telephony.Sms.STATUS_COMPLETE
                    message.dateSent = System.currentTimeMillis()
                    message.read = true
                }

                // Update the message in the native ContentProvider
                val values = ContentValues()
                values.put(Telephony.Sms.STATUS, Telephony.Sms.STATUS_COMPLETE)
                values.put(Telephony.Sms.DATE_SENT, System.currentTimeMillis())
                values.put(Telephony.Sms.READ, true)
                context.contentResolver.update(message.getUri(), values, null, null)
            }
        }
    }

    override fun markDeliveryFailed(id: Long, resultCode: Int) {
        Realm.getDefaultInstance().use { realm ->
            realm.refresh()

            val message = realm.where(Message::class.java).equalTo("id", id).findFirst()
            message?.let {
                // Update the message in realm
                realm.executeTransaction {
                    message.deliveryStatus = Telephony.Sms.STATUS_FAILED
                    message.dateSent = System.currentTimeMillis()
                    message.read = true
                    message.errorCode = resultCode
                }

                // Update the message in the native ContentProvider
                val values = ContentValues()
                values.put(Telephony.Sms.STATUS, Telephony.Sms.STATUS_FAILED)
                values.put(Telephony.Sms.DATE_SENT, System.currentTimeMillis())
                values.put(Telephony.Sms.READ, true)
                values.put(Telephony.Sms.ERROR_CODE, resultCode)
                context.contentResolver.update(message.getUri(), values, null, null)
            }
        }
    }

    override fun deleteMessages(vararg messageIds: Long) {
        Realm.getDefaultInstance().use { realm ->
            realm.refresh()

            val messages = realm.where(Message::class.java)
                    .anyOf("id", messageIds)
                    .findAll()

            val uris = messages.map { it.getUri() }

            realm.executeTransaction { messages.deleteAllFromRealm() }

            uris.forEach { uri -> context.contentResolver.delete(uri, null, null) }
        }
    }

    override fun deleteConversations(vararg threadIds: Long) {
        Realm.getDefaultInstance().use { realm ->
            val conversation = realm.where(Conversation::class.java).anyOf("id", threadIds).findAll()
            val messages = realm.where(Message::class.java).anyOf("threadId", threadIds).findAll()

            realm.executeTransaction {
                conversation.deleteAllFromRealm()
                messages.deleteAllFromRealm()
            }
        }

        threadIds.forEach { threadId ->
            val uri = ContentUris.withAppendedId(Telephony.Threads.CONTENT_URI, threadId)
            context.contentResolver.delete(uri, null, null)
        }
    }

    private fun getConversationFromCp(threadId: Long): Conversation? {
        return cursorToConversation.getConversationCursor(threadId)
                ?.takeIf { cursor -> cursor.moveToFirst() }
                ?.use { cursor ->
                    val conversation = cursorToConversation.map(cursor)

                    val realm = Realm.getDefaultInstance()
                    val contacts = realm.copyFromRealm(realm.where(Contact::class.java).findAll())

                    val recipients = conversation.recipients
                            .map { recipient -> recipient.id }
                            .map { id -> cursorToRecipient.getRecipientCursor(id) }
                            .mapNotNull { recipientCursor ->
                                // Map the recipient cursor to a list of recipients
                                recipientCursor?.use { recipientCursor.map { cursorToRecipient.map(recipientCursor) } }
                            }
                            .flatten()
                            .map { recipient ->
                                recipient.apply {
                                    contact = contacts.firstOrNull {
                                        it.numbers.any { PhoneNumberUtils.compare(recipient.address, it.address) }
                                    }
                                }
                            }

                    conversation.recipients.clear()
                    conversation.recipients.addAll(recipients)
                    conversation.insertOrUpdate()
                    realm.close()

                    conversation
                }
    }

}