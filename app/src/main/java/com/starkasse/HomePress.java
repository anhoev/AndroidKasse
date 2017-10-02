package com.starkasse;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.os.ParcelFileDescriptor.MODE_WORLD_READABLE;

/**
 * Created by andy on 26/07/2017.
 */

public class HomePress {
    public static Intent GetDesiredIntent(Context c)
    {
        String s = "com.starkasse.kasse";
        return new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setPackage(s).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS| Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    }

    public static void Perform(Context c)
    {
        Intent i = GetDesiredIntent(c);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, i, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
