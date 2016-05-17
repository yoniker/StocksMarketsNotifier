package dor.only.dorking.android.stocksmarketsnotifier.Database;

/**
 * Created by Yoni on 5/16/2016.
 *
 * This class helps opening the existing database where the Nasdaq stocks appear
 */
import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "nasdaq_companies.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}