package de.stonecs.android.lockcontrol.ui.preferences;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;

/**
 * Created by Daniel on 19.09.13.
 * TODO Check for connection with the selected wifis when the pref changes
 */
public class WifiMultiSelectListPreference extends MultiSelectListPreference {

    @Inject
    WifiManager wifiManager;

    public WifiMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        afterConstruct();
    }

    public WifiMultiSelectListPreference(Context context) {
        super(context);
        afterConstruct();
    }

    private void afterConstruct() {
        App.inject(this);
        Map<String, String> knownNetworks = getKnownNetworks();

        String[] entryValues =  knownNetworks.keySet().toArray(new String[0]);
        String[] entries = knownNetworks.values().toArray(new String[0]);

        setEntries(entries);
        setEntryValues(entryValues);
    }

    /**
     * Returns needed information about all known wireless networks
     * @return
     */
    protected Map<String, String> getKnownNetworks() {
        Map<String, String> knownNetworks = new HashMap<String, String>();
        for (WifiConfiguration wifiConfiguration : wifiManager.getConfiguredNetworks()) {
            knownNetworks.put(String.valueOf(wifiConfiguration.networkId), wifiConfiguration.SSID.replaceAll("\"", ""));
        }
        return knownNetworks;
    }
}