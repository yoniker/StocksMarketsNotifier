package dor.only.dorking.android.stocksmarketsnotifier;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.test.ApplicationTestCase;

import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowContract;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testGetType()  {

        ContentValues values=new ContentValues();
        values.put(FollowContract.SecurityEntry.COLUMN_SECURITY_NAME,"Dorinfi");
        values.put(FollowContract.SecurityEntry.COLUMN_URI_INFO_LINK,"fff");
        values.put(FollowContract.SecurityEntry.COLUMN_STOCKMARKETNAME,"NAHSDAQ");
        values.put(FollowContract.SecurityEntry.COLUMN_TICKER,"DORK");
        values.put(FollowContract.SecurityEntry.COLUMN_SECURITY_TYPE,"Security");
        values.put(FollowContract.SecurityEntry.COLUMN_COUNTRY,"CARNADA");

        mContext.getContentResolver().insert(FollowContract.SecurityEntry.CONTENT_URI,values);
        assertTrue(true);
        String s="";
        Cursor theCursor=mContext.getContentResolver().query(FollowContract.SecurityEntry.CONTENT_URI,null,null,null,null);
        while(theCursor.moveToNext()){
            s+=theCursor.getString(theCursor.getColumnIndex(FollowContract.SecurityEntry.COLUMN_SECURITY_NAME));
            s=s;

        }

        //assertEquals("hmm","d",s);



    }
}