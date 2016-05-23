package dor.only.dorking.android.stocksmarketsnotifier.Database;

/**
 * Created by Yoni on 5/16/2016.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of quotes
     */
    public List<Security> getStocksInfo(String subStringToLookFor) {
        List<Security> list = new ArrayList<>();
        //TODO change this line so SQL injection won't be possible (now it is def possible).
        final char quote='"';
        Cursor cursor = database.rawQuery("SELECT * FROM nasdaqcompanies WHERE name LIKE "+quote+"%"+subStringToLookFor+"%"+quote,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Security s=getSecurityFromCursor(cursor);
            list.add(s);

            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


    private static String getThisColumnFromCursor(String column,Cursor cursor){
        int index=cursor.getColumnIndex(column);
        if(index<0){return "";}
        return cursor.getString(index);


    }


    /**
     * Get a cursor to some security specifically in the nasdaq database, and return a security
     * Notice that this assumes a whole lot about the database (specifically column names are very rigid)
     *
     *
     * @return a Nasdaq security



     */
    private static Security getSecurityFromCursor(Cursor cursor){
        final String NAME="Name";
        final String SYMBOL="Symbol";
        final String SUMMARY_QUOTE="Summary_Quote";
        final String UNITED_STATES="UNITED_STATES";
        final String NASDAQ="NASDAQ";

        Security security=new Security();
        security.setCountry(UNITED_STATES);
        security.setSecurityType(Security.SECURITY_IS_STOCK);
        security.setMoreInfoUri(getThisColumnFromCursor(SUMMARY_QUOTE,cursor));
        security.setName(getThisColumnFromCursor(NAME,cursor));
        security.setStocksMarketName(NASDAQ);
        security.setTicker(getThisColumnFromCursor(SYMBOL,cursor));




        return security;

    }

}
