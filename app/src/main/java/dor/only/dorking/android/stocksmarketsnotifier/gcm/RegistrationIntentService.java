package dor.only.dorking.android.stocksmarketsnotifier.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import dor.only.dorking.android.stocksmarketsnotifier.ConnectionServer.ConnectionServer;
import dor.only.dorking.android.stocksmarketsnotifier.Contants.Constants;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.UserFollows;
import dor.only.dorking.android.stocksmarketsnotifier.R;


public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                InstanceID instanceID = InstanceID.getInstance(this);

                // gcm_default sender ID comes from the API console
                String senderId = getString(R.string.gcm_defaultSenderId);
                if ( senderId.length() != 0 ) {
                    String token = instanceID.getToken(senderId,
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    sendRegistrationToServer(token);
                    //When we are here, we got an instance token from Google's cloud
                    //First let's save it into shared pref file
                    Constants.writeToSharedPref(getApplicationContext(),Constants.SP_GCM_REG_TOKEN,token);
                    //and now,if there is an ID, let's write the user to the server.

                    if(Constants.hasId(getApplicationContext())){
                        UserFollows theUser=Constants.getUserFromPreferences(getApplicationContext());
                        //everything is set already in user,except,maybe,GCM registeration token
                        theUser.setGCMRegisterToken(token);
                        ConnectionServer.sendToServer(theUser);


                    }
                }

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
               // sharedPreferences.edit().putBoolean(ChooseStockActivity.SENT_TOKEN_TO_SERVER, true).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);

            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
           // sharedPreferences.edit().putBoolean(ChooseStockActivity.SENT_TOKEN_TO_SERVER, false).apply();
            //Delete the Intance Token currently at shared preferences - it is not valid anymore anyways
            Constants.writeToSharedPref(getApplicationContext(),Constants.SP_GCM_REG_TOKEN,"");
        }
    }

    /**
     * Normally, you would want to persist the registration to third-party servers. Because we do
     * not have a server, and are faking it with a website, you'll want to log the token instead.
     * That way you can see the value in logcat, and note it for future use in the website.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        Log.i(TAG, "GCM Registration Token: " + token);
    }
}