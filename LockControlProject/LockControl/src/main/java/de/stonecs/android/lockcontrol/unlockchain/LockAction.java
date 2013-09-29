package de.stonecs.android.lockcontrol.unlockchain;

/**
 * Created by Daniel on 19.09.13.
 */
public interface LockAction {

    /**
     * @return false, if further LockAction's may be executed, true otherwise
     */
    boolean onUnlock();


    /**
     * @return false, if further LockAction's may be executed, true otherwise
     */
    boolean doLock();


    /**
     * @return tue, if the action is enabled, false otherwise
     */
    boolean isEnabled();


    /**
     * enables or disables the action, if disabled, the action will not be executed, no matter of the result of {@see applies}
     * @param enabled
     */
    void setEnabled(boolean enabled);


    /**
     * @return true or false, depending if the action applies to the circumstances under which the action is supposed to be executed
     */
    boolean applies();

    
    /**
     * @return a convenience method for (normally) {@see applies} && {@see isEnabled}
    */
    boolean shouldExecute();


}
