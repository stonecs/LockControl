package de.stonecs.android.lockcontrol.unlockchain;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.security.Provider;
import java.util.Calendar;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
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
    AlarmManager alarmManager;


    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;

    @Override
    public void onCreate() {
        super.onCreate();
        App.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        receiver = new ScreenOffReceiver();
        intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(RE_ENABLE_KEYGUARD);
        registerReceiver(receiver, intentFilter);
        return Service.START_STICKY;
    }

    // not used
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ScreenOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                Log.d(App.TAG, "starting timer for re-lock");
                Intent reLockIntent = new Intent(RE_ENABLE_KEYGUARD);
                PendingIntent reLockPendingIntent = PendingIntent.getBroadcast(context, RELOCK_KEYGUARD_REQUEST, reLockIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                int timeoutMillis = preferences.disableDuration() * 1000;
                Calendar calendar = Calendar.getInstance();
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + timeoutMillis, reLockPendingIntent);
            } else {
                Log.d(App.TAG, "executing re-lock");
                chain.doLock();
            }
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}