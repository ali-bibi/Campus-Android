package de.tum.in.tumcampusapp.component.other.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.other.generic.activity.BaseActivity;
import de.tum.in.tumcampusapp.utils.Const;

import static androidx.preference.PreferenceFragmentCompat.ARG_PREFERENCE_ROOT;

/**
 * Provides the preferences, encapsulated into an own activity.
 */
public class UserPreferencesActivity extends BaseActivity
        implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    public UserPreferencesActivity() {
        super(R.layout.activity_user_preferences);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle args = new Bundle();
            if (intent != null) {
                args.putString(ARG_PREFERENCE_ROOT, intent.getStringExtra(Const.PREFERENCE_SCREEN));
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_frame, SettingsFragment.newInstance(args))
                    .commit();
        }
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragment,
                                           PreferenceScreen preferenceScreen) {
        String key = preferenceScreen.getKey();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_frame, SettingsFragment.newInstance(key))
                .addToBackStack(null)
                .commit();
        return true;
    }

}