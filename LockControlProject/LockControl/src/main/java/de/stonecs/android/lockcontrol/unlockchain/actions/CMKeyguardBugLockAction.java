package de.stonecs.android.lockcontrol.unlockchain.actions;

import android.app.Instrumentation;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.dagger.qualifiers.ForApplication;
import de.stonecs.android.lockcontrol.preferences.InternalPreferences;
import de.stonecs.android.lockcontrol.unlockchain.PrioritizedLockAction;

/**
 * Created by Daniel on 23.09.13.
 */
public class CMKeyguardBugLockAction implements PrioritizedLockAction {
    @Inject
    PowerManager powerManager;

    @Inject
    DevicePolicyManager devicePolicyManager;

    @Inject
    InternalPreferences internalPreferences;

    private Context context;

    private boolean enabled;

    @Inject
    public CMKeyguardBugLockAction(@ForApplication Context context) {
        this.context = context;
        this.enabled = true;
    }

    @Override
    public int getUnlockPriority() {
        return 0;
    }

    @Override
    public int getLockPriority() {
        return 0;
    }

    @Override
    public boolean onUnlock() {
        return false;
    }

    @Override
    public boolean doLock() {
            Log.d(App.TAG, "Preventing Lockscreen-not-responding Bug");
            PowerManager.WakeLock screenLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "ScreenOnWakeLock");
            screenLock.acquire();
            screenLock.release();

            if (internalPreferences.deviceAdminEnabled()) {
                devicePolicyManager.lockNow();
            } else {
                // TODO notification
                Log.d(App.TAG, "Device Admin not enabled");
            }
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

    // todo if(cm){
    @Override
    public boolean applies() {
        return !powerManager.isScreenOn();
    }

    @Override
    public boolean shouldExecute() {
        return enabled && applies();
    }
}
