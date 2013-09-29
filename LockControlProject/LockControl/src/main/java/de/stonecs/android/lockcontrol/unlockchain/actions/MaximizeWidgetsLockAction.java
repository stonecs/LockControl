package de.stonecs.android.lockcontrol.unlockchain.actions;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.dagger.qualifiers.ForApplication;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;
import de.stonecs.android.lockcontrol.unlockchain.PrioritizedLockAction;

/**
 * Created by deekay on 20.09.13.
 */
public class MaximizeWidgetsLockAction implements PrioritizedLockAction {

    @Inject
    @ForApplication
    Context context;

    @Inject
    LockControlPreferences preferences;

    private boolean enabled;

    @Inject
    public MaximizeWidgetsLockAction() {
        this.enabled = true;
    }


    @Override
    public int getUnlockPriority() {
        return 1000;
    }

    @Override
    public int getLockPriority() {
        return 3000;
    }

    @Override
    public boolean onUnlock() {
        enableMaximizeWidget();
        return false;
    }

    @Override
    public boolean doLock() {
        disableMaximizeWidget();
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

    @Override
    public boolean applies() {
        return preferences.cmMaximizeWidgets();
    }

    @Override
    public boolean shouldExecute() {
        return enabled && applies();
    }

    private void enableMaximizeWidget() {
        Log.d(App.TAG, "Set 'maximize widgets' on");
        Settings.System.putInt(context.getContentResolver(), "lockscreen_maximize_widgets", 1);
    }

    private void disableMaximizeWidget() {
        Log.d(App.TAG, "Set 'maximize widgets' off");
        Settings.System.putInt(context.getContentResolver(), "lockscreen_maximize_widgets", 0);
    }
}
