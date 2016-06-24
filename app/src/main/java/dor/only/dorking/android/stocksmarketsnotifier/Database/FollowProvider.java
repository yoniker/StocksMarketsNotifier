package dor.only.dorking.android.stocksmarketsnotifier.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.net.UrlQuerySanitizer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

import dor.only.dorking.android.stocksmarketsnotifier.ConnectionServer.RequestToServer;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Follow;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.FollowAndStatus;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowContract.FollowEntry;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowContract.SecurityEntry;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowContract.RequestEntry;

/**
 * Created by Yoni on 6/1/2016.
 */
public class FollowProvider extends ContentProvider {

    private FollowsDbHelper mDBHelper;


    static final int FOLLOW = 100;
    static final int FOLLOW_WITH_ID = 101;
    static final int SECURITY_WITH_ID = 102;
    static final int SECURITY = 300;
    static final int REQUEST=103;

    static final int FOLLOW_WITH_SECURITY=301;


    //follow._id=?
    private static final String sFollowWithId= FollowEntry.TABLE_NAME+"."+FollowEntry._ID+"=?";

    //security._id=?
    private static final String sSecurityWithId=SecurityEntry.TABLE_NAME+"."+SecurityEntry._ID+"=?";




    private static final UriMatcher sUriMatcher = buildUriMatcher();


    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FollowContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, FollowContract.PATH_FOLLOW, FOLLOW);
        matcher.addURI(authority, FollowContract.PATH_FOLLOW + "/#", FOLLOW_WITH_ID);
        matcher.addURI(authority, FollowContract.PATH_SECURITY , SECURITY);

        matcher.addURI(authority, FollowContract.PATH_SECURITY+"/#", SECURITY_WITH_ID);
        matcher.addURI(authority,FollowContract.PATH_FOLLOW+"/"+FollowContract.PATH_SECURITY,FOLLOW_WITH_SECURITY);
        matcher.addURI(authority,FollowContract.PATH_REQUEST,REQUEST);
        return matcher;
    }

    private static final SQLiteQueryBuilder sFollowBySecurityQueryBuilder;

    static{
        sFollowBySecurityQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sFollowBySecurityQueryBuilder.setTables(
               FollowEntry.TABLE_NAME + " INNER JOIN " +
                        SecurityEntry.TABLE_NAME +
                        " ON " + FollowEntry.TABLE_NAME +
                        "." + FollowEntry.COLUMN_SECURITY_ID +
                        " = " + SecurityEntry.TABLE_NAME +
                        "." + SecurityEntry._ID);
    }






    private   Cursor getFollowWithId(Uri uri,String[] projection,String selection,String[] selectionArgs){

        String id=String.valueOf( ContentUris.parseId(uri));

        if(selection==null || selection.equals("")){

         return mDBHelper.getReadableDatabase().query(FollowEntry.TABLE_NAME,projection,sFollowWithId,new String[]{id},null,null,null);

     }

        ArrayList<String> args= new ArrayList<String>(Arrays.asList(selectionArgs));
        args.add(id);
        return mDBHelper.getReadableDatabase().query(FollowEntry.TABLE_NAME,projection,selection+" and "+sFollowWithId,(String[])args.toArray(),null,null,null);



    }

    private Cursor getSecurityWithId(Uri uri,String[] projection,String selection,String [] selectionArgs){
        String id=String.valueOf(ContentUris.parseId(uri));
        if(selection==null || selection.equals("")){
            return mDBHelper.getReadableDatabase().query(SecurityEntry.TABLE_NAME,projection,sSecurityWithId,new String[]{id},null,null,null);
        }

        ArrayList<String> args=new ArrayList<String>(Arrays.asList(selectionArgs));
        args.add(id);
        return mDBHelper.getReadableDatabase().query(SecurityEntry.TABLE_NAME,projection,selection+" and "+sSecurityWithId,(String[])args.toArray(),null,null,null);
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case SECURITY:
                return SecurityEntry.CONTENT_TYPE;
            case FOLLOW:
                return FollowEntry.CONTENT_TYPE;
            case SECURITY_WITH_ID:
                return SecurityEntry.CONTENT_ITEM_TYPE;
            case FOLLOW_WITH_ID:
                return FollowEntry.CONTENT_ITEM_TYPE;
            case FOLLOW_WITH_SECURITY:
                return FollowEntry.CONTENT_TYPE;
            case REQUEST:
                return FollowContract.RequestEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private Cursor getFollowsBySecurity(Uri uri,String[] projection,String selection,String[] selectionArgs,String sortOrder) {

        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
        sanitizer.setAllowUnregisteredParamaters(true);
        sanitizer.parseUrl(uri.toString());
        String stocksMarketName= sanitizer.getValue(FollowContract.QUERY_KEY_SECURITY_STOCKS_MARKET);
        String ticker= sanitizer.getValue(FollowContract.QUERY_KEY_SECURITY_TICKER);


        if(selection==null || selection.length()<1) selection="1"; //this is 'true' in sqlite
               String finalSelection="( "+selection+ " ) and " +FollowContract.sSecurityDetails;
        String[] finalselectionArgs=new String[selectionArgs==null? 2:selectionArgs.length+2];
        if(selectionArgs!=null){
            for(int i=0; i<selectionArgs.length; ++i){
                finalselectionArgs[i]=selectionArgs[i];

            }


        }
        finalselectionArgs[finalselectionArgs.length-2]=ticker;
        finalselectionArgs[finalselectionArgs.length-1]=stocksMarketName;

        if(stocksMarketName==null||ticker==null||ticker.equals("")){
            finalselectionArgs=selectionArgs;
            finalSelection=selection;

        }


        return sFollowBySecurityQueryBuilder.query(mDBHelper.getReadableDatabase(),projection,finalSelection,finalselectionArgs,null,null,sortOrder);


    }


    public static Security cursorToSecurity(Cursor theCursor){
        Security securityReturn=new Security();
        securityReturn.setName(theCursor.getString(theCursor.getColumnIndex(SecurityEntry.COLUMN_SECURITY_NAME)));
        securityReturn.setStocksMarketName(theCursor.getString(theCursor.getColumnIndex(SecurityEntry.COLUMN_STOCKMARKETNAME)));
        securityReturn.setTicker(theCursor.getString(theCursor.getColumnIndex(SecurityEntry.COLUMN_TICKER)));
        securityReturn.setCountry(theCursor.getString(theCursor.getColumnIndex(SecurityEntry.COLUMN_COUNTRY)));
        securityReturn.setMoreInfoUri(theCursor.getString(theCursor.getColumnIndex(SecurityEntry.COLUMN_URI_INFO_LINK)));
        securityReturn.setSecurityType(theCursor.getString(theCursor.getColumnIndex(SecurityEntry.COLUMN_SECURITY_TYPE)));

        return securityReturn;

    }


    public static FollowAndStatus cursorToFollowAndStatus(Cursor theCursor){
        FollowAndStatus followAndStatusReturn=new FollowAndStatus();
        Follow followReturn=new Follow();
        followReturn.setTheSecurity(cursorToSecurity(theCursor));
        followReturn.setFollowType(theCursor.getString(theCursor.getColumnIndex(FollowEntry.COLUMN_FOLLOW_TYPE)));

        followReturn.setStart(Timestamp.valueOf(theCursor.getString(theCursor.getColumnIndex(FollowEntry.COLUMN_DATE_STARTED))));
        followReturn.setExpiry(Timestamp.valueOf(theCursor.getString(theCursor.getColumnIndex(FollowEntry.COLUMN_DATE_EXPIRY))));
        double[] followParam=new double[Follow.NUMBER_OF_PARAMETERS];
        followParam[0]=theCursor.getDouble(theCursor.getColumnIndex(FollowEntry.COLUMN_PARAM1));
        followParam[1]=theCursor.getDouble(theCursor.getColumnIndex(FollowEntry.COLUMN_PARAM2));
        followParam[2]=theCursor.getDouble(theCursor.getColumnIndex(FollowEntry.COLUMN_PARAM3));
        followParam[3]=theCursor.getDouble(theCursor.getColumnIndex(FollowEntry.COLUMN_PARAM4));
        followReturn.setFollowParams(followParam);
        followAndStatusReturn.setFollow(followReturn);
        followAndStatusReturn.setFollowURIToServer(theCursor.getString(theCursor.getColumnIndex(FollowEntry.COLUMN_URI_TO_SERVER)));
        followAndStatusReturn.setStatus(theCursor.getString(theCursor.getColumnIndex(FollowEntry.COLUMN_STATUS)));
        followAndStatusReturn.setPriceStarted(theCursor.getDouble(theCursor.getColumnIndex(FollowEntry.COLUMN_PRICE_STARTED)));
        followAndStatusReturn.setFinalPrice(theCursor.getDouble(theCursor.getColumnIndex(FollowEntry.COLUMN_FINAL_PRICE)));

        return followAndStatusReturn;


    }


    public static ContentValues requestContentValues(RequestToServer theRequest){
        ContentValues values=new ContentValues();
        values.put(RequestEntry.COLUMN_CONTENT,theRequest.getContent());
        values.put(RequestEntry.COLUMN_HTTPMETHOD,theRequest.getHttpMethod());
        values.put(RequestEntry.COLUMN_RESPONSE,theRequest.getResponse());
        values.put(RequestEntry.COLUMN_STATUS,theRequest.getStatus());
        values.put(RequestEntry.COLUMN_URL,theRequest.getUrl());
        values.put(RequestEntry.COLUMN_TRIES,theRequest.getNumberOfTries());

    return values;
    }



    public static ContentValues securityContentValues(Security theSecurity){
        ContentValues values=new ContentValues();
        values.put(SecurityEntry.COLUMN_SECURITY_NAME,theSecurity.getName());
        values.put(SecurityEntry.COLUMN_TICKER,theSecurity.getTicker());
        values.put(SecurityEntry.COLUMN_SECURITY_TYPE,theSecurity.getSecurityType());
        values.put(SecurityEntry.COLUMN_STOCKMARKETNAME,theSecurity.getStocksMarketName());
        values.put(SecurityEntry.COLUMN_URI_INFO_LINK,theSecurity.getMoreInfoUri());
        values.put(SecurityEntry.COLUMN_COUNTRY,theSecurity.getCountry());
        return values;


    }


    //Convenience method, adds the info which exists in the follows database only
    public static ContentValues followAndStatusContentValues(FollowAndStatus theFollowAndStatus){
        Follow theFollow=theFollowAndStatus.getFollow();
        double[] theArray=theFollow.getFollowParams();
        ContentValues values=new ContentValues();
        values.put(FollowEntry.COLUMN_PARAM1,theArray[0]);
        values.put(FollowEntry.COLUMN_PARAM2,theArray[1]);
        values.put(FollowEntry.COLUMN_PARAM3,theArray[2]);
        values.put(FollowEntry.COLUMN_PARAM4,theArray[3]);
        values.put(FollowEntry.COLUMN_DATE_STARTED,theFollow.getStart().toString());
        values.put(FollowEntry.COLUMN_DATE_EXPIRY,theFollow.getExpiry().toString());
        values.put(FollowEntry.COLUMN_FOLLOW_TYPE,theFollow.getFollowType());
        values.put(FollowEntry.COLUMN_PRICE_STARTED,theFollowAndStatus.getPriceStarted());
        values.put(FollowEntry.COLUMN_STATUS,theFollowAndStatus.getStatus());
        values.put(FollowEntry.COLUMN_URI_TO_SERVER,theFollowAndStatus.getFollowURIToServer());
        values.put(FollowEntry.COLUMN_FINAL_PRICE,theFollowAndStatus.getFinalPrice());
        return values;

    }




    @Override
    public boolean onCreate() {
        mDBHelper = new FollowsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case FOLLOW:
            {
                retCursor = mDBHelper.getReadableDatabase().query(FollowEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }

            case SECURITY: {
                retCursor = mDBHelper.getReadableDatabase().query(SecurityEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }

            case REQUEST:{
                retCursor=mDBHelper.getReadableDatabase().query(FollowContract.RequestEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }

            case FOLLOW_WITH_ID: {


                retCursor= getFollowWithId(uri,projection,selection,selectionArgs);
                break;


            }
            case SECURITY_WITH_ID: {

                retCursor=getSecurityWithId(uri,projection,selection,selectionArgs);
                break;
            }

            case FOLLOW_WITH_SECURITY:{
                retCursor=getFollowsBySecurity(uri,projection,selection,selectionArgs,sortOrder);
                break;


            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case SECURITY: {
                long _id = db.insert(SecurityEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = SecurityEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(_id)).build();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FOLLOW: {
                long _id = db.insert(FollowEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = FollowEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(_id)).build();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case REQUEST: {
                long _id = db.insert(FollowContract.RequestEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = FollowContract.RequestEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(_id)).build();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //Notify the change to the root Uri (uri notification based on descendants)
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case FOLLOW:{
                rowsDeleted = db.delete(
                        FollowEntry.TABLE_NAME, selection, selectionArgs);
                break;}
            case SECURITY:{
                rowsDeleted = db.delete(
                        SecurityEntry.TABLE_NAME, selection, selectionArgs);
                break;}
            case REQUEST:{
                rowsDeleted = db.delete(
                        FollowContract.RequestEntry.TABLE_NAME, selection, selectionArgs);
                break;

            }
            //For now I will not add support for paths with ID.
            default:
                throw new UnsupportedOperationException("Unknown uri for delete: " + uri);
        }
        // a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case FOLLOW:
            {
                rowsUpdated = db.update(FollowEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;}
            case SECURITY:
            {
                rowsUpdated = db.update(SecurityEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;}

            case REQUEST:
            {
                rowsUpdated = db.update(RequestEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;}

            //Again, for now I will not support id appended paths.
            default:
                throw new UnsupportedOperationException("Unknown uri for update: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
