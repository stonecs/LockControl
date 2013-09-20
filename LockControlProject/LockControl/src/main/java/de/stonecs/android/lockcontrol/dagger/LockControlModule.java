package de.stonecs.android.lockcontrol.dagger;

import javax.inject.Singleton;

import android.app.AlarmManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import de.devland.esperandro.Esperandro;
import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.receivers.UserPresentReceiver;
import de.stonecs.android.lockcontrol.ui.dialogs.HmsPickerActivity;
import de.stonecs.android.lockcontrol.dagger.qualifiers.ForApplication;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;
import de.stonecs.android.lockcontrol.receivers.UnlockReceiver;
import de.stonecs.android.lockcontrol.ui.preferences.WifiMultiSelectListPreference;
import de.stonecs.android.lockcontrol.unlockchain.MaximizeWidgetsLockAction;
import de.stonecs.android.lockcontrol.unlockchain.PatternDisableLockAction;
import de.stonecs.android.lockcontrol.unlockchain.PrioritizedLockAction;
import de.stonecs.android.lockcontrol.unlockchain.RelockService;
import de.stonecs.android.lockcontrol.unlockchain.TimedRelockLockAction;

@Module(library = true, injects = { UnlockReceiver.class, UserPresentReceiver.class, RelockService.class, App.class, WifiMultiSelectListPreference.class, HmsPickerActivity.class})
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

    @Provides
    List<PrioritizedLockAction> provideLockActions(LockControlPreferences preferences, TimedRelockLockAction timedRelockLockAction, MaximizeWidgetsLockAction maximizeWidgetsLockAction, PatternDisableLockAction patternDisableLockAction){
        ArrayList<PrioritizedLockAction> prioritizedLockActions = new ArrayList<PrioritizedLockAction>();

        prioritizedLockActions.add(timedRelockLockAction);

        if (preferences.rootPatternUnlock()) {
            prioritizedLockActions.add(patternDisableLockAction);
        }
        if (preferences.cmMaximizeWidgets()) {
            prioritizedLockActions.add(maximizeWidgetsLockAction);
        }

        return prioritizedLockActions;
    }
}
