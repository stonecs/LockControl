package de.stonecs.android.lockcontrol.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.preferences.InternalPreferences;

/**
 * Created by deekay on 26.09.13.
 */
public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {

    @Inject
    InternalPreferences internalPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        App.inject(this);
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        internalPreferences.deviceAdminEnabled(true);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        internalPreferences.deviceAdminEnabled(false);
    }
}
