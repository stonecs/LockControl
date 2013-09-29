package de.stonecs.android.lockcontrol.unlockchain.actions;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.dagger.qualifiers.ForApplication;
import de.stonecs.android.lockcontrol.preferences.InternalPreferences;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;
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
    LockControlPreferences preferences;

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
        return !internalPreferences.connectedToSelectedWifi() || !preferences.ignoreTimeoutOnWifi();
    }

    @Override
    public boolean shouldExecute() {
        return enabled && applies();
    }


}
