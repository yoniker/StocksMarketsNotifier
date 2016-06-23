package dor.only.dorking.android.stocksmarketsnotifier;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.test.ApplicationTestCase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Follow;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowContract;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public static class gsonUTCdateAdapter implements JsonSerializer<Date>,JsonDeserializer<Date> {

        private final DateFormat dateFormat;

        public gsonUTCdateAdapter() {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");      //This is the format I need
            //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); //This is the key line which converts the date to UTC which cannot be accessed with the default serializer


        }

        @Override
        public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(dateFormat.format(date));
        }

        @Override
        public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            try {
                return dateFormat.parse(jsonElement.getAsString());
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
        }

    }


    private void sendNotification(String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent =
                PendingIntent.getActivity(mContext, 0, new Intent(mContext, ChooseStockActivity.class), 0);

        // Notifications using both a large and a small icon (which yours should!) need the large
        // icon as a bitmap. So we need to create that here from the resource ID, and pass the
        // object along in our notification builder. Generally, you want to use the app icon as the
        // small icon, so that users understand what app is triggering this notification.
        Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.common_google_signin_btn_text_light_focused);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_plusone_small_off_client)
                        .setLargeIcon(largeIcon)
                        //toDo change notification or behavior when getting a message
                        .setContentTitle("Notification title!")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void testSendNotification(){

        sendNotification("Dor is everything!");


    }


    public void testGettingMessage(){
    String theMessage="{\"theSecurity\":{\"name\":\"Microsoft Corporation\",\"ticker\":\"MSFT\",\"moreInfoUri\":\"http://www.nasdaq.com/symbol/msft\",\"country\":\"USA\",\"stocksMarketName\":\"NASDAQ\",\"securityType\":\"stock\"},\"followType\":\"between\",\"followParams\":[51.183,51.18399999999999,0.0,0.0],\"start\":\"2016-06-22T17:11:30.871\",\"expiry\":\"2016-06-22T17:11:30.871\"}";
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new gsonUTCdateAdapter()).create();
        Follow theFollow=gson.fromJson(theMessage,Follow.class);
        assertEquals(theFollow.getFollowType(),"between");


    }

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