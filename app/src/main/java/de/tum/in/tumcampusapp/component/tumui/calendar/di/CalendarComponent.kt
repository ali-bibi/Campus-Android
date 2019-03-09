package de.tum.`in`.tumcampusapp.component.tumui.calendar.di

import dagger.Subcomponent
import de.tum.`in`.tumcampusapp.service.SilenceService

@Subcomponent(modules = [CalendarModule::class])
interface CalendarComponent {

    fun inject(silenceService: SilenceService)

    @Subcomponent.Builder
    interface Builder {

        fun calendarModule(calendarModule: CalendarModule): CalendarComponent.Builder

        fun build(): CalendarComponent

    }

}
