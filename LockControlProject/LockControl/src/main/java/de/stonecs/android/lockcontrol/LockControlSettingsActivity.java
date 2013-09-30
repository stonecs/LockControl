package de.stonecs.android.lockcontrol;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.List;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.receivers.WifiBroadcastReceiver;
import de.stonecs.android.lockcontrol.ui.preferences.WifiMultiSelectListPreference;
import de.stonecs.android.lockcontrol.unlockchain.actions.MaximizeWidgetsLockAction;
import de.stonecs.android.lockcontrol.unlockchain.actions.PatternDisableLockAction;
import de.stonecs.android.lockcontrol.util.TimeunitConversionUtil;

/**
 * A {@link android.preference.PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class LockControlSettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */

    private static final int RESULT_ADMIN_ENABLE = 1;
    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    @Inject
    DevicePolicyManager devicePolicyManager;

    @Inject
    ComponentName adminReceiverComponentName;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupSimplePreferencesScreen();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.inject(this);

        if (!devicePolicyManager.isAdminActive(adminReceiverComponentName)) {
            Intent intent = new Intent(DevicePolicyManager
                    .ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    adminReceiverComponentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Additional text explaining why this needs to be added.");
            startActivityForResult(intent, RESULT_ADMIN_ENABLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPreferenceSummaryToValue(findPreference("disableDuration"));
        findPreference("configuredNetworks").setOnPreferenceChangeListener
                (sBindPreferenceSummaryToValueListener);
        findPreference("useCompleteDisable").setOnPreferenceChangeListener
                (sBindPreferenceSummaryToValueListener);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);

        // Add 'wifi' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_wifi);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_wifi);

        // Add 'notifications' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_notifications);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_notification);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        /*
         * bindPreferenceSummaryToValue(findPreference("example_text"));
		 * bindPreferenceSummaryToValue(findPreference("example_list"));
		 * bindPreferenceSummaryToValue
		 * (findPreference("notifications_new_message_ringtone"));
		 */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link android.preference.PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    // todo make the listener react on changes inside other objects (e.g. HmsPickerActivity)
    private static Preference.OnPreferenceChangeListener
            sBindPreferenceSummaryToValueListener = new
            Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object
                        value) {
                    String stringValue = "";
                    if (preference.getKey().equals("disableDuration")) {
                        stringValue = TimeunitConversionUtil.getStringRepresentationFor(Integer.parseInt(String.valueOf(value)));
                    }
                    if (preference.getKey().equals("useCompleteDisable")) {
                        boolean isSelected = (Boolean) value;
                        PatternDisableLockAction patternDisableLockAction = App.getBean(PatternDisableLockAction.class);
                        if (patternDisableLockAction != null) {
                            patternDisableLockAction.setEnabled(!isSelected);
                        }
                        MaximizeWidgetsLockAction maximizeWidgetsLockAction = App.getBean(MaximizeWidgetsLockAction.class);
                        if (maximizeWidgetsLockAction != null) {
                            maximizeWidgetsLockAction.setEnabled(!isSelected);
                        }
                    }
                    if (preference instanceof WifiMultiSelectListPreference) {
                        Intent intent = new Intent(WifiBroadcastReceiver.APPLICATION_TRIGGERED_CHECK_ACTION);
                        preference.getContext().sendBroadcast(intent);
                    } else if (preference instanceof ListPreference) { // For list preferences, look up the correct display value in
                        stringValue = value.toString();
                        // the preference's 'entries' list.
                        ListPreference listPreference = (ListPreference) preference;
                        int index =
                                listPreference.findIndexOfValue(stringValue);

                        // Set the summary to reflect the new value.
                        preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
                    } else { // For all other preferences, set the summary to the value's simple string representation.
                        preference.setSummary(stringValue);
                    }
                    return true;
                }
            };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener
                (sBindPreferenceSummaryToValueListener);

        String value;
        if (preference.getKey().equals("disableDuration")) {
            // TODO use typed Esperandro preferences
            value = String.valueOf(PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getInt("disableDuration", 30));
        } else {
            value =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), "");
        }
        // Trigger the listener immediately with the preference's  current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value);
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            /*
             * bindPreferenceSummaryToValue(findPreference("example_text"));
			 * bindPreferenceSummaryToValue(findPreference("example_list"));
			 */
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends
            PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            /*
             * bindPreferenceSummaryToValue(findPreference(
			 * "notifications_new_message_ringtone"));
			 */
        }
    }


    /**
     * This fragment shows wifi preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class WifiPreferenceFragment extends
            PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_wifi);
        }
    }


}
