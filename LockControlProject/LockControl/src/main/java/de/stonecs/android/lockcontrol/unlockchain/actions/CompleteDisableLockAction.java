package de.stonecs.android.lockcontrol.unlockchain.actions;

import android.util.Log;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;
import de.stonecs.android.lockcontrol.unlockchain.PrioritizedLockAction;

/**
 * Created by Daniel on 23.09.13.
 */
public class CompleteDisableLockAction implements PrioritizedLockAction {

    @Inject
    LockControlPreferences preferences;

    @Inject
    public CompleteDisableLockAction() {
        this.enabled = true;
    }

    private boolean enabled;


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
        App.getInstance().disableKeyguard();
        App.getInstance().completeDisableAppliesCompareHelper = false;
        return true;
    }

    @Override
    public boolean doLock() {
        Log.d(App.TAG, "reenabling keyguard");
        App.getInstance().lockKeyguard();
        disableUnneededActions();
        App.getInstance().completeDisableAppliesCompareHelper = true;
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean applies() {
        return preferences.useCompleteDisable() && App.getInstance().isKeyguardLocked() == App.getInstance().completeDisableAppliesCompareHelper;
    }

    @Override
    public boolean shouldExecute() {
        return enabled && applies();
    }

    private void disableUnneededActions(){
        PatternDisableLockAction patternDisableLockAction = App.getBean(PatternDisableLockAction.class);
        if (patternDisableLockAction != null) {
            patternDisableLockAction.setEnabled(false);
        }
        MaximizeWidgetsLockAction maximizeWidgetsLockAction = App.getBean(MaximizeWidgetsLockAction.class);
        if(maximizeWidgetsLockAction!=null){
            maximizeWidgetsLockAction.setEnabled(false);
        }
    }
}
