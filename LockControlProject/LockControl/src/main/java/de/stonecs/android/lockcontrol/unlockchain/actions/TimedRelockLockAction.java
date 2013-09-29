package de.stonecs.android.lockcontrol.unlockchain.actions;

import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.dagger.qualifiers.ForApplication;
import de.stonecs.android.lockcontrol.preferences.InternalPreferences;
import de.stonecs.android.lockcontrol.unlockchain.PrioritizedLockAction;
import de.stonecs.android.lockcontrol.unlockchain.RelockService;

/**
 * Created by deekay on 20.09.13.
 */
public class TimedRelockLockAction implements PrioritizedLockAction {



    @Inject
    @ForApplication
    Context context;

    @Inject
    InternalPreferences internalPreferences;

    private boolean enabled;

    @Inject
    public TimedRelockLockAction() {
        this.enabled = true;
    }

    @Override
    public int getUnlockPriority() {
        return 10000;
    }

    @Override
    public int getLockPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean onUnlock() {
        Intent intent = new Intent(context, RelockService.class);
        context.startService(intent);
        return false;
    }

    @Override
    public boolean doLock() {
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
        return !internalPreferences.connectedToSelectedWifi();
    }

    @Override
    public boolean shouldExecute() {
        return enabled && applies();
    }


}
