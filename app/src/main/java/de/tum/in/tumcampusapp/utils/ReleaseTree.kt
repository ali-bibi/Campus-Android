package de.tum.`in`.tumcampusapp.utils

import com.crashlytics.android.Crashlytics
import timber.log.Timber

class ReleaseTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null) {
            Crashlytics.logException(t)
        } else {
            Crashlytics.log(priority, tag, message)
        }
    }

}
