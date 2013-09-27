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

    @Inject
    public CMKeyguardBugLockAction(@ForApplication Context context) {
        this.context = context;
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
        if (!powerManager.isScreenOn()) {
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
        }
        //    try {
        //       int screenOffTimeout = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        //Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0);
        // Thread.sleep(1000L);
        //  Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, screenOffTimeout);
        //  } catch (Settings.SettingNotFoundException unhandled) {
        //  } catch (InterruptedException unhandled) {
        //   }
        //       KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_POWER);
        //        try {
        //            Shell shell = RootTools.getShell(true);
        //            shell.add(new CommandCapture(0, "input keyevent 26")).waitForFinish();
        //            shell.close();
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        } catch (TimeoutException e) {
        //            e.printStackTrace();
        //        } catch (RootDeniedException e) {
        //            e.printStackTrace();
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }

        return false;
    }
}
