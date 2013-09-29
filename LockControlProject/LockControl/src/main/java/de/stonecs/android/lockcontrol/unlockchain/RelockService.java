package de.stonecs.android.lockcontrol.unlockchain;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.preferences.InternalPreferences;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;

/**
 * Created by deekay on 20.09.13.
 */
public class RelockService extends Service {

    private static final int RELOCK_KEYGUARD_REQUEST = 1;
    public static final String RE_ENABLE_KEYGUARD = "de.stonecs.android.lockcontrol.reenableKeyguard";

    @Inject
    LockActionChain chain;

    @Inject
    LockControlPreferences preferences;

    @Inject
    InternalPreferences internalPreferences;

    @Inject
    AlarmManager alarmManager;


    private BroadcastReceiver screenOffReceiver;
    private IntentFilter screenOffReceiverIntentFilter;

    private BroadcastReceiver screenOnReceiver;
    private IntentFilter screenOnReceiverIntentFilter;

    private PendingIntent reLockPendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        App.inject(this);
        screenOffReceiver = new ScreenOffReceiver();
        screenOnReceiver = new ScreenOnReceiver();
        screenOffReceiverIntentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        screenOnReceiverIntentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        screenOffReceiverIntentFilter.addAction(RE_ENABLE_KEYGUARD);
        registerReceiver(screenOffReceiver, screenOffReceiverIntentFilter);
        registerReceiver(screenOnReceiver, screenOnReceiverIntentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    // not used
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(screenOffReceiver);
        unregisterReceiver(screenOnReceiver);
        super.onDestroy();
    }

    private class ScreenOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                if (shouldSetTimer()) {
                    Log.d(App.TAG, "starting timer for re-lock");
                    Intent reLockIntent = new Intent(RE_ENABLE_KEYGUARD);
                    reLockPendingIntent = PendingIntent.getBroadcast(context, RELOCK_KEYGUARD_REQUEST, reLockIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    int timeoutMillis = preferences.disableDuration() * 1000;
                    Calendar calendar = Calendar.getInstance();
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + timeoutMillis, reLockPendingIntent);
                }
            } else if (RE_ENABLE_KEYGUARD.equals(intent.getAction())) {
                Log.d(App.TAG, "executing re-lock");
                chain.doLock();
            }
        }
    }

    private boolean shouldSetTimer() {
        boolean shouldSetTimer = preferences.rootPatternUnlock() || (preferences.useCompleteDisable() && !App.getInstance().isKeyguardLocked());
        shouldSetTimer &= !(internalPreferences.connectedToSelectedWifi());
        return shouldSetTimer;
    }

    private class ScreenOnReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                if (shouldSetTimer()) {
                    Log.d(App.TAG, "removing timer for re-lock");
                    alarmManager.cancel(reLockPendingIntent);
                }
            }
        }
    }
}