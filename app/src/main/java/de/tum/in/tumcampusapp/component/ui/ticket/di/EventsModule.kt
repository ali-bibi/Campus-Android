package de.tum.`in`.tumcampusapp.component.ui.ticket.di

import android.content.Context
import dagger.Module
import dagger.Provides
import de.tum.`in`.tumcampusapp.api.app.TUMCabeClient
import de.tum.`in`.tumcampusapp.component.prefs.AppConfig
import de.tum.`in`.tumcampusapp.component.ui.ticket.repository.EventsLocalRepository
import de.tum.`in`.tumcampusapp.component.ui.ticket.repository.EventsRemoteRepository
import de.tum.`in`.tumcampusapp.component.ui.ticket.repository.TicketsLocalRepository
import de.tum.`in`.tumcampusapp.component.ui.ticket.repository.TicketsRemoteRepository
import de.tum.`in`.tumcampusapp.database.TcaDb

@Module
class EventsModule {

    @Provides
    fun provideTicketsLocalRepository(
            database: TcaDb
    ): TicketsLocalRepository {
        return TicketsLocalRepository(database)
    }

    @Provides
    fun provideEventsRemoteRepository(
            context: Context,
            appConfig: AppConfig,
            tumCabeClient: TUMCabeClient,
            eventsLocalRepository: EventsLocalRepository,
            ticketsLocalRepository: TicketsLocalRepository,
            ticketsRemoteRepository: TicketsRemoteRepository
    ): EventsRemoteRepository {
        return EventsRemoteRepository(context, appConfig, tumCabeClient,
                eventsLocalRepository, ticketsLocalRepository, ticketsRemoteRepository)
    }

}
