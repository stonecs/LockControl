package de.stonecs.android.lockcontrol.dagger;

import javax.inject.Singleton;

import android.app.AlarmManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import de.devland.esperandro.Esperandro;
import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.LockControlSettingsActivity;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;
import de.stonecs.android.lockcontrol.receivers.UnlockReceiver;

@Module(injects = { UnlockReceiver.class, App.class })
public class LockControlModule {

	Context context;

	public LockControlModule(Application application) {
		this.context = application;
	}

	@Provides
	LockControlPreferences provideLockControlPreferences() {
		return Esperandro.getPreferences(LockControlPreferences.class, context);
	}

	@Provides
	@Singleton
	KeyguardManager provideKeyguardManager() {
		return (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
	}

	@Provides
	@Singleton
	AlarmManager provideAlarmManager() {
		return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}
}
