
package com.acme.international.trading.cashbacktracker.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.acme.international.trading.cashbacktracker.AAUtils;
import com.acme.international.trading.cashbacktracker.CashbackProfile;

import java.util.ArrayList;
import java.util.List;

public class CashbackDba {

    private static final String TAG = "CashbackDba";

    private static CashbackDba mDba;

    // Query string constants to work with database.
    private static String PROFILE_SELECTION_BY_DATE = AAProvider.ProfileColumns.ORDER_DATE + " LIKE ? ";

    private static String PROFILE_SELECTION_BY_ID = AAProvider.ProfileColumns._ID + " LIKE ? ";

    public static String ID_SELECTION = BaseColumns._ID + "=?";

    public static CashbackDba getDB() {
        if (mDba == null)
            mDba = new CashbackDba();
        return mDba;
    }

    /**
     * Save order
     *
     * @param cr
     * @param profile
     * @return
     */
    public Uri saveAAProfile(ContentResolver cr, CashbackProfile profile) {
        if (profile == null) {
            return null;
        }

        ContentValues values = new ContentValues();
        AAUtils.toContentValues(profile, values);

        Log.d(TAG, "insert order " + profile.getDate());
        return cr.insert(AAProvider.ProfileColumns.CONTENT_URI, values);
    }

    public List<CashbackProfile> getAllProfile(ContentResolver cr) {
        List<CashbackProfile> profileList = new ArrayList<CashbackProfile>();

        CashbackProfile profile = null;
        Cursor cursor = null;

        try {
            cursor = cr.query(AAProvider.ProfileColumns.CONTENT_URI, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    profile = new CashbackProfile();
                    AAUtils.fromCursor(cursor, profile);
                    profileList.add(profile);
                } while (cursor.moveToNext());

            }
        } catch (SQLException e) {
            Log.e(TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return profileList;
    }

    public CashbackProfile getAAProfileById(ContentResolver cr, String id) {
        CashbackProfile profile = new CashbackProfile();
        Log.d(TAG, "{getAAProfile} the ID is : " + id);
        if (id == null)
            return null;

        Cursor cursor = null;

        try {
            cursor = cr.query(AAProvider.ProfileColumns.CONTENT_URI, null, PROFILE_SELECTION_BY_ID,
                    new String[]{
                            id
                    }, null);
            if (cursor != null && cursor.moveToFirst()) {
                AAUtils.fromCursor(cursor, profile);

            }
        } catch (SQLException e) {
            Log.e(TAG, "Error in retrieve Date: " + id + e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return profile;
    }

    public List<CashbackProfile> getAAProfile(ContentResolver cr, String Date) {
        List<CashbackProfile> profileList = new ArrayList<CashbackProfile>();
        Log.d(TAG, "{getAAProfile} the Date is : " + Date);
        if (Date == null)
            return null;

        CashbackProfile profile = null;
        Cursor cursor = null;

        try {
            cursor = cr.query(AAProvider.ProfileColumns.CONTENT_URI, null, PROFILE_SELECTION_BY_DATE,
                    new String[]{
                            Date
                    }, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    profile = new CashbackProfile();
                    AAUtils.fromCursor(cursor, profile);
                    profileList.add(profile);
                } while (cursor.moveToNext());

            }
        } catch (SQLException e) {
            Log.d(TAG, "Error in retrieve Date: " + Date, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return profileList;
    }

    public int deleteAAProfile(ContentResolver cr, CashbackProfile profile) {
        int count = 0;
        if (profile != null) {
            count = cr.delete(AAProvider.ProfileColumns.CONTENT_URI, ID_SELECTION, new String[]{
                    profile.getTempId()
            });
        }
        return count;
    }

    public int deleteAAProfile(ContentResolver cr, String id) {
        int count = 0;
        CashbackProfile profile = getAAProfileById(cr, id);
        if (profile != null) {
            count = cr.delete(AAProvider.ProfileColumns.CONTENT_URI, ID_SELECTION, new String[]{
                    profile.getTempId()
            });
        }
        return count;
    }
}
