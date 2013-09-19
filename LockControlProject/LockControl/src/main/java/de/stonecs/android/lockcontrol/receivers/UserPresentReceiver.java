package de.stonecs.android.lockcontrol.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.unlockchain.LockActionChain;

/**
 * Created by Daniel on 20.09.13.
 */
public class UserPresentReceiver extends BroadcastReceiver {


    @Inject
    LockActionChain chain;

    @Override
    public void onReceive(Context context, Intent intent) {
        App.inject(this);
        if ("android.intent.action.USER_PRESENT".equals(intent.getAction())) {
            chain.onUnlock();
        }
    }
}