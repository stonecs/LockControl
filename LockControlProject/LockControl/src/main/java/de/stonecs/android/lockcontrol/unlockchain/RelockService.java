package de.stonecs.android.lockcontrol.unlockchain;

import android.app.IntentService;
import android.content.Intent;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;

/**
 * Created by deekay on 20.09.13.
 */
public class RelockService extends IntentService {

    @Inject
    LockActionChain chain;

    public RelockService() {
        super("RelockService");
        App.inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        chain.doLock();
    }
}