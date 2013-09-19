package de.stonecs.android.lockcontrol.preferences;

import java.util.Set;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;

@SharedPreferences
public interface LockControlPreferences extends SharedPreferenceActions {

	@Default(ofInt = 30)
	int disableDuration();
	void disableDuration(int duration);

    Set<String> configuredNetworks();
    void configuredNetworks(Set<String> networks);

}