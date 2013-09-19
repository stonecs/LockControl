package de.stonecs.android.lockcontrol.unlockchain;

/**
 * Created by Daniel on 19.09.13.
 */
public interface LockAction {

    /**
     *
     * @return false, if further LockAction's may be executed, true otherwise
     */
    boolean onUnlock();


    /**
     *
     * @return false, if further LockAction's may be executed, true otherwise
     */
    boolean doLock();

}
