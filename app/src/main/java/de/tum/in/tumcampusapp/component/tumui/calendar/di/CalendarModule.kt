package de.tum.`in`.tumcampusapp.component.tumui.calendar.di

import android.content.Context
import dagger.Module
import dagger.Provides
import de.tum.`in`.tumcampusapp.component.tumui.calendar.CalendarController

@Module
class CalendarModule {

    @Provides
    fun provideCalendarController(
            context: Context
    ): CalendarController = CalendarController(context)

}
