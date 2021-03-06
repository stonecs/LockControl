package de.stonecs.android.lockcontrol.dagger;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import de.devland.esperandro.Esperandro;
import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.LockControlSettingsActivity;
import de.stonecs.android.lockcontrol.dagger.qualifiers.ForApplication;
import de.stonecs.android.lockcontrol.preferences.InternalPreferences;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;
import de.stonecs.android.lockcontrol.receivers.DeviceAdminReceiver;
import de.stonecs.android.lockcontrol.receivers.UserPresentReceiver;
import de.stonecs.android.lockcontrol.receivers.WifiBroadcastReceiver;
import de.stonecs.android.lockcontrol.ui.dialogs.HmsPickerActivity;
import de.stonecs.android.lockcontrol.ui.preferences.WifiMultiSelectListPreference;
import de.stonecs.android.lockcontrol.unlockchain.PrioritizedLockAction;
import de.stonecs.android.lockcontrol.unlockchain.RelockService;
import de.stonecs.android.lockcontrol.unlockchain.actions.CMKeyguardBugLockAction;
import de.stonecs.android.lockcontrol.unlockchain.actions.CompleteDisableLockAction;
import de.stonecs.android.lockcontrol.unlockchain.actions.MaximizeWidgetsLockAction;
import de.stonecs.android.lockcontrol.unlockchain.actions.PatternDisableLockAction;
import de.stonecs.android.lockcontrol.unlockchain.actions.TimedRelockLockAction;

@Module(library = true, injects = {LockControlSettingsActivity.class, MaximizeWidgetsLockAction.class, PatternDisableLockAction.class, WifiBroadcastReceiver.class, UserPresentReceiver.class, RelockService.class, App.class, WifiMultiSelectListPreference.class, HmsPickerActivity.class}, includes = AndroidModule.class)
public class LockControlModule {

    Context application;

    public LockControlModule(Application application) {
        this.application = application;
    }

    @Provides
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    LockControlPreferences provideLockControlPreferences() {
        return Esperandro.getPreferences(LockControlPreferences.class, application);
    }

    @Provides
    InternalPreferences provideInternalPreferences() {
        return Esperandro.getPreferences(InternalPreferences.class, application);
    }

    @Provides
    List<PrioritizedLockAction> provideLockActions(TimedRelockLockAction timedRelockLockAction, MaximizeWidgetsLockAction maximizeWidgetsLockAction
            , PatternDisableLockAction patternDisableLockAction, CompleteDisableLockAction completeDisableLockAction, CMKeyguardBugLockAction cmKeyguardBugLockAction) {
        ArrayList<PrioritizedLockAction> prioritizedLockActions = new ArrayList<PrioritizedLockAction>();

        prioritizedLockActions.add(timedRelockLockAction);
        prioritizedLockActions.add(cmKeyguardBugLockAction);
        prioritizedLockActions.add(completeDisableLockAction);
        prioritizedLockActions.add(patternDisableLockAction);
        prioritizedLockActions.add(maximizeWidgetsLockAction);

        return prioritizedLockActions;
    }

    @Provides
    ComponentName provideDeviceAdminReceiverComponentName() {
        return new ComponentName(application, DeviceAdminReceiver.class);
    }
}
