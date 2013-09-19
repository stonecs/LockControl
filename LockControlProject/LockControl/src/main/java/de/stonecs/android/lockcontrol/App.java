package de.stonecs.android.lockcontrol;

import javax.inject.Inject;

import android.app.Application;
import android.app.KeyguardManager;
import dagger.ObjectGraph;
import de.stonecs.android.lockcontrol.dagger.LockControlModule;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;

public class App extends Application {
	public static String TAG = "de.stonecs.android.lockControl.Tag";
	public static final String KEYGUARD_LOCK_TAG = "de.stonecs.android.lockcontrol.keyguardLockTag";

	@Inject
	KeyguardManager keyguardManager;

	@Inject
	LockControlPreferences preferences;

	ObjectGraph objectGraph;

	KeyguardManager.KeyguardLock keyguardLock;

	private static App instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		objectGraph = ObjectGraph.create(new LockControlModule(this));
		inject(this);
		initPrefs();
	}

	private void initPrefs() {
		preferences.disableDuration(preferences.disableDuration());
	}

	public KeyguardManager.KeyguardLock getKeyguardLock() {
		if (keyguardLock == null) {
			keyguardLock = keyguardManager.newKeyguardLock(KEYGUARD_LOCK_TAG);
		}
		return keyguardLock;
	}

	public static App getInstance() {
		return instance;
	}

	public static void inject(Object object) {
		instance.objectGraph.inject(object);
	}
}
