package de.tum.`in`.tumcampusapp.component.ui.updatenote

import de.tum.`in`.tumcampusapp.BuildConfig
import de.tum.`in`.tumcampusapp.api.app.TUMCabeClient
import de.tum.`in`.tumcampusapp.component.prefs.AppConfig
import de.tum.`in`.tumcampusapp.utils.tryOrNull
import javax.inject.Inject

class UpdateNoteController @Inject constructor(
        private val tumCabeClient: TUMCabeClient,
        private val appConfig: AppConfig
) {

    fun downloadUpdateNote() {
        val note = tryOrNull { tumCabeClient.getUpdateNote(BuildConfig.VERSION_CODE) }
        note?.updateNote?.let { appConfig.updateMessage = it }
    }

}
