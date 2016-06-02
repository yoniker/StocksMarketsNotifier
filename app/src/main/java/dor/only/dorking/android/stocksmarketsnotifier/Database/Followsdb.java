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
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowContract.*;

/**
 * Created by Yoni on 6/1/2016.
 */
public class Followsdb {
    private FollowsDbHelper mDBHelper;
    private Context mContext;

    private static final long SECURITY_NOT_FOUND=-1;
    private static final long FOLLOW_NOT_FOUND=-1;



    public Followsdb(Context context){
        mContext=context;
        mDBHelper=new FollowsDbHelper(mContext);



    }


    /*Get security by ID */

    public Security getSecurityById(long id){
        Cursor theCursor=null;
        try{
            theCursor=mDBHelper.getReadableDatabase().query(SecurityEntry.TABLE_NAME,
                    null,
                    SecurityEntry._ID+"=?",
                    new String[]{String.valueOf(id)},
                    null, null, null
            );

            if(!theCursor.moveToFirst()){return null;}
            Security theSecurity=new Security();
            theSecurity.setName(theCursor.getString(theCursor.getColumnIndex(SecurityEntry.COLUMN_SECURITY_NAME)));
            theSecurity.setTicker(theCursor.getString(theCursor.getColumnIndex(SecurityEntry.COLUMN_TICKER)));
            theSecurity.setStocksMarketName(theCursor.getString(theCursor.getColumnIndex(SecurityEntry.COLUMN_STOCKMARKETNAME)));
            theSecurity.setCountry(theCursor.getString(theCursor.getColumnIndex(SecurityEntry.COLUMN_COUNTRY)));
            theSecurity.setSecurityType(theCursor.getString(theCursor.getColumnIndex(SecurityEntry.COLUMN_SECURITY_TYPE)));
            theSecurity.setMoreInfoUri(theCursor.getString(theCursor.getColumnIndex(SecurityEntry.COLUMN_URI_INFO_LINK)));
            return theSecurity;

        }

        finally{
            if(theCursor!=null){theCursor.close();}

        }


    }



    private long findSecurityId(Security theSecurity){


        Cursor theCursor=null;
        try{
        theCursor = mDBHelper.getReadableDatabase().query(SecurityEntry.TABLE_NAME,
                new String[]{SecurityEntry._ID},
                SecurityEntry.COLUMN_TICKER+"=?"+
                        " and "+SecurityEntry.COLUMN_STOCKMARKETNAME+"=?"+
                        " and "+SecurityEntry.COLUMN_COUNTRY+"=?",
                new String []{theSecurity.getTicker(),
                        theSecurity.getStocksMarketName(),
                        theSecurity.getCountry()},null,null,null);
        //If it exists already then just return the id
        if(theCursor.moveToFirst()){
            return theCursor.getLong(0); //there is just one column in the results set- the _ID column...
        }

            return SECURITY_NOT_FOUND;

    } finally {
            if(theCursor!=null){theCursor.close();}
        }
        }


    /* adds a security to the database and gives its id,or just gives its id if it already exists*/
    private long addToSecuritiesDB(Security theSecurity){

        long foundSecurityID=findSecurityId(theSecurity);
        if(foundSecurityID!=SECURITY_NOT_FOUND) {return foundSecurityID;}
        SQLiteDatabase db=null;
        try{



            db=mDBHelper.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(SecurityEntry.COLUMN_SECURITY_NAME,theSecurity.getName());
            values.put(SecurityEntry.COLUMN_TICKER,theSecurity.getTicker());
            values.put(SecurityEntry.COLUMN_COUNTRY,theSecurity.getCountry());
            values.put(SecurityEntry.COLUMN_SECURITY_TYPE,theSecurity.getSecurityType());
            values.put(SecurityEntry.COLUMN_STOCKMARKETNAME,theSecurity.getStocksMarketName());
            values.put(SecurityEntry.COLUMN_URI_INFO_LINK,theSecurity.getMoreInfoUri());
            return db.insert(SecurityEntry.TABLE_NAME,null,values);
        }

        finally {
            if(db!=null){db.close();}
        }




    }

    private long getFollowID(FollowAndStatus theFollow){
        if(theFollow==null) {return FOLLOW_NOT_FOUND;}
        Cursor cursor=null;

        long securityID= findSecurityId(theFollow.getFollow().getTheSecurity());

        try{
            cursor=mDBHelper.getReadableDatabase().query(FollowEntry.TABLE_NAME,
                    new String[]{FollowEntry._ID},
                    FollowEntry.COLUMN_SECURITY_ID+"=?",
                    new String[]{String.valueOf(securityID)},
                    null,
                    null,
                    null

                    );

            if(cursor.moveToFirst()){
                return cursor.getLong(0);

            }
            return FOLLOW_NOT_FOUND;

        }
        finally {
            if(cursor!=null) {
            cursor.close();
            }
        }

    }




    public void addToFollowsDB(FollowAndStatus theFollow){
        SQLiteDatabase db=null;
        try {
            ContentValues values = new ContentValues();
            Follow f = theFollow.getFollow();
            values.put(FollowEntry.COLUMN_PARAM1, f.getFollowParams()[0]);
            values.put(FollowEntry.COLUMN_PARAM2, f.getFollowParams()[1]);
            values.put(FollowEntry.COLUMN_PARAM3, 0);
            values.put(FollowEntry.COLUMN_PARAM4, 0);
            long securityId = addToSecuritiesDB(f.getTheSecurity());
            values.put(FollowEntry.COLUMN_SECURITY_ID, securityId);
            values.put(FollowEntry.COLUMN_DATE_STARTED, f.getStart().getTime());
            values.put(FollowEntry.COLUMN_DATE_EXPIRY, f.getExpiry().getTime());
            values.put(FollowEntry.COLUMN_FOLLOW_TYPE, f.getFollowType());
            values.put(FollowEntry.COLUMN_URI_TO_SERVER, theFollow.getFollowURIToServer());
            values.put(FollowEntry.COLUMN_STATUS, theFollow.getStatus());

            //Do we need to update or to insert? if follow exists then just update,if not then insert
            long currentFollowID = getFollowID(theFollow);
            db = mDBHelper.getWritableDatabase();


            if(currentFollowID==FOLLOW_NOT_FOUND){
                // If we insert eg it is new,then we can put the new price too.
                //TODO do it in a more logical way when changing it to a content provider :P
            values.put(FollowEntry.COLUMN_PRICE_STARTED, theFollow.getPriceStarted());
                db.insert(FollowEntry.TABLE_NAME, null, values);}
            else{
                db.update(FollowEntry.TABLE_NAME,values,
                        FollowEntry.COLUMN_SECURITY_ID+"=?",
                        new String []{String.valueOf(securityId)}
                        );


            }
        }
        finally {
            if(db!=null){db.close();}
        }


    }


    /*Find all follows of the security, or gives back every follow if null is passed as a security instead */
    public List<FollowAndStatus> getAllFollows(Security securityToFind){
        ArrayList<FollowAndStatus> theList=new ArrayList<FollowAndStatus>();
        Cursor theCursor=null;
        String selection=null;
        String[] selectionArgs=null;
        if(securityToFind!=null)
        {
            //To find all follows which point at a certain security,we just need to find all follows with the security's key,if it exists.

            long securityIDFound= findSecurityId(securityToFind);
            if(securityIDFound==SECURITY_NOT_FOUND) {
                //If there is no security like that in the securities database,there is no follow for sure,so just return the current empty list.
                return theList;
            }

                selection=FollowEntry.COLUMN_SECURITY_ID+"=?";
                selectionArgs=new String[]{String.valueOf(securityIDFound)};



        }
        try {
            theCursor = mDBHelper.getReadableDatabase().query(FollowEntry.TABLE_NAME, null/* Just get all columns-we are actually going to user every single column*/,
                    selection,
                    selectionArgs, null, null, null);
            if (theCursor.moveToFirst()) {
                do {

                    Follow theFollow=new Follow();
                    long securityID=theCursor.getLong(theCursor.getColumnIndex(FollowEntry.COLUMN_SECURITY_ID));
                    Security theSecurity=getSecurityById(securityID);
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
                    toPutInList.setFollowURIToServer(theCursor.getString(theCursor.getColumnIndex(FollowEntry.COLUMN_URI_TO_SERVER)));
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
