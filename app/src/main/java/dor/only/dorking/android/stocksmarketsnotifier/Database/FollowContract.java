package dor.only.dorking.android.stocksmarketsnotifier.Database;

import android.provider.BaseColumns;

/**
 * Created by Yoni on 6/1/2016.
 */
public class FollowContract {
    public static final class FollowEntry implements BaseColumns {

        public static final String TABLE_NAME="follows_table";
        public static final String COLUMN_DATE_STARTED="date_started";
        public static final String COLUMN_DATE_EXPIRY="expire";
        public static final String COLUMN_PRICE_STARTED="price_started";
        // For now "security id" will just be the ticker. In the future TODO implement another database with securities that we follow
        public static final String COLUMN_SECURITY_TICKER="security_ticker";
        public static final String COLUMN_FOLLOW_TYPE="follow_type";
        public static final String COLUMN_PARAM1="param1";
        public static final String COLUMN_PARAM2="param2";
        public static final String COLUMN_PARAM3="param3";
        public static final String COLUMN_PARAM4="param4";
        //What is the status of the follow?
        public static final String COLUMN_STATUS="status";




    }
}