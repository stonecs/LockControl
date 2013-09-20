package de.stonecs.android.lockcontrol.unlockchain;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import de.stonecs.android.lockcontrol.App;
import de.stonecs.android.lockcontrol.dagger.qualifiers.ForApplication;

/**
 * Created by deekay on 20.09.13.
 */
public class PatternDisableLockAction implements PrioritizedLockAction {

    @Inject
    @ForApplication
    Context context;

    @Inject
    public PatternDisableLockAction() {
    }

    @Override
    public int getUnlockPriority() {
        return 2000;
    }

    @Override
    public int getLockPriority() {
        return 2000;
    }

    @Override
    public boolean onUnlock() {
        Log.d(App.TAG, "Disable pattern lock via root");
        boolean alreadyDisabled = false;
        try {
            alreadyDisabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCK_PATTERN_ENABLED) == 0;
        } catch (Settings.SettingNotFoundException e) {
            Log.w(App.TAG, String.format("Could not read settings value for '%s'", Settings.Secure.LOCK_PATTERN_ENABLED));
        }

        if (!alreadyDisabled) {
            Shell rootShell = getRootShell();
            try {
                setLocksettingsDBWritable(rootShell);
                setPatternAutolock(false);
                setLocksettingsDBSecured(rootShell);
            } finally {
                if (rootShell != null) {
                    try {
                        rootShell.close();
                    } catch (IOException e) {
                        Log.e(App.TAG, "Caught exception while closing shell.", e);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean doLock() {
        Log.d(App.TAG, "Enable pattern lock via root");

        boolean alreadyEnabled = false;
        try {
            alreadyEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCK_PATTERN_ENABLED) == 1;
        } catch (Settings.SettingNotFoundException e) {
            Log.w(App.TAG, String.format("Could not read settings value for '%s'", Settings.Secure.LOCK_PATTERN_ENABLED));
        }

        if (!alreadyEnabled) {
            Shell rootShell = getRootShell();
            try {
                setLocksettingsDBWritable(rootShell);
                setPatternAutolock(true);
                setLocksettingsDBSecured(rootShell);
            } finally {
                if (rootShell != null) {
                    try {
                        rootShell.close();
                    } catch (IOException e) {
                        Log.e(App.TAG, "Caught exception while closing shell.", e);
                    }
                }
            }
        }
        return false;
    }

    private void setLocksettingsDBWritable(Shell shell) {
        try {
            if (shell != null) {
                shell.add(new CommandCapture(0, new StringBuilder("chown ").append(android.os.Process.myUid()).append(" /data/system/locksettings"
                        + ".db*\n").toString())).waitForFinish();
                shell.add(new CommandCapture(0, "chmod 0660 /data/system/locksettings.db-*\n")).waitForFinish();
            }
        } catch (IOException e) {
            Log.e(App.TAG, "Caught exception while setting locksettings DB writable.", e);
        } catch (InterruptedException e) {
            Log.e(App.TAG, "Caught exception while setting locksettings DB writable.", e);
        }
    }

    private void setLocksettingsDBSecured(Shell shell) {
        try {
            if (shell != null) {
                shell.add(new CommandCapture(0, "chmod 0600 /data/system/locksettings.db-*\n")).waitForFinish();
                shell.add(new CommandCapture(0, "chown 1000 /data/system/locksettings.db-*\n")).waitForFinish();
                shell.close();
            }
        } catch (IOException e) {
            Log.e(App.TAG, "Caught exception while setting locksettings DB secured.", e);
        } catch (InterruptedException e) {
            Log.e(App.TAG, "Caught exception while setting locksettings DB secured.", e);
        }
    }

    private Shell getRootShell() {
        Shell shell = null;
        try {
            shell = RootTools.getShell(true);
        } catch (RootDeniedException e) {
            // TODO request root via notification
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return shell;
    }

    private void setPatternAutolock(boolean patternEnabled) {
        SQLiteDatabase sqlitedatabase1 = SQLiteDatabase.openDatabase("/data/system/locksettings" + ".db", null,
                SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING);
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("value", patternEnabled ? 1 : 0);
        sqlitedatabase1.beginTransaction();
        String as[] = new String[1];
        as[0] = "lock_pattern_autolock";
        if (sqlitedatabase1.update("locksettings", contentvalues, "name = ?", as) == 0) {
            contentvalues.put("name", "lock_pattern_autolock");
            sqlitedatabase1.insert("locksettings", null, contentvalues);
        }
        sqlitedatabase1.setTransactionSuccessful();
        sqlitedatabase1.endTransaction();
        sqlitedatabase1.close();
    }

}
