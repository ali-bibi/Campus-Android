package de.tum.`in`.tumcampusapp.component.other.generic.activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.tumonline.AccessTokenManager
import de.tum.`in`.tumcampusapp.component.other.generic.drawer.DrawerMenuHelper
import de.tum.`in`.tumcampusapp.component.other.navigation.NavigationManager
import de.tum.`in`.tumcampusapp.component.prefs.AppConfig
import de.tum.`in`.tumcampusapp.component.ui.onboarding.WizNavStartActivity
import de.tum.`in`.tumcampusapp.component.ui.overview.MainActivity
import de.tum.`in`.tumcampusapp.di.AppComponent
import de.tum.`in`.tumcampusapp.di.app
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.Utils
import de.tum.`in`.tumcampusapp.utils.closeDrawers
import org.jetbrains.anko.defaultSharedPreferences
import java.util.*

/**
 * Takes care of the navigation drawer which might be attached to the activity and also handles up navigation
 */
abstract class BaseActivity(private val layoutId: Int) : AppCompatActivity(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    protected val injector: AppComponent by lazy { app.appComponent }

    protected val appConfig: AppConfig by lazy {
        AppConfig(defaultSharedPreferences)
    }

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.main_toolbar) }

    private val drawerLayout: DrawerLayout? by lazy {
        findViewById<DrawerLayout>(R.id.drawer_layout)
    }

    private val drawerList: NavigationView? by lazy {
        findViewById<NavigationView>(R.id.left_drawer)
    }

    private var drawerToggle: ActionBarDrawerToggle? = null

    private val shouldShowDrawer: Boolean by lazy {
        val askedToShowDrawer = intent.getBooleanExtra(Const.SHOW_DRAWER, false)
        drawerLayout != null && (askedToShowDrawer || this is MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        setUpToolbar()
        setUpDrawer()
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == Const.EMPLOYEE_MODE && drawerList != null) {
            // Update the drawer contents (not the header).
            (drawerList as NavigationView).menu.clear()
            DrawerMenuHelper(this).populateMenu(drawerList as NavigationView)
        }
    }

    open fun setUpToolbar() {
        setSupportActionBar(toolbar)

        supportActionBar?.let {
            val parent = NavUtils.getParentActivityName(this)
            if (parent != null || this is MainActivity) {
                it.setDisplayHomeAsUpEnabled(true)
                it.setHomeButtonEnabled(true)
            }
        }
    }

    private fun setUpDrawer() {
        drawerToggle = object : ActionBarDrawerToggle(
                this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            // Free ad space
        }

        supportActionBar?.let {
            enableDrawer(shouldShowDrawer)
            drawerToggle?.isDrawerIndicatorEnabled = shouldShowDrawer
        }

        if (!shouldShowDrawer) {
            return
        }

        drawerList?.let {
            val headerView = it.inflateHeaderView(R.layout.drawer_header)
            headerView?.let { view -> setupDrawerHeader(view) }

            val helper = DrawerMenuHelper(this)
            helper.populateMenu(it)

            it.setNavigationItemSelectedListener { item ->
                drawerLayout?.closeDrawers { NavigationManager.open(this, item) }
                true
            }
        }

        drawerToggle?.let {
            it.syncState()
            drawerLayout?.addDrawerListener(it)
        }
    }

    private fun setupDrawerHeader(headerView: View) {
        val background = headerView.findViewById<ImageView>(R.id.background)
        val nameTextView = headerView.findViewById<TextView>(R.id.nameTextView)
        val emailTextView = headerView.findViewById<TextView>(R.id.emailTextView)
        val loginButton = headerView.findViewById<MaterialButton>(R.id.loginButton)

        val isLoggedIn = AccessTokenManager.hasValidAccessToken(this)
        background.visibility = if (isLoggedIn) View.VISIBLE else View.GONE

        if (isLoggedIn) {
            val name = appConfig.chatRoomDisplayName
            if (name != null) {
                nameTextView.text = name
            } else {
                nameTextView.visibility = View.INVISIBLE
            }

            val lrzId = appConfig.lrzId
            val email = lrzId?.let { "$it@mytum.de" }
            if (email != null) {
                emailTextView.text = email
            } else {
                emailTextView.visibility = View.GONE
            }

            loginButton.visibility = View.GONE
        } else {
            nameTextView.visibility = View.GONE
            emailTextView.visibility = View.GONE

            loginButton.visibility = View.VISIBLE
            loginButton.setOnClickListener {
                val intent = Intent(this, WizNavStartActivity::class.java)
                startActivity(intent)
            }
        }

        fetchProfilePicture(headerView)

        val divider = headerView.findViewById<View>(R.id.divider)
        val rainbowBar = headerView.findViewById<View>(R.id.rainbow_bar)

        if (appConfig.rainbowMode) {
            divider.visibility = View.GONE
            rainbowBar.visibility = View.VISIBLE
        } else {
            divider.visibility = View.VISIBLE
            rainbowBar.visibility = View.GONE
        }
    }

    protected fun enableDrawer(isEnabled: Boolean) {
        val mode = if (isEnabled) {
            DrawerLayout.LOCK_MODE_UNLOCKED
        } else {
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        }

        drawerLayout?.setDrawerLockMode(mode)
    }

    fun openDrawer() {
        drawerLayout?.openDrawer(Gravity.START)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val handled = drawerToggle?.onOptionsItemSelected(item) ?: false
        if (shouldShowDrawer && handled) {
            return true
        }

        if (item.itemId == android.R.id.home) {
            NavigationManager.closeActivity(this)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (shouldShowDrawer) {
            drawerToggle?.syncState()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (shouldShowDrawer) {
            drawerToggle?.onConfigurationChanged(newConfig)
        }
    }

    private fun fetchProfilePicture(headerView: View) {
        val id = appConfig.tumOnlinePersonId ?: return
        val parts = id.split("\\*".toRegex()).toTypedArray()
        if (parts.size != 2) {
            return
        }

        val group = parts[0]
        val personId = parts[1]
        val url = String.format(Locale.getDefault(),
                Const.TUM_ONLINE_PROFILE_PICTURE_URL_FORMAT_STRING, group, personId)

        val imageView = headerView.findViewById<CircleImageView>(R.id.profileImageView)
        Picasso.get()
                .load(url)
                .error(R.drawable.photo_not_available)
                .placeholder(R.drawable.photo_not_available)
                .into(imageView)
    }

    override fun onBackPressed() {
        val handled = NavigationManager.onBackPressed(this)
        if (!handled) {
            super.onBackPressed()
        }
    }

}
