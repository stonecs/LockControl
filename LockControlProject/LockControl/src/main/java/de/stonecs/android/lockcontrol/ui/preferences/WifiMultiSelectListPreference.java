package de.stonecs.android.lockcontrol.ui.preferences;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.R;

/**
 * Created by Daniel on 19.09.13.
 * TODO Check for wifi state changes in general to activate / deactivate the pref
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

        Set<String> strings = knownNetworks.keySet();
        String[] entryValues = strings.toArray(new String[strings.size()]);
        String[] entries = knownNetworks.values().toArray(new String[strings.size()]);

        setEntries(entries);
        setEntryValues(entryValues);
        if (!wifiManager.isWifiEnabled()) {
            setEnabled(false);
            setSummary(getContext().getResources().getString(R.string.wifi_disabled_information));
        }
    }

    /**
     * Returns needed information about all known wireless networks
     *
     * @return a id to ssid mapping of all known networks
     */
    protected Map<String, String> getKnownNetworks() {
        LinkedHashMap<String, String> knownNetworks = new LinkedHashMap<String, String>();
        if (wifiManager.isWifiEnabled()) {
            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
            Collections.sort(configuredNetworks, new Comparator<WifiConfiguration>() {
                @Override
                public int compare(WifiConfiguration left, WifiConfiguration right) {
                    return left.SSID.toLowerCase().compareTo(right.SSID.toLowerCase());
                }
            });
            for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                knownNetworks.put(String.valueOf(wifiConfiguration.networkId), wifiConfiguration.SSID.replaceAll("\"", ""));
            }
        }
        return knownNetworks;
    }
}
