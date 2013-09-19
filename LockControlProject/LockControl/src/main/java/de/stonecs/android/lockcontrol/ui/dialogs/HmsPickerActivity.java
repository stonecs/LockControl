package de.stonecs.android.lockcontrol.ui.dialogs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.doomonafireball.betterpickers.hmspicker.HmsPickerBuilder;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.R;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;
import de.stonecs.android.lockcontrol.ui.dialogs.HmsPickerFragment;

/**
 * Created by Daniel on 19.09.13.
 */
public class HmsPickerActivity extends FragmentActivity implements HmsPickerFragment.HmsPickedHandler {

    private static final String FRAGMENT_TAG = "picker_fragment";
    private static final int HOURS_TO_SECONDS = 3600;
    private static final int MINUTES_TO_SECONDS = 60;

    @Inject
    protected LockControlPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.inject(this);
        setContentView(R.layout.frame_layout);
        HmsPickerFragment pickerFragment = new HmsPickerFragment();
        pickerFragment.setHandler(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, pickerFragment, FRAGMENT_TAG);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HmsPickerFragment pickerFragment = (HmsPickerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        HmsPickerBuilder picker = pickerFragment.createPicker();
        picker.show();
    }

    @Override
    public void onHmsPicked(int reference, int hours, int minutes, int seconds) {
        Log.d("handler", "received selection");
        preferences.disableDuration((hours * HOURS_TO_SECONDS) + (minutes * MINUTES_TO_SECONDS) + seconds);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
