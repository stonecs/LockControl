package de.stonecs.android.lockcontrol.unlockchain;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.preferences.InternalPreferences;

/**
 * Created by Daniel on 19.09.13.
 */
@Singleton
public class LockActionChain implements LockAction {

    @Inject
    Provider<List<PrioritizedLockAction>> chainProvider;

    @Inject
    InternalPreferences internalPreferences;

    // TODO read about <? super clazz>
    private Comparator<? super PrioritizedLockAction> unlockPriorityComparator = new Comparator<PrioritizedLockAction>() {
        @Override
        public int compare(PrioritizedLockAction left, PrioritizedLockAction right) {
            return new Integer(right.getUnlockPriority()).compareTo(left.getUnlockPriority());
        }
    };

    private Comparator<? super PrioritizedLockAction> lockPriorityComparator = new Comparator<PrioritizedLockAction>() {
        @Override
        public int compare(PrioritizedLockAction left, PrioritizedLockAction right) {
            return new Integer(right.getLockPriority()).compareTo(left.getLockPriority());
        }
    };

    @Override
    public boolean onUnlock() {
        Log.d(App.TAG, "onUnlock of chain called");
        List<PrioritizedLockAction> chain = chainProvider.get();
        Collections.sort(chain, unlockPriorityComparator);
        for (PrioritizedLockAction lockAction : chain) {
            if (lockAction.shouldExecute()) {
                if (lockAction.onUnlock()) {
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public boolean doLock() {
        Log.d(App.TAG, "doLock of chain called");
        List<PrioritizedLockAction> chain = chainProvider.get();
        Collections.sort(chain, lockPriorityComparator);
        for (PrioritizedLockAction lockAction : chain) {
            if (lockAction.shouldExecute()) {
                if (lockAction.doLock()) {
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        // unused
    }

    @Override
    public boolean applies() {
        return true;
    }

    @Override
    public boolean shouldExecute() {
        return true;
    }
}
