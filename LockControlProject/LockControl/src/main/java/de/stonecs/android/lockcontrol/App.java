package de.stonecs.android.lockcontrol;

import javax.inject.Inject;

import android.app.Application;
import android.app.KeyguardManager;
import android.content.Intent;

import dagger.ObjectGraph;
import de.stonecs.android.lockcontrol.dagger.AndroidModule;
import de.stonecs.android.lockcontrol.dagger.LockControlModule;
import de.stonecs.android.lockcontrol.preferences.InternalPreferences;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;
import de.stonecs.android.lockcontrol.receivers.WifiBroadcastReceiver;

public class App extends Application {
    public static String TAG = "de.stonecs.android.lockControl.Tag";
    public static final String KEYGUARD_LOCK_TAG = "de.stonecs.android.lockcontrol.keyguardLockTag";

    @Inject
    KeyguardManager keyguardManager;

    @Inject
    LockControlPreferences preferences;

    @Inject
    InternalPreferences internalPreferences;

    ObjectGraph objectGraph;

    KeyguardManager.KeyguardLock keyguardLock;

    private static App instance;

    public int desiredNextLockPatternState = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        objectGraph = ObjectGraph.create(new LockControlModule(this), new AndroidModule(this));
        inject(this);
        initPrefs();
    }

    private void initPrefs() {
        if (!preferences.contains("disableDuration")) {
            preferences.disableDuration(preferences.disableDuration());
        }
        if (!internalPreferences.contains("connectedToSelectedWifi")) {
            Intent intent = new Intent(WifiBroadcastReceiver.APPLICATION_TRIGGERED_CHECK_ACTION);
            sendBroadcast(intent);
        }
    }

    private KeyguardManager.KeyguardLock getKeyguardLock() {
        if (keyguardLock == null) {
            keyguardLock = keyguardManager.newKeyguardLock(KEYGUARD_LOCK_TAG);
        }
        return keyguardLock;
    }

    public void lockKeyguard() {
        getKeyguardLock().reenableKeyguard();
        keyguardLock = null;
    }

    public void disableKeyguard() {
        getKeyguardLock().disableKeyguard();
    }

    public boolean isKeyguardLocked() {
        return keyguardLock == null;
    }

    public static App getInstance() {
        return instance;
    }

    public static void inject(Object object) {
        instance.objectGraph.inject(object);
    }
}
