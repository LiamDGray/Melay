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
package tech.mattico.melay.injection

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences

import dagger.Module
import dagger.Provides

import tech.mattico.melay.manager.ExternalBlockingManager
import tech.mattico.melay.manager.ExternalBlockingManagerImpl
import tech.mattico.melay.manager.KeyManager
import tech.mattico.melay.manager.KeyManagerImpl
import tech.mattico.melay.manager.NotificationManager
import tech.mattico.melay.manager.PermissionManager
import tech.mattico.melay.manager.PermissionManagerImpl
import tech.mattico.melay.manager.RatingManager
import tech.mattico.melay.manager.WidgetManager
import tech.mattico.melay.manager.WidgetManagerImpl
import tech.mattico.melay.mapper.CursorToContact
import tech.mattico.melay.mapper.CursorToContactImpl
import tech.mattico.melay.mapper.CursorToConversation
import tech.mattico.melay.mapper.CursorToConversationImpl
import tech.mattico.melay.mapper.CursorToMessage
import tech.mattico.melay.mapper.CursorToMessageImpl
import tech.mattico.melay.mapper.CursorToPart
import tech.mattico.melay.mapper.CursorToPartImpl
import tech.mattico.melay.mapper.CursorToRecipient
import tech.mattico.melay.mapper.CursorToRecipientImpl
import tech.mattico.melay.manager.RatingManagerImpl
import tech.mattico.melay.repository.*

import javax.inject.Singleton

@Module
class AppModule(private var application: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return application
    }

    @Provides
    fun provideContentResolver(context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideRxPreferences(context: Context): RxSharedPreferences {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return RxSharedPreferences.create(preferences)
    }

    // Managers

    @Provides
    fun externalBlockingManager(manager: ExternalBlockingManagerImpl): ExternalBlockingManager = manager

    @Provides
    fun provideKeyManager(manager: KeyManagerImpl): KeyManager = manager

    @Provides
    fun provideNotificationsManager(manager: NotificationManagerImpl): NotificationManager = manager

    @Provides
    fun providePermissionsManager(manager: PermissionManagerImpl): PermissionManager = manager

    @Provides
    fun provideRatingManager(manager: RatingManagerImpl): RatingManager = manager

    @Provides
    fun provideWidgetManager(manager: WidgetManagerImpl): WidgetManager = manager


    // Mapper

    @Provides
    fun provideCursorToContact(mapper: CursorToContactImpl): CursorToContact = mapper

    @Provides
    fun provideCursorToConversation(mapper: CursorToConversationImpl): CursorToConversation = mapper

    @Provides
    fun provideCursorToMessage(mapper: CursorToMessageImpl): CursorToMessage = mapper

    @Provides
    fun provideCursorToPart(mapper: CursorToPartImpl): CursorToPart = mapper

    @Provides
    fun provideCursorToRecipient(mapper: CursorToRecipientImpl): CursorToRecipient = mapper


    // Repository

    @Provides
    fun provideContactRepository(repository: ContactRepositoryImpl): ContactRepository = repository

    @Provides
    fun provideImageRepository(repository: ImageRepostoryImpl): ImageRepository = repository

    @Provides
    fun provideMessageRepository(repository: MessageRepositoryImpl): IMessageRepository = repository

    @Provides
    fun provideSyncRepository(repository: SyncRepositoryImpl): ISyncRepository = repository
    

}