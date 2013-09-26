package de.stonecs.android.lockcontrol.preferences;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;
import de.stonecs.android.lockcontrol.util.PendingIntentRequestIdGenerator;

/**
 * Created by Daniel on 23.09.13.
 */
@SharedPreferences(name = "InternalPrefs")
public interface InternalPreferences extends SharedPreferenceActions{

    @Default(ofInt = PendingIntentRequestIdGenerator.UNSAVED)
    int pendingIntentBaseSeed();
    void pendingIntentBaseSeed(int baseSeed);

    @Default(ofBoolean = false)
    boolean connectedToSelectedWifi();
    void connectedToSelectedWifi(boolean connected);

    @Default(ofBoolean = false)
    boolean deviceAdminEnabled();
    void deviceAdminEnabled(boolean deviceAdminEnabled);

}
