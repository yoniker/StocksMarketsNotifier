package dor.only.dorking.android.stocksmarketsnotifier.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowContract.FollowEntry;

/**
 * Created by Yoni on 6/1/2016.
 */
public class FollowsDbHelper extends SQLiteOpenHelper implements BaseColumns {
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "follows.db";

    public FollowsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        //TODO: Make it a content provider  so I can use a Loader / Service later on

        final String SQL_CREATE_FOLLOW_TABLE = "CREATE TABLE " +FollowEntry.TABLE_NAME + " (" +
               FollowEntry._ID + " INTEGER PRIMARY KEY," +
                FollowEntry.COLUMN_SECURITY_TICKER + " TEXT NOT NULL, " +
                FollowEntry.COLUMN_FOLLOW_TYPE + " TEXT NOT NULL, " +
                FollowEntry.COLUMN_PARAM1 + " REAL NOT NULL, " +
                FollowEntry.COLUMN_PARAM2 + " REAL NOT NULL, " +
                FollowEntry.COLUMN_PARAM3 + " REAL NOT NULL, " +
                FollowEntry.COLUMN_PARAM4 + " REAL NOT NULL, " +
                FollowEntry.COLUMN_PRICE_STARTED + " REAL NOT NULL, " +
                FollowEntry.COLUMN_DATE_STARTED + " INTEGER,"+
                FollowEntry.COLUMN_DATE_EXPIRY+" INTEGER,"+
                FollowEntry.COLUMN_STATUS + " TEXT " +
                " );";

        db.execSQL(SQL_CREATE_FOLLOW_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + FollowEntry.TABLE_NAME);
        onCreate(db);

    }
}
