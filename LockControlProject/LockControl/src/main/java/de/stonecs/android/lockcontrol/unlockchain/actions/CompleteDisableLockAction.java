package de.stonecs.android.lockcontrol.unlockchain.actions;

import android.util.Log;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.preferences.InternalPreferences;
import de.stonecs.android.lockcontrol.unlockchain.PrioritizedLockAction;

/**
 * Created by Daniel on 23.09.13.
 */
public class CompleteDisableLockAction implements PrioritizedLockAction {

    @Inject
    InternalPreferences internalPreferences;

    @Inject
    public CompleteDisableLockAction() {
    }

    @Override
    public int getUnlockPriority() {
        return 9999;
    }

    @Override
    public int getLockPriority() {
        return 9999;
    }

    @Override
    public boolean onUnlock() {
        if (App.getInstance().isKeyguardLocked()) {
            Log.d(App.TAG, "completely disabling keyguard");
            App.getInstance().disableKeyguard();
        }
        return true;
    }

    @Override
    public boolean doLock() {
        if (App.getInstance().isKeyguardLocked()) {
            Log.d(App.TAG, "reenabling keyguard");
            App.getInstance().lockKeyguard();
        }
        return true;
    }
}
