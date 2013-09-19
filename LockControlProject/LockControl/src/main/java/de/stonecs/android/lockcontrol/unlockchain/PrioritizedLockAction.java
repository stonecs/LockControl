package de.stonecs.android.lockcontrol.unlockchain;

/**
 * Created by Daniel on 19.09.13.
 */
public interface PrioritizedLockAction extends LockAction{

    int getUnlockPriority();
    int getLockPriority();
}
