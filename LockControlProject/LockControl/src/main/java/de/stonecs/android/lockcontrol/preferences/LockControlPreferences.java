package de.stonecs.android.lockcontrol.preferences;

import java.util.Set;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;

@SharedPreferences
public interface LockControlPreferences extends SharedPreferenceActions {

	@Default(ofString = "30")
	String disableDuration();
	void disableDuration(String duration);

    Set<String> configuredNetworks();
    void configuredNetworks(Set<String> networks);

}