package de.stonecs.android.lockcontrol.dagger;

import javax.inject.Singleton;

import android.app.AlarmManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.net.wifi.WifiManager;

import dagger.Module;
import dagger.Provides;
import de.devland.esperandro.Esperandro;
import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.ui.dialogs.HmsPickerActivity;
import de.stonecs.android.lockcontrol.dagger.qualifiers.ForApplication;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;
import de.stonecs.android.lockcontrol.receivers.UnlockReceiver;
import de.stonecs.android.lockcontrol.ui.preferences.WifiMultiSelectListPreference;

@Module(library = true, injects = { UnlockReceiver.class, App.class, WifiMultiSelectListPreference.class, HmsPickerActivity.class})
public class LockControlModule {

	Context application;

	public LockControlModule(Application application) {
		this.application = application;
	}

    @Provides
    @ForApplication
    Context provideApplicationContext(){
        return application;
    }

	@Provides
	LockControlPreferences provideLockControlPreferences() {
		return Esperandro.getPreferences(LockControlPreferences.class, application);
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
    WifiManager provideWifiManager(){
        return (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
    }
}
