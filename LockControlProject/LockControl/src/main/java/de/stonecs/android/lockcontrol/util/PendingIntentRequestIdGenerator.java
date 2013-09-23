package de.stonecs.android.lockcontrol.util;


import java.util.Random;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.dagger.qualifiers.ForApplication;
import de.stonecs.android.lockcontrol.preferences.InternalPreferences;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;


/**
 * Created by Daniel on 23.09.13.
 */
public class PendingIntentRequestIdGenerator {

    public static final int UNSAVED = 0;
    @Inject
    @ForApplication
    Context context;

    @Inject
    InternalPreferences internalPreferences;

    @Inject
    public PendingIntentRequestIdGenerator() {
    }


    public int generate(Class<?> clazz, int requestId) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
        } catch (NameNotFoundException unchecked) {
        }
        int uid = applicationInfo.uid;
        int baseSeed = getBaseSeed();
        int classHash = clazz.getName().hashCode();

        int hash = uid * baseSeed * classHash;

        return hash + requestId;
    }


    private int getBaseSeed() {
        int baseSeed = internalPreferences.pendingIntentBaseSeed();
        Random random = new Random();
        while (baseSeed == UNSAVED) {
            baseSeed = random.nextInt();
            internalPreferences.pendingIntentBaseSeed(baseSeed);
        }
        return baseSeed;
    }
}

