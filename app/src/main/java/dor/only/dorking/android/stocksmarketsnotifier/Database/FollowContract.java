package dor.only.dorking.android.stocksmarketsnotifier.Database;

import android.provider.BaseColumns;

/**
 * Created by Yoni on 6/1/2016.
 */
public class FollowContract {

    public static final class SecurityEntry implements  BaseColumns{

        public static final String TABLE_NAME="securities_table";
        public static final String COLUMN_SECURITY_NAME="sname";
        public static final String COLUMN_TICKER="ticker";
        public static final String COLUMN_COUNTRY="country";
        public static final String COLUMN_STOCKMARKETNAME="stock_market_name";
        public static final String COLUMN_SECURITY_TYPE="stype";
        public static final String COLUMN_URI_INFO_LINK="uri_info_link";



    }
    public static final class FollowEntry implements BaseColumns {

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




    }
}