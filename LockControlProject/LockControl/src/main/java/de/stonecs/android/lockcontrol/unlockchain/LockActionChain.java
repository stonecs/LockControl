package de.stonecs.android.lockcontrol.unlockchain;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Created by Daniel on 19.09.13.
 */
@Singleton
public class LockActionChain implements LockAction {

    @Inject
    Provider<List<PrioritizedLockAction>> chainProvider;


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
        List<PrioritizedLockAction> chain = chainProvider.get();
        Collections.sort(chain, unlockPriorityComparator);
        for (PrioritizedLockAction lockAction : chain) {
            if (lockAction.onUnlock()) {
                break;
            }
        }
        return false;
    }

    @Override
    public boolean doLock() {
        List<PrioritizedLockAction> chain = chainProvider.get();
        Collections.sort(chain, lockPriorityComparator);
        for (PrioritizedLockAction lockAction : chain) {
            if (lockAction.doLock()) {
                break;
            }
        }
        return false;
    }
}
