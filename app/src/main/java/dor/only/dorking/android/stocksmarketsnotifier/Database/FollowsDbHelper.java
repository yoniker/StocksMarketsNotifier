package dor.only.dorking.android.stocksmarketsnotifier.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowContract.*;

/**
 * Created by Yoni on 6/1/2016.
 */
public class FollowsDbHelper extends SQLiteOpenHelper implements BaseColumns {
    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "follows.db";

    public FollowsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FOLLOW_TABLE = "CREATE TABLE " +FollowEntry.TABLE_NAME + " (" +
               FollowEntry._ID + " INTEGER PRIMARY KEY," +
                FollowEntry.COLUMN_SECURITY_ID + " INTEGER, " +
                FollowEntry.COLUMN_FOLLOW_TYPE + " TEXT NOT NULL, " +
                FollowEntry.COLUMN_PARAM1 + " REAL NOT NULL, " +
                FollowEntry.COLUMN_PARAM2 + " REAL NOT NULL, " +
                FollowEntry.COLUMN_PARAM3 + " REAL NOT NULL, " +
                FollowEntry.COLUMN_PARAM4 + " REAL NOT NULL, " +
                FollowEntry.COLUMN_PRICE_STARTED + " REAL NOT NULL, " +
                FollowEntry.COLUMN_DATE_STARTED + " INTEGER,"+
                FollowEntry.COLUMN_DATE_EXPIRY+" INTEGER,"+
                FollowEntry.COLUMN_STATUS + " TEXT, " +
                FollowEntry.COLUMN_URI_TO_SERVER+" TEXT "+
                " );";

        final String SQL_CREATE_SECURITIES_TABLE="CREATE TABLE " + SecurityEntry.TABLE_NAME + " (" +
                SecurityEntry._ID + " INTEGER PRIMARY KEY," +
                SecurityEntry.COLUMN_SECURITY_NAME + " TEXT NOT NULL, " +
                SecurityEntry.COLUMN_COUNTRY + " TEXT NOT NULL, " +
                SecurityEntry.COLUMN_SECURITY_TYPE + " TEXT NOT NULL, " +
                SecurityEntry.COLUMN_STOCKMARKETNAME + " TEXT NOT NULL, " +
                SecurityEntry.COLUMN_TICKER + " TEXT NOT NULL, " +
                SecurityEntry.COLUMN_URI_INFO_LINK+" TEXT "+
                " );";

        final String SQL_CREATE_REQUESTS_TABLE="CREATE TABLE " + RequestEntry.TABLE_NAME + " (" +
                RequestEntry._ID + " INTEGER PRIMARY KEY," +
                RequestEntry.COLUMN_CONTENT + " TEXT, " +
                RequestEntry.COLUMN_HTTPMETHOD + " TEXT NOT NULL, " +
                RequestEntry.COLUMN_URL + " TEXT NOT NULL, " +
                RequestEntry.COLUMN_RESPONSE + " TEXT, " +
                RequestEntry.COLUMN_STATUS + " INTEGER," +
                RequestEntry.COLUMN_TRIES+" INTEGER"+
                " );";

        db.execSQL(SQL_CREATE_FOLLOW_TABLE);
        db.execSQL(SQL_CREATE_SECURITIES_TABLE);
        db.execSQL(SQL_CREATE_REQUESTS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + FollowEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SecurityEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RequestEntry.TABLE_NAME);

        onCreate(db);

    }
}
