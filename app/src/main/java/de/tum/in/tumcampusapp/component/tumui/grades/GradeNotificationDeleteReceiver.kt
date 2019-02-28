package de.tum.`in`.tumcampusapp.component.tumui.grades

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.tum.`in`.tumcampusapp.di.app
import de.tum.`in`.tumcampusapp.service.di.DownloadModule
import java.util.*
import javax.inject.Inject

/**
 * Receives delete intents from grade notification and updates the [GradesStore] with the newly
 * released grades. This helps to prevent repeated notifications.
 */
class GradeNotificationDeleteReceiver : BroadcastReceiver() {

    @Inject
    lateinit var gradesStore: GradesStore

    override fun onReceive(context: Context, intent: Intent) {
        context.app.appComponent.downloadComponent()
                .downloadModule(DownloadModule())
                .build()
                .inject(this)

        val newGrades = intent.getStringArrayListExtra(KEY_NUMBER_OF_GRADES)
        val existingGrades = gradesStore.gradedCourses
        val updatedGrades = existingGrades + newGrades
        gradesStore.store(updatedGrades)
    }

    companion object {

        private const val KEY_NUMBER_OF_GRADES = "KEY_NUMBER_OF_GRADES";

        fun newIntent(context: Context, newGrades: List<String>): Intent {
            return Intent(context, GradeNotificationDeleteReceiver::class.java)
                    .putStringArrayListExtra(KEY_NUMBER_OF_GRADES, newGrades as ArrayList<String>)
        }

    }

}
