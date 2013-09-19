package de.stonecs.android.lockcontrol.ui.dialogs;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.doomonafireball.betterpickers.hmspicker.HmsPickerBuilder;
import com.doomonafireball.betterpickers.hmspicker.HmsPickerDialogFragment;
import de.stonecs.android.lockcontrol.R;

/**
 * Created by Daniel on 19.09.13.
 * TODO react on back presses when dialog is opened
 */
public class HmsPickerFragment extends Fragment implements HmsPickerDialogFragment.HmsPickerDialogHandler{

    public void setHandler(HmsPickedHandler handler) {
        this.handler = handler;
    }

    protected HmsPickedHandler handler;

    public HmsPickerBuilder createPicker(){
        HmsPickerBuilder hmsPicker = new HmsPickerBuilder()
                .setFragmentManager(getChildFragmentManager())
                .setStyleResId(R.style.BetterPickersDialogFragment_Light)
                .setTargetFragment(HmsPickerFragment.this);
        return hmsPicker;
    }

    @Override
    public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
        Log.d("HMS", "date set");
        if(handler!= null){
            handler.onHmsPicked(reference, hours, minutes, seconds);
        }
    }

    public interface HmsPickedHandler {
        void onHmsPicked(int reference, int hours, int minutes, int seconds);
    }
}
