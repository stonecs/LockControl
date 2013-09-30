package de.stonecs.android.lockcontrol.unlockchain.actions;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.util.Log;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.dagger.qualifiers.ForApplication;
import de.stonecs.android.lockcontrol.unlockchain.PrioritizedLockAction;
import de.stonecs.android.lockcontrol.unlockchain.RelockService;

/**
 * Created by Daniel on 23.09.13.
 */
public class CMKeyguardBugLockAction implements PrioritizedLockAction {
    @Inject
    PowerManager powerManager;

    @Inject
    DevicePolicyManager devicePolicyManager;

    @Inject
    ComponentName deviceAdminComponentName;

    @Inject
    PackageManager packageManager;

    private boolean enabled;
    private Context context;

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

    // TODO check if enable/disable of service helps with screen on being triggered, otherwise trigger an unregister / register of the receiver
    @Override
    public boolean doLock() {
        Log.d(App.TAG, "Preventing Lockscreen-not-responding Bug");
        PowerManager.WakeLock screenLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "ScreenOnWakeLock");
        ComponentName relockServiceComponent = new ComponentName(context, RelockService.class);
        packageManager.setComponentEnabledSetting(relockServiceComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        screenLock.acquire();
        screenLock.release();

        if (devicePolicyManager.isAdminActive(deviceAdminComponentName)) {
            devicePolicyManager.lockNow();
        } else {
            // TODO notification
            Log.d(App.TAG, "Device Admin not enabled");
        }
        packageManager.setComponentEnabledSetting(relockServiceComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
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
