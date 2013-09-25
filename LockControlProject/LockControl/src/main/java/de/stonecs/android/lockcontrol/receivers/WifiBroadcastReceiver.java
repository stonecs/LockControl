package de.stonecs.android.lockcontrol.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.Date;
import java.util.Set;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.preferences.InternalPreferences;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;
import de.stonecs.android.lockcontrol.unlockchain.LockActionChain;
import de.stonecs.android.lockcontrol.util.PendingIntentRequestIdGenerator;

/**
 * Created by Daniel on 23.09.13.
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    private static final String EXTRA_RETRY_COUNT = "de.stonecs.android.lockcontrol.WifiBroadcastReceiver.extra.retryCount";

    public static final String APPLICATION_TRIGGERED_CHECK_ACTION = "de.stonecs.android.lockcontrol.WifiBroadcastReceiver.action.applicationTriggeredCheck";

    private static final int CHECK_FOR_VALID_BSSID = 0;

    private static final int maxBSSIDCheckRetryCount = 5;

    private static final long bssidCheckRetryMillis = 5000;

    private static final String regularBSSIDPattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

    private static final String irregularBSSIDPattern = "^([0]{2}[:-]){5}([0]{2})$";

    @Inject
    WifiManager wifi;

    @Inject
    AlarmManager alarmManager;

    @Inject
    LockControlPreferences preferences;

    @Inject
    InternalPreferences internalPreferences;

    @Inject
    PendingIntentRequestIdGenerator requestIdGenerator;

    @Inject
    LockActionChain chain;

    Context context;

    public WifiBroadcastReceiver() {
        App.inject(this);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    handleWifiConnect(intent);
                } else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    handleWifiDisconnect();
                }
            }
            // used for manual triggers
            else if (action.equals(APPLICATION_TRIGGERED_CHECK_ACTION)) {
                if (wifi.isWifiEnabled()) {
                    handleWifiConnect(intent);
                }
            }
        }
    }

    /**
     * Checks if the current connection is a) a known center access point and b) not the same
     * connection (bssid) as the last one. If both is fulfilled a new wifi connect event is generated
     * through the EventManagementService.
     *
     * @param intent
     */

    private void handleWifiConnect(Intent intent) {
        WifiInfo connectionInfo = wifi.getConnectionInfo();
        String receivedSsid = connectionInfo.getSSID();
        String bssid = connectionInfo.getBSSID();

        if (receivedSsid == null || !bssidIsValid(bssid)) {
            int retryCount = intent.getIntExtra(EXTRA_RETRY_COUNT, 0);
            initRetryMechanism(retryCount);
            return;
        }

        String ssid = stripSsid(receivedSsid).toLowerCase();
        Log.d(App.TAG, String.format("connected to wifi %s", ssid));
        Set<String> selectedNetworks = preferences.configuredNetworks();
        if(selectedNetworks != null && selectedNetworks.contains(String.valueOf(connectionInfo.getNetworkId()))){
            Log.d(App.TAG, String.format("wifi %s is one of the selected wifis", ssid));
            internalPreferences.connectedToSelectedWifi(true);
            chain.onUnlock();
        }

    }

    private void handleWifiDisconnect() {
        WifiInfo connectionInfo = wifi.getConnectionInfo();
        String ssid = getSsid().toLowerCase();
        Log.d(App.TAG, String.format("disconnected from wifi %s", ssid));
        Set<String> selectedNetworks = preferences.configuredNetworks();
        if(selectedNetworks != null && selectedNetworks.contains(String.valueOf(connectionInfo.getNetworkId()))){
            Log.d(App.TAG, String.format("wifi %s is one of the selected wifis", ssid));
            internalPreferences.connectedToSelectedWifi(false);
            chain.doLock();
        }
    }

    private String getSsid(){
        WifiInfo connectionInfo = wifi.getConnectionInfo();
        String receivedSsid = connectionInfo.getSSID();
        return stripSsid(receivedSsid);
    }

    private void initRetryMechanism(int retryCount) {
        if (retryCount < maxBSSIDCheckRetryCount) {

            Intent alarmIntent = new Intent(context, WifiBroadcastReceiver.class);
            alarmIntent.setAction(APPLICATION_TRIGGERED_CHECK_ACTION);
            alarmIntent.putExtra(EXTRA_RETRY_COUNT, ++retryCount);

            PendingIntent operation = PendingIntent.getBroadcast(context,
                    requestIdGenerator.generate(getClass(), CHECK_FOR_VALID_BSSID), alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            long alarmTimeMillis = new Date().getTime() + (retryCount * bssidCheckRetryMillis);
            alarmManager.set(AlarmManager.RTC, alarmTimeMillis, operation);
        }

    }

    /**
     * Checks if we have a valid bssid
     *
     * @param bssid the bssid to check
     * @return if we have a valid format and the bssid is not 00:00:00:...
     */
    private boolean bssidIsValid(String bssid) {
        boolean result = false;
        if (bssid != null) {
            boolean formatCorrect = bssid.matches(regularBSSIDPattern);
            boolean isValid = !bssid.matches(irregularBSSIDPattern);
            result = formatCorrect && isValid;
        }
        return result;
    }

    /**
     * Sometimes WifiManager returns a quoted ssid String ("ssid") instead of the unquoted ssid name.
     * In this case remove the quotes.
     *
     * @param ssid
     * @return
     */
    private String stripSsid(String ssid) {
        String resultSsid = ssid;
        String quote = "\"";
        if (ssid.startsWith(quote) && ssid.endsWith(quote)) {
            resultSsid = ssid.substring(1, ssid.length() - 1);
        }
        return resultSsid;
    }
}
