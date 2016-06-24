package dor.only.dorking.android.stocksmarketsnotifier.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.sql.Timestamp;

import dor.only.dorking.android.stocksmarketsnotifier.Contants.Constants;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.FollowAndStatus;

/**
 * Created by Yoni on 6/22/2016.
 */
 public class UtilityForDatabase {


    /**
     * updateFollowStatusInDatabase- updates  an *active* follow in the database,
     * an active follow being a follow which was not notified or archived
     *
     * @param theContext
     * @param theFollowAndStatus
     */

    static public void updateFollowInDatabase(Context theContext, FollowAndStatus theFollowAndStatus,ContentValues values){
        final String LOG_TAG=theContext.getClass().getSimpleName();

        //First lets check if the follow is already in the Database..
        //For now we are assuming that each security has at most one  follow so we can find it based on that
        //Let's find the security ID first (based on its ticker and stock market).

        //Something like this select _id,julianday('now')-julianday(date_started) as difference_date from follows_table where difference_date IN (select distinct min(julianday('now')-julianday(date_started)) as difference from follows_table);
        //is way too elaborated(and can cause huge amount of bugs later which will be hard to trace!)
        //So here is the plan of action: We will recognize the follow by its security+start date.
        //If nothing was found,then we will look for the follow by the same security+closest to the date..


        long securityId= Constants.getSecurityId(theContext,theFollowAndStatus.getFollow().getTheSecurity());
        if(securityId==Constants.SECURITY_NOT_FOUND){
            //If there is no such security that exists on the device, I am going to assume that the local device is more up-to-date than the server.
            Log.e(LOG_TAG,"Follow should already be in local database when updating its status!.");

            return;
        }

        Cursor followSearchResult=null;
        Cursor followClosestToStartDateResult=null;
        try{


        final String selectOnlyCurrentDate=FollowContract.FollowEntry.COLUMN_DATE_STARTED+"="+'"'+theFollowAndStatus.getFollow().getStart()+'"';
         followSearchResult=theContext.getContentResolver().query(FollowContract.FollowEntry.CONTENT_URI,
                new String[] {FollowContract.FollowEntry._ID},
                FollowContract.FollowEntry.COLUMN_SECURITY_ID+"=? and "+selectOnlyCurrentDate,
                new String[]{String.valueOf(securityId)},
                null);
        long idInContentProvider=0;
        if(followSearchResult.moveToFirst()){

             idInContentProvider=followSearchResult.getLong(0);
            if(!(idInContentProvider>0)){
                Log.e(LOG_TAG,"Found a follow with an illegal id");
                return;
            }
        } else{

            /*We are here only if no follow with the exact same startdate and security was found. Let's try to find the closest one */
             followClosestToStartDateResult=theContext.getContentResolver().query(FollowContract.FollowEntry.CONTENT_URI,
                    new String[] {FollowContract.FollowEntry._ID,FollowContract.FollowEntry.COLUMN_DATE_STARTED},
                    FollowContract.FollowEntry.COLUMN_SECURITY_ID+"=?",
                    new String[]{String.valueOf(securityId)},
                    null);
            if(followClosestToStartDateResult.getCount()<=0){
            Log.e(LOG_TAG,"Follow should already be in local database when updating it!");
            return;}
             long difference=Long.MAX_VALUE;
            Timestamp receivedTimeStamp=theFollowAndStatus.getFollow().getStart();
            while(followClosestToStartDateResult.moveToNext()){
           Timestamp iteratorTimeStamp= Timestamp.valueOf(followClosestToStartDateResult.getString(1));
                if(Math.abs(iteratorTimeStamp.getTime()-receivedTimeStamp.getTime())<difference){
                  difference=Math.abs(iteratorTimeStamp.getTime()-receivedTimeStamp.getTime());
                    idInContentProvider=followClosestToStartDateResult.getInt(0);

                }



            }






        }
        theContext.getContentResolver().update(FollowContract.FollowEntry.CONTENT_URI,
                values,
                FollowContract.FollowEntry._ID+"=?",
                new String[]{String.valueOf(idInContentProvider)});


    }finally{
            if(followClosestToStartDateResult!=null){followClosestToStartDateResult.close();}
            if(followSearchResult!=null){followSearchResult.close();}

        }}
}
