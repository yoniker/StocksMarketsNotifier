package dor.only.dorking.android.stocksmarketsnotifier.Database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;

/**
 * Created by Yoni on 6/1/2016.
 */
public class FollowContract {

    public static final String CONTENT_AUTHORITY = "dor.only.dorking.android.stocksmarketsnotifier";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://dor.only.dorking.android.stocksmarketsnotifier/security/ is a valid path for
    // looking at securities data. content://dor.only.dorking.android.stocksmarketsnotifier/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    public static final String PATH_SECURITY = "security";
    public static final String PATH_FOLLOW = "follow";

    //Some queries keys
    public static final String QUERY_KEY_SECURITY_TICKER="ticker";
    public static final String QUERY_KEY_SECURITY_STOCKS_MARKET="stocks_market";

    //ticker=? and stock_market_name=?
    public static final String sSecurityDetails=  SecurityEntry.COLUMN_TICKER+"=? and "+SecurityEntry.COLUMN_STOCKMARKETNAME+"=?";

    //The path follow/security which represents the data of a follow with the data on the security as well.
    public static final Uri sFollowWithSecurity=FollowContract.BASE_CONTENT_URI.buildUpon().appendPath(FollowContract.PATH_FOLLOW).appendPath(FollowContract.PATH_SECURITY).build();

    public static Uri buildFollowWithSecurity(Security theSecurity) {
        return FollowContract.BASE_CONTENT_URI.buildUpon().appendPath(FollowContract.PATH_FOLLOW).appendPath(FollowContract.PATH_SECURITY)
                .appendQueryParameter(QUERY_KEY_SECURITY_STOCKS_MARKET,theSecurity.getStocksMarketName())
                .appendQueryParameter(QUERY_KEY_SECURITY_TICKER,theSecurity.getTicker()).build();
    }


    public static final class SecurityEntry implements  BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SECURITY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SECURITY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SECURITY;

        public static final String TABLE_NAME="securities_table";
        public static final String COLUMN_SECURITY_NAME="sname";
        public static final String COLUMN_TICKER="ticker";
        public static final String COLUMN_COUNTRY="country";
        public static final String COLUMN_STOCKMARKETNAME="stock_market_name";
        public static final String COLUMN_SECURITY_TYPE="stype";
        public static final String COLUMN_URI_INFO_LINK="uri_info_link";



    }
    public static final class FollowEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FOLLOW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOLLOW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOLLOW;

        public static final String TABLE_NAME="follows_table";
        public static final String COLUMN_DATE_STARTED="date_started";
        public static final String COLUMN_DATE_EXPIRY="expire";
        public static final String COLUMN_PRICE_STARTED="price_started";
        //a foreign key to the securities table
        public static final String COLUMN_SECURITY_ID="security_id";
        public static final String COLUMN_FOLLOW_TYPE="follow_type";
        public static final String COLUMN_PARAM1="param1";
        public static final String COLUMN_PARAM2="param2";
        public static final String COLUMN_PARAM3="param3";
        public static final String COLUMN_PARAM4="param4";
        //What is the status of the follow?
        public static final String COLUMN_STATUS="status";
        public static final String COLUMN_URI_TO_SERVER="follow_uri_to_server";




    }
}