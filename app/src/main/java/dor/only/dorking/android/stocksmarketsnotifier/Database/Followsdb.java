package dor.only.dorking.android.stocksmarketsnotifier.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Follow;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.FollowAndStatus;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowContract.FollowEntry;

/**
 * Created by Yoni on 6/1/2016.
 */
public class Followsdb {
    private FollowsDbHelper mDBHelper;
    private Context mContext;
    private static String [] ALL_FOLLOW_COLUMNS={FollowEntry.COLUMN_SECURITY_TICKER,FollowEntry.COLUMN_PRICE_STARTED,FollowEntry.COLUMN_DATE_STARTED,FollowEntry.COLUMN_DATE_EXPIRY
            ,FollowEntry.COLUMN_FOLLOW_TYPE,FollowEntry.COLUMN_PARAM1,FollowEntry.COLUMN_PARAM2,FollowEntry.COLUMN_STATUS};

    public Followsdb(Context context){
        mContext=context;
        mDBHelper=new FollowsDbHelper(mContext);



    }

    public void addToFollowsDB(FollowAndStatus theFollow){
        SQLiteDatabase db=null;
        try{
         db=mDBHelper.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(FollowEntry.COLUMN_STATUS,theFollow.getStatus());
            values.put(FollowEntry.COLUMN_PRICE_STARTED,theFollow.getPriceStarted());
            Follow f=theFollow.getFollow();
            values.put(FollowEntry.COLUMN_SECURITY_TICKER,f.getTheSecurity().getTicker());
            values.put(FollowEntry.COLUMN_DATE_STARTED,f.getStart().getTime());
            values.put(FollowEntry.COLUMN_DATE_EXPIRY,f.getExpiry().getTime());
            values.put(FollowEntry.COLUMN_FOLLOW_TYPE,f.getFollowType());
            values.put(FollowEntry.COLUMN_PARAM1,f.getFollowParams()[0]);
            values.put(FollowEntry.COLUMN_PARAM2,f.getFollowParams()[1]);
            values.put(FollowEntry.COLUMN_PARAM3,0);
            values.put(FollowEntry.COLUMN_PARAM4,0);
            long id= db.insert(FollowEntry.TABLE_NAME,null,values);
             if( id>0){}



        }
        finally {
            if(db!=null){db.close();}
        }


    }

    public List<FollowAndStatus> getAllFollows(){
        ArrayList<FollowAndStatus> theList=new ArrayList<FollowAndStatus>();
        Cursor theCursor=null;
        try {
            theCursor = mDBHelper.getReadableDatabase().query(FollowEntry.TABLE_NAME, ALL_FOLLOW_COLUMNS, null, null, null, null, null);
            if (theCursor.moveToFirst()) {
                do {

                    Follow theFollow=new Follow();
                    Security theSecurity=new Security();
                    //TODO Very assumptive, change this code after implementing a local database for securities (along with changing what that we actually save to a foreign key)
                    theSecurity.setCountry(Security.USA);
                    theSecurity.setStocksMarketName(Security.STOCKMARKET_NASDAQ);
                    theSecurity.setTicker(theCursor.getString(theCursor.getColumnIndex(FollowEntry.COLUMN_SECURITY_TICKER)));
                    theFollow.setTheSecurity(theSecurity);

                    theFollow.setStart(new Timestamp(theCursor.getLong(theCursor.getColumnIndex(FollowEntry.COLUMN_DATE_STARTED))));
                    theFollow.setExpiry(new Timestamp(theCursor.getLong(theCursor.getColumnIndex(FollowEntry.COLUMN_DATE_EXPIRY))));
                    theFollow.setFollowType(theCursor.getString(theCursor.getColumnIndex(FollowEntry.COLUMN_FOLLOW_TYPE)));
                    double[] theParameters=new double[2];
                    theParameters[0]=theCursor.getDouble(theCursor.getColumnIndex(FollowEntry.COLUMN_PARAM1));
                    theParameters[1]=theCursor.getDouble(theCursor.getColumnIndex(FollowEntry.COLUMN_PARAM2));
                    theFollow.setFollowParams(theParameters);
                    String status=theCursor.getString(theCursor.getColumnIndex(FollowEntry.COLUMN_STATUS));
                    FollowAndStatus toPutInList=new FollowAndStatus();
                    toPutInList.setFollow(theFollow);
                    toPutInList.setStatus(status);
                    toPutInList.setPriceStarted(theCursor.getDouble(theCursor.getColumnIndex(FollowEntry.COLUMN_PRICE_STARTED)));
                    theList.add(toPutInList);

                } while (theCursor.moveToNext());


            }
        }

        finally {
            theCursor.close();
        }


        return theList;

    }
}
