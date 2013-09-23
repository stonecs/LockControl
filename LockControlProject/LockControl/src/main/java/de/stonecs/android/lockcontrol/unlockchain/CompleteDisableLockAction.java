package de.stonecs.android.lockcontrol.unlockchain;

import android.util.Log;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;

/**
 * Created by Daniel on 23.09.13.
 */
public class CompleteDisableLockAction implements PrioritizedLockAction {

    @Inject
    public CompleteDisableLockAction(){
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
        Log.d(App.TAG, "completely disabling keyguard");
        App.getInstance().getKeyguardLock().disableKeyguard();
        return true;
    }

    @Override
    public boolean doLock() {
        Log.d(App.TAG, "reenabling keyguard");
        App.getInstance().getKeyguardLock().reenableKeyguard();
        return true;
    }
}
