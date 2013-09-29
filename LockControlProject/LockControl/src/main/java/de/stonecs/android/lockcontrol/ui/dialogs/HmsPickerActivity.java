package de.stonecs.android.lockcontrol.ui.dialogs;

import android.app.FragmentManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.doomonafireball.betterpickers.hmspicker.HmsPickerBuilder;
import com.doomonafireball.betterpickers.hmspicker.HmsPickerDialogFragment;

import java.util.Vector;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.R;
import de.stonecs.android.lockcontrol.preferences.LockControlPreferences;
import de.stonecs.android.lockcontrol.util.TimeunitConversionUtil;

/**
 * Created by Daniel on 19.09.13.
 */
public class HmsPickerActivity extends FragmentActivity implements HmsPickerDialogFragment.HmsPickerDialogHandler, DismissingHmsDialogFragment.DismissListener {

    private static final String FRAGMENT_TAG = "picker_fragment";

    @Inject
    protected LockControlPreferences preferences;
    private android.support.v4.app.FragmentManager manager;
    private static Integer styleResId = R.style.BetterPickersDialogFragment_Light;
    private Vector<HmsPickerDialogFragment.HmsPickerDialogHandler> mHmsPickerDialogHandlers = new Vector<HmsPickerDialogFragment.HmsPickerDialogHandler>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        App.inject(this);
        manager = getSupportFragmentManager();
        mHmsPickerDialogHandlers.add(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        show();
    }

    @Override
    public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
        Log.d("handler", "received selection");
        preferences.disableDuration(TimeunitConversionUtil.getSecondsFor(hours, minutes, seconds));
        finish();
    }

    public void show(){
        final FragmentTransaction ft = manager.beginTransaction();
        final Fragment prev = manager.findFragmentByTag("hms_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        final DismissingHmsDialogFragment fragment = DismissingHmsDialogFragment.newInstance(0, styleResId);

        fragment.setHmsPickerDialogHandlers(mHmsPickerDialogHandlers);
        fragment.setOnDismissCallback(this);
        fragment.show(ft, "hms_dialog");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onDismiss() {
        finish();
    }
}
