package de.stonecs.android.lockcontrol.unlockchain;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.dagger.qualifiers.ForApplication;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;

/**
 * Created by deekay on 20.09.13.
 */
public class TimedRelockLockAction implements PrioritizedLockAction {

    private static final int RELOCK_KEYGUARD_REQUEST = 1;

    @Inject
    @ForApplication
    Context context;

    @Inject
    LockControlPreferences preferences;

    @Inject
    AlarmManager alarmManager;

    @Inject
    public TimedRelockLockAction() {
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
        // TODO doesn't work, why???
        Intent reLockIntent = new Intent(context, RelockService.class);
        PendingIntent reLockPendingIntent = PendingIntent.getService(context, RELOCK_KEYGUARD_REQUEST, reLockIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        int timeoutMillis = preferences.disableDuration() * 1000;
        Calendar calendar = Calendar.getInstance();
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + timeoutMillis, reLockPendingIntent);


        return false;
    }

    @Override
    public boolean doLock() {
        return false;
    }


}
