package de.tum.`in`.tumcampusapp.service

import android.content.Context
import androidx.work.*
import androidx.work.ListenableWorker.Result.*
import androidx.work.NetworkType.CONNECTED
import de.tum.`in`.tumcampusapp.api.app.AuthenticationManager
import de.tum.`in`.tumcampusapp.api.app.TUMCabeClient
import de.tum.`in`.tumcampusapp.api.app.exception.NoPrivateKey
import de.tum.`in`.tumcampusapp.component.ui.chat.ChatMessageViewModel
import de.tum.`in`.tumcampusapp.component.ui.chat.repository.ChatMessageLocalRepository
import de.tum.`in`.tumcampusapp.component.ui.chat.repository.ChatMessageRemoteRepository
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.di.injector
import de.tum.`in`.tumcampusapp.utils.Utils
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Service used to send chat messages.
 */
class SendMessageWorker(
        context: Context,
        workerParams: WorkerParameters
) : Worker(context, workerParams) {

    @Inject
    lateinit var database: TcaDb

    @Inject
    lateinit var tumCabeClient: TUMCabeClient

    @Inject
    lateinit var authManager: AuthenticationManager

    init {
        injector.inject(this)
    }

    override fun doWork(): ListenableWorker.Result {
        ChatMessageRemoteRepository.tumCabeClient = tumCabeClient
        ChatMessageLocalRepository.db = database

        val viewModel = ChatMessageViewModel(ChatMessageLocalRepository, ChatMessageRemoteRepository)
        viewModel.deleteOldEntries()

        return try {
            viewModel.getUnsent()
                    .asSequence()
                    .onEach { it.signature = authManager.sign(it.text) }
                    .forEach { viewModel.sendMessage(it.room, it, applicationContext) }
            success()
        } catch (noPrivateKey: NoPrivateKey) {
            // Retrying doesn't make any sense
            failure()
        } catch (e: Exception) {
            Utils.log(e)
            // Maybe the server is currently busy, but we really want to send the messages
            retry()
        }
    }

    companion object {

        @JvmStatic
        fun getWorkRequest(): WorkRequest {
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(CONNECTED)
                    .build()
            return OneTimeWorkRequestBuilder<SendMessageWorker>()
                    .setConstraints(constraints)
                    .build()
        }

        fun getPeriodicWorkRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(CONNECTED)
                    .build()
            return PeriodicWorkRequestBuilder<SendMessageWorker>(3, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .build()
        }

    }

}
