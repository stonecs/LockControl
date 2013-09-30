package de.stonecs.android.lockcontrol.receivers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.stonecs.android.lockcontrol.App;

/**
 * Created by deekay on 26.09.13.
 */
public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Log.d(App.TAG, "Received device admin");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Log.d(App.TAG, "Device admin was disabled");
    }
}
