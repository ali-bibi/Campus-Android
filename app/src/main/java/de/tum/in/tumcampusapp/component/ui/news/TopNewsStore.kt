package de.tum.`in`.tumcampusapp.component.ui.news

import de.tum.`in`.tumcampusapp.component.prefs.AppConfig
import de.tum.`in`.tumcampusapp.component.ui.news.model.NewsAlert
import javax.inject.Inject

/**
 * This interface defines all possible operations on a persistent store for [NewsAlert] objects.
 * It's implemented by [RealTopNewsStore].
 */
interface TopNewsStore {

    /**
     * Returns whether the user has enabled the display of top-news cards in overview screen.
     *
     * @return True if top-news cards should be displayed
     */
    fun isEnabled(): Boolean

    /**
     * Sets whether top-news cards should be displayed in overview screen.
     *
     * @param isEnabled Whether top-news cards should be displayed
     */
    fun setEnabled(isEnabled: Boolean)

    /**
     * Returns the most recent [NewsAlert], or null if none is stored.
     *
     * @return The [NewsAlert] or null
     */
    fun getNewsAlert(): NewsAlert?

    /**
     * Stores the provided [NewsAlert] in the persistent store. It can later be retrieved via
     * [getNewsAlert].
     *
     * @param newsAlert The [NewsAlert] to be stored
     */
    fun store(newsAlert: NewsAlert)

}

class RealTopNewsStore @Inject constructor(
        private val appConfig: AppConfig
) : TopNewsStore {

    override fun isEnabled(): Boolean = appConfig.showTopNews

    override fun setEnabled(isEnabled: Boolean) {
        appConfig.showTopNews = isEnabled
    }

    override fun getNewsAlert(): NewsAlert? {
        val showTopNews = appConfig.showTopNews
        if (showTopNews.not()) {
            return null
        }
        return appConfig.newsAlert
    }

    override fun store(newsAlert: NewsAlert) {
        appConfig.newsAlert = newsAlert
    }

}
