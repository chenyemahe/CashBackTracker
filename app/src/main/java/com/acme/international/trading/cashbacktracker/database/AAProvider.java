
package com.acme.international.trading.cashbacktracker.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

public class AAProvider extends ContentProvider {

    public static final String AUTHORITY = "com.acme.international.trading.cashbacktracker.provider";

    public static final String DB_NAME = "acme_cashback_tracker.db";

    private static final int DB_VERSION = 2;

    private static final String TAG = "AAProvider";

    // URL matcher path constants
    // Product order from merchant
    private static final int AA_PROFILE = 1;

    private static final int AA_PROFILE_ID = 2;

    private AADatabaseHelper mHelper;

    // Amazon Order profile
    public interface ProfileColumns extends BaseColumns {
        String TBL_AA_PROFILES = "aa_profiles";

        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/"
                + TBL_AA_PROFILES);

        String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.acme.profile";

        String ORDER_ID = "order_id";

        String ORDER_DATE = "order_date";

        String ORDER_STORE = "order_store";

        String ORDER_DETAIL = "order_detail";

        String ORDER_CASHBACK_COMPANY = "order_cashback_company";

        String ORDER_CASHBACK_STATE = "order_cashback_state";

        String ORDER_CASHBACK_PERCENT = "order_cashback_percent";

        String ORDER_CASHBACK_AMOUNT = "order_cashback_amount";

        String ORDER_TOTAL_COST = "order_total_cost";

        String ORDER_CATEGORY = "order_category";

        String ORDER_PRICE_CB_AVAILABLE = "order_price_cb_available";
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        String whichTable = getTable(uri);
        int count = db.delete(whichTable, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        String type = null;
        switch (urlMatcher.match(uri)) {
            case AA_PROFILE:
                type = ProfileColumns.CONTENT_TYPE;
                break;
        }
        return type;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        String whichTable = getTable(uri);
        if (values == null) {
            values = new ContentValues();
        }

        long rowId = db.insert(whichTable, BaseColumns._ID, values);

        if (rowId >= 0) {
            Uri url = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(url, null, false);
            return url;
        }

        throw new SQLiteException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        mHelper = new AADatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sort) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sortOrder = null;
        int match = urlMatcher.match(uri);
        switch (match) {
            case AA_PROFILE:
                qb.setTables(ProfileColumns.TBL_AA_PROFILES);
                sortOrder = (sort != null ? sort : ProfileColumns._ID);
                break;
            case AA_PROFILE_ID:
                qb.setTables(ProfileColumns.TBL_AA_PROFILES);
                qb.appendWhere("_id=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }

        Log.d(TAG,
                "Build query: " + qb.buildQuery(projection, selection, null, null, sortOrder, null));

        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        String whichTable = getTable(uri);

        int count;
        int match = urlMatcher.match(uri);
        switch (match) {
            case AA_PROFILE:
                count = db.update(whichTable, values, selection, selectionArgs);
                break;
            case AA_PROFILE_ID:
                String segment = uri.getPathSegments().get(1);
                count = db
                        .update(whichTable, values,
                                "_id="
                                        + segment
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                                + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }

        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null, false);

        return count;
    }

    public static class AADatabaseHelper extends SQLiteOpenHelper {

        // private Context ctx;
        public AADatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            // this.ctx = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Profi.les
            db.execSQL("CREATE TABLE " + ProfileColumns.TBL_AA_PROFILES + " (" + ProfileColumns._ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ProfileColumns.ORDER_ID
                    + " VARCHAR, " + ProfileColumns.ORDER_DATE + " VARCHAR, "
                    + ProfileColumns.ORDER_STORE + " VARCHAR, " + ProfileColumns.ORDER_DETAIL
                    + " VARCHAR, " + ProfileColumns.ORDER_CASHBACK_COMPANY + " VARCHAR, "
                    + ProfileColumns.ORDER_CASHBACK_STATE + " VARCHAR, "
                    + ProfileColumns.ORDER_CASHBACK_PERCENT + " VARCHAR, "
                    + ProfileColumns.ORDER_CASHBACK_AMOUNT + " VARCHAR, "
                    + ProfileColumns.ORDER_CATEGORY + " VARCHAR, "
                    + ProfileColumns.ORDER_TOTAL_COST + " VARCHAR);");
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "Downgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            switch (oldVersion) {
                case 1:
                    try {
                        // upgrade to DB version 2
                        db.execSQL("ALTER TABLE " + ProfileColumns.TBL_AA_PROFILES + " ADD " + ProfileColumns.ORDER_PRICE_CB_AVAILABLE + " varchar");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
            }
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            Log.i(TAG, "Database has been opened for operations.");
        }
    }

    private static final UriMatcher urlMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        urlMatcher.addURI(AUTHORITY, ProfileColumns.TBL_AA_PROFILES, AA_PROFILE);
        urlMatcher.addURI(AUTHORITY, ProfileColumns.TBL_AA_PROFILES + "/#", AA_PROFILE_ID);
    }

    /**
     * Get the targeting table based on given url match.
     * 
     * @param uri uri for the table
     * @return table
     */
    private String getTable(Uri uri) {
        String whichTable;
        int match = urlMatcher.match(uri);
        switch (match) {
            case AA_PROFILE:
            case AA_PROFILE_ID:
                whichTable = ProfileColumns.TBL_AA_PROFILES;
                break;
            default:
                throw new IllegalArgumentException("Unknown URL: " + uri);
        }

        Log.d(TAG, "Selected table is " + whichTable);
        return whichTable;
    }
}
