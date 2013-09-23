package de.stonecs.android.lockcontrol.dagger;

import android.app.AlarmManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.net.wifi.WifiManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Daniel on 23.09.13.
 */
@Module(library = true, complete = false)
public class AndroidModule {

    Context application;

    public AndroidModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    KeyguardManager provideKeyguardManager() {
        return (KeyguardManager) application
                .getSystemService(Context.KEYGUARD_SERVICE);
    }

    @Provides
    @Singleton
    AlarmManager provideAlarmManager() {
        return (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
    }

    @Provides
    @Singleton
    WifiManager provideWifiManager() {
        return (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
    }
}