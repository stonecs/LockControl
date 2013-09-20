package de.stonecs.android.lockcontrol.receivers;

import java.util.Calendar;

import javax.inject.Inject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;

public class UnlockReceiver extends BroadcastReceiver {

	private static final int REENABLE_KEYGUARD_REQUEST = 1;
	public static final String REENABLE_KEYGUARD = "de.stonecs.android.lockcontrol.reenableKeyguard";

	@Inject
	LockControlPreferences preferences;
	@Inject
	AlarmManager alarmManager;

	public UnlockReceiver() {
		App.inject(this);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.USER_PRESENT".equals(intent.getAction())) {
			//disableKeyguard(context);
		} else if (REENABLE_KEYGUARD.equals(intent.getAction())) {
			//reEnableKeyguard(context);
		}
	}

	private void reEnableKeyguard(Context context) {
		Log.d(App.TAG, "reenabling keyguard in receiver");
		App.getInstance().getKeyguardLock().reenableKeyguard();
	}

	private void disableKeyguard(Context context) {
		Log.d(App.TAG, "screen unlocked");
		App.getInstance().getKeyguardLock().disableKeyguard();
		setReEnableAlarm(context);
	}

	private void setReEnableAlarm(Context context) {

		Intent reEnableIntent = new Intent(REENABLE_KEYGUARD);
		PendingIntent reEnablePendingIntent = PendingIntent.getBroadcast(
				context, REENABLE_KEYGUARD_REQUEST, reEnableIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		int timeoutMillis = preferences.disableDuration() * 1000;
		Calendar calendar = Calendar.getInstance();
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()
				+ timeoutMillis, reEnablePendingIntent);
	}

}
