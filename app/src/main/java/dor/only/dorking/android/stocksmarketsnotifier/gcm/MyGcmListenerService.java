package dor.only.dorking.android.stocksmarketsnotifier.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dor.only.dorking.android.stocksmarketsnotifier.ChooseStockActivity;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Follow;
import dor.only.dorking.android.stocksmarketsnotifier.R;
import dor.only.dorking.android.stocksmarketsnotifier.SecurityPresent;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    private static final String EXTRA_DATA = "data";
    private static final String EXTRA_MESSAGE="message";

    public static final int NOTIFICATION_ID = 1;


    private static class gsonUTCdateAdapter implements JsonSerializer<Date>,JsonDeserializer<Date> {

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

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {




        // Time to unparcel the bundle!
        if (!data.isEmpty()) {
            // gcm_default sender ID came from the API console
            String senderId = getString(R.string.gcm_defaultSenderId);
            // Not a bad idea to check that the message is coming from your server.
            if ((senderId).equals(from)) {
                // Process message and then post a notification of the received message.
                try {
                    //JSONObject jsonObject = new JSONObject(data.getString(EXTRA_DATA));
                    String theMessage = data.getString(EXTRA_MESSAGE);
                    //This SHOULD be a jsonobject of follow, lets check it out

                    try{

                        handleMessageFromServer(theMessage);



                    }
                    catch(Exception e)
                    {

                        String alert =theMessage;
                        }
                }

                catch (Exception e) {
                    // JSON parsing failed, so we just let this message go, since GCM is not one
                    // of our critical features.
                }
            }
            Log.i(TAG, "Received: " + data.toString());
        }
    }

    /**
     *  Put the message into a notification and post it.
     *
     * @param theMessage The  message we got from the server
     */
    private void handleMessageFromServer(String theMessage) {
        //Some contants that will come from the server in the message itself:
        final String DIRECTION_JSON_KEY = "DIRECTION";
        final String FINAL_PRICE_JSON_KEY = "FINAL_PRICE";
        final String THE_FOLLOW_JSON_KEY = "THEFOLLOW";
        //and those are the possible values for the 'direction' key
        final String UP_VALUE = "UP";
        final String DOWN_VALUE = "DOWN";

        if(!isJson(theMessage)){
            handleSimpleMessageFromServer(theMessage);

        }


        //Let's start by parsing the message
        try {
            JSONObject theMessageInJson = new JSONObject(theMessage);
            String direction = theMessageInJson.getString(DIRECTION_JSON_KEY);
            double finalPrice = theMessageInJson.getDouble(FINAL_PRICE_JSON_KEY);
            String theFollowString = theMessageInJson.getString(THE_FOLLOW_JSON_KEY);
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new gsonUTCdateAdapter()).create();
            Follow theFollow = gson.fromJson(theFollowString, Follow.class);
            double [] followParams=theFollow.getFollowParams();
            double lowValue=followParams[0];
            double highValue=followParams[1];


            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Intent launchSecurityShow=new Intent(this, SecurityPresent.class);
            launchSecurityShow.putExtra(SecurityPresent.THE_SECURITY,theFollow.getTheSecurity());
            launchSecurityShow.putExtra(SecurityPresent.THE_FOLLOW_AND_STATUS,theFollow);

            PendingIntent contentIntent =
                    PendingIntent.getActivity(this, 0, launchSecurityShow, 0);

            int drawableArrow= direction.equals(UP_VALUE)?R.drawable.uparrow:R.drawable.downarrow;
            int iconArrow=direction.equals(UP_VALUE)?R.drawable.ic_stat_uparrow:R.drawable.ic_stat_downarrow;
            int notificationColor=direction.equals(UP_VALUE)?Color.GREEN:Color.RED;


            String title=theFollow.getTheSecurity().getName()+" IS "+direction+"!";
            String content=title+" at "+finalPrice+". You wanted me to notify you at ("+lowValue+','+highValue+")";

            // Notifications using both a large and a small icon (which yours should!) need the large
            // icon as a bitmap. So we need to create that here from the resource ID, and pass the
            // object along in our notification builder. Generally, you want to use the app icon as the
            // small icon, so that users understand what app is triggering this notification.
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), drawableArrow);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(iconArrow)
                            .setLargeIcon(largeIcon)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                            .setContentText(content)
                            .setLights(notificationColor, 1000, 1000)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(0, mBuilder.build());
        } catch(JSONException e){}
    }

    private boolean isJson(String theString){
        try {
            new JSONObject(theString);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(theString);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;

    }

    private void handleSimpleMessageFromServer(String theMessage){
        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, new Intent(this,ChooseStockActivity.class), 0);



        String title="You got a new message!";
        String content=theMessage;

        // Notifications using both a large and a small icon (which yours should!) need the large
        // icon as a bitmap. So we need to create that here from the resource ID, and pass the
        // object along in our notification builder. Generally, you want to use the app icon as the
        // small icon, so that users understand what app is triggering this notification.
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.messages);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_message_chat_text_bubble_phone)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                        .setContentText(content)
                        .setLights(Color.WHITE, 1000, 1000)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }





}



