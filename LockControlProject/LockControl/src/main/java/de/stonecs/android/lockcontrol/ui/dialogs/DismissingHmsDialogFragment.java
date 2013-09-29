package de.stonecs.android.lockcontrol.ui.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.doomonafireball.betterpickers.hmspicker.HmsPickerDialogFragment;

import de.stonecs.android.lockcontrol.App;

/**
 * Created by Daniel on 29.09.13.
 * <p/>
 * TODO maybe make a pull request for easier extension of those pickers, or extend the available listeners, till now no way of getting notified for dimiss actions
 */
public class DismissingHmsDialogFragment extends HmsPickerDialogFragment {

    private static final String REFERENCE_KEY = "HmsPickerDialogFragment_ReferenceKey";
    private static final String THEME_RES_ID_KEY = "HmsPickerDialogFragment_ThemeResIdKey";

    public interface DismissListener{
        void onDismiss();
    }

    private DismissListener onDismissCallback;

    public void setOnDismissCallback(DismissListener callback) {
        this.onDismissCallback = callback;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(onDismissCallback != null){
            onDismissCallback.onDismiss();
        }
        super.onDismiss(dialog);
    }

    public static DismissingHmsDialogFragment newInstance(int reference, int themeResId) {
        final DismissingHmsDialogFragment fragment = new DismissingHmsDialogFragment();
        Bundle args = new Bundle();
        args.putInt(REFERENCE_KEY, reference);
        args.putInt(THEME_RES_ID_KEY, themeResId);
        fragment.setArguments(args);
        return fragment;

    }
}
