package de.tum.in.tumcampusapp;

import android.app.Application;
import android.os.StrictMode;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import net.danlew.android.joda.JodaTimeAndroid;

import de.tum.in.tumcampusapp.component.notifications.NotificationUtils;
import de.tum.in.tumcampusapp.di.AppComponent;
import de.tum.in.tumcampusapp.di.AppModule;
import de.tum.in.tumcampusapp.di.DaggerAppComponent;
import de.tum.in.tumcampusapp.utils.ReleaseTree;
import timber.log.Timber;

public class App extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        buildAppComponent();
        setupPicasso();
        NotificationUtils.setupNotificationChannels(this);
        JodaTimeAndroid.init(this);
        setupStrictMode();
        setupTimber();
    }

    private void buildAppComponent() {
        // We use Dagger 2 for dependency injection. The main AppModule and AppComponent can be
        // found in the package "di".
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    protected void setupPicasso() {
        Picasso instance = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE))
                .build();
        Picasso.setSingletonInstance(instance);
    }

    protected void setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                               .detectAll()
                                               .permitDiskReads()  // those is mainly caused by shared preferences and room. probably enable
                                               .permitDiskWrites() // this as soon as we don't call allowMainThreadQueries() in TcaDb
                                               .penaltyLog()
                                               .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                                           .detectActivityLeaks()
                                           //.detectLeakedClosableObjects() // seems like room / DAOs leak
                                           .detectLeakedRegistrationObjects()
                                           .detectFileUriExposure()
                                           //.detectCleartextNetwork() // not available at the current minSdk
                                           //.detectContentUriWithoutPermission()
                                           //.detectUntaggedSockets()
                                           .penaltyLog()
                                           .build());
        }
    }

    private void setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
