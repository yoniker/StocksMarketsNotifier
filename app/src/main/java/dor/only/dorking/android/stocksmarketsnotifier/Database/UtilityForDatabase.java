package dor.only.dorking.android.stocksmarketsnotifier.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

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

    static public void updateFollowStatusAndUriInDatabase(Context theContext, FollowAndStatus theFollowAndStatus){
        final String LOG_TAG=theContext.getClass().getSimpleName();

        //First lets check if the follow is already in the Database..
        //For now we are assuming that each security has at most one  follow so we can find it based on that
        //Let's find the security ID first (based on its ticker and stock market).


        long securityId= Constants.getSecurityId(theContext,theFollowAndStatus.getFollow().getTheSecurity());
        if(securityId==Constants.SECURITY_NOT_FOUND){
            //If there is no such security that exists on the device, I am going to assume that the local device is more up-to-date than the server.
            Log.e(LOG_TAG,"Follow should already be in local database when updating its status!.");

            return;
        }



        final String selectOnlyActiveFollows="not "+FollowContract.FollowEntry.COLUMN_STATUS+"=\""+FollowAndStatus.STATUS_HISTORY+'"';
        Cursor followSearchResult=theContext.getContentResolver().query(FollowContract.FollowEntry.CONTENT_URI,
                new String[] {FollowContract.FollowEntry._ID},
                FollowContract.FollowEntry.COLUMN_SECURITY_ID+"=? and "+selectOnlyActiveFollows,
                new String[]{String.valueOf(securityId)},
                null);
        if(followSearchResult.moveToFirst()){

            long idInContentProvider=followSearchResult.getLong(0);
            if(!(idInContentProvider>0)){
                Log.e(LOG_TAG,"Found a follow with an illegal id");
                return;
            }
            ContentValues values=new ContentValues();
            values.put(FollowContract.FollowEntry.COLUMN_URI_TO_SERVER,theFollowAndStatus.getFollowURIToServer());
            values.put(FollowContract.FollowEntry.COLUMN_STATUS,FollowAndStatus.STATUS_SENT_SUCCESSFULLY);
            theContext.getContentResolver().update(FollowContract.FollowEntry.CONTENT_URI,
                    values,
                    FollowContract.FollowEntry._ID+"=?",
                    new String[]{String.valueOf(idInContentProvider)});



        } else{
            Log.e(LOG_TAG,"Follow should already be in local database when updating it!");
            return;
        }

    }
}
