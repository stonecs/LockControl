package de.stonecs.android.lockcontrol.ui.preferences;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

import de.stonecs.android.lockcontrol.ui.dialogs.HmsPickerActivity;

/**
 * Created by Daniel on 19.09.13.
 */
public class TimeSelectionPreference extends Preference {

    public TimeSelectionPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public TimeSelectionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeSelectionPreference(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        Intent pickActivity = new Intent(getContext(), HmsPickerActivity.class);
        getContext().startActivity(pickActivity);
    }

}
