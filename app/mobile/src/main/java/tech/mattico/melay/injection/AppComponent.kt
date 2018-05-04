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


import dagger.Component
import tech.mattico.melay.MelayApplication
import tech.mattico.melay.view.conversationinfo.ConversationInfoActivity
import tech.mattico.melay.view.conversationinfo.ConversationInfoViewModel

import tech.mattico.melay.view.MelayDialog


import tech.mattico.melay.view.widget.AvatarView
import tech.mattico.melay.view.widget.PagerTitleView
import tech.mattico.melay.view.widget.PreferenceView
import tech.mattico.melay.view.widget.MelayEditText
import tech.mattico.melay.view.widget.MelaySwitch
import tech.mattico.melay.view.widget.MelayTextView
import tech.mattico.melay.view.widget.Separator
import tech.mattico.melay.utils.ContactImageLoader

import tech.mattico.melay.view.compose.ComposeViewModel
import tech.mattico.melay.view.compose.ComposeActivity
import tech.mattico.melay.view.widget.WidgetAdapter
import tech.mattico.melay.view.widget.WidgetProvider
//receivers
import tech.mattico.melay.receiver.DefaultSmsChangedReceiver
import tech.mattico.melay.receiver.MarkReadReceiver
import tech.mattico.melay.receiver.MarkSeenReceiver
import tech.mattico.melay.receiver.MmsReceivedReceiver
import tech.mattico.melay.receiver.MmsSentReceiver
import tech.mattico.melay.receiver.MmsUpdatedReceiver
import tech.mattico.melay.receiver.NightModeReceiver
import tech.mattico.melay.receiver.RemoteMessagingReceiver
import tech.mattico.melay.receiver.SmsDeliveredReceiver
import tech.mattico.melay.receiver.SmsProviderChangedReceiver
import tech.mattico.melay.receiver.SmsReceiver
import tech.mattico.melay.receiver.SmsSentReceiver
import tech.mattico.melay.view.compose.DetailedChipView
import tech.mattico.melay.view.main.MainActivity
import tech.mattico.melay.view.about.AboutActivity
import tech.mattico.melay.view.about.AboutViewModel
import tech.mattico.melay.view.main.MainViewModel
import tech.mattico.melay.view.reply.MelayReplyActivity
import tech.mattico.melay.view.reply.MelayReplyViewModel
import tech.mattico.melay.view.setup.SetupActivity
import tech.mattico.melay.view.setup.SetupViewModel
import tech.mattico.melay.view.settings.SettingsActivity
import tech.mattico.melay.view.settings.SettingsViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {

    fun inject(application: MelayApplication)
    fun inject(activity: MainActivity)
    fun inject(activity: ComposeActivity)
    fun inject(activity: SetupActivity)

    fun inject(activity: AboutActivity)
    //fun inject(activity: BlockedActivity)

    fun inject(activity: ConversationInfoActivity)
    /*fun inject(activity: GalleryActivity)
    fun inject(activity: NotificationPrefsActivity)
    fun inject(activity: PlusActivity)
    */
    fun inject(activity: MelayReplyActivity)

    fun inject(activity: SettingsActivity)
    //fun inject(activity: ThemePickerActivity)

    fun inject(dialog: MelayDialog)

    fun inject(fetcher: ContactImageLoader.ContactImageFetcher)

    fun inject(receiver: DefaultSmsChangedReceiver)
    fun inject(receiver: SmsDeliveredReceiver)
    fun inject(receiver: SmsSentReceiver)
    fun inject(receiver: MarkSeenReceiver)
    fun inject(receiver: MarkReadReceiver)
    fun inject(receiver: MmsReceivedReceiver)
    fun inject(receiver: MmsSentReceiver)
    fun inject(receiver: MmsUpdatedReceiver)
    fun inject(receiver: NightModeReceiver)
    fun inject(receiver: RemoteMessagingReceiver)
    fun inject(receiver: SmsProviderChangedReceiver)
    fun inject(receiver: SmsReceiver)
    fun inject(receiver: WidgetProvider)

    fun inject(service: WidgetAdapter)

    fun inject(view: AvatarView)

    fun inject(view: DetailedChipView)
    fun inject(view: PagerTitleView)
    fun inject(view: PreferenceView)
    fun inject(view: MelayEditText)
    fun inject(view: MelaySwitch)
    fun inject(view: MelayTextView)
    fun inject(view: Separator)


    fun inject(viewModel: ComposeViewModel)
    fun inject(viewModel: MainViewModel)
    fun inject(viewModel: SetupViewModel)
    fun inject(viewModel: AboutViewModel)
    //fun inject(viewModel: BlockedViewModel)

    fun inject(viewModel: ConversationInfoViewModel)
    /*fun inject(viewModel: GalleryViewModel)
    fun inject(viewModel: NotificationPrefsViewModel)
    fun inject(viewModel: PlusViewModel)
    */
    fun inject(viewModel: MelayReplyViewModel)

    fun inject(viewModel: SettingsViewModel)
    //fun inject(viewModel: ThemePickerViewModel)

}