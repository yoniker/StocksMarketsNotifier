package dor.only.dorking.android.stocksmarketsnotifier.ConnectionServer;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.UserFollows;

/**
 * Created by Yoni on 5/23/2016.
 * This class represents the connection to the server, and is in charge of sending requests off the main thread to the server
 */
public class ConnectionServer {
    private static final String SERVER_NAME="46.121.72.221:8080";
    private static final String USERS="users";
    private static final String POST="POST";
    private static final String GET="GET";
    private static final String PUT="PUT";
    private static final String LOG_TAG="CONNECTIONSERVER";
     public static void  sendToServer(UserFollows theUser){

         AsyncTask<UserFollows,Void,Void> task=new AsyncTask<UserFollows, Void, Void>() {
             @Override
             protected Void doInBackground(UserFollows... params) {

                 UserFollows theUser = params[0];


                 if (theUser == null) {
                     return null;
                 }
                 //Alright, so what we want to do is to send the user in a json format to the server so:
                 Gson gson = new Gson();
                 String json = gson.toJson(theUser);
                 final String SERVER_BASE_URL = "http://" + SERVER_NAME + "/securitiesFollowServer";
                 Uri builtUri = Uri.parse(SERVER_BASE_URL).buildUpon()
                         .appendPath(USERS).build();

                 // These three need to be declared outside the try/catch
                 // so that they can be closed in the finally block. Try with resources requires API level 19.
                 HttpURLConnection urlConnection = null;
                 BufferedReader reader = null;
                 OutputStream writer = null;
                 try {

                     URL url = new URL(builtUri.toString());

                     // Create the request to OpenWeatherMap, and open the connection
                     urlConnection = (HttpURLConnection) url.openConnection();
                     urlConnection.setRequestMethod(POST);
                     urlConnection.setRequestProperty("Content-Type", "application/json");
                     writer = urlConnection.getOutputStream();
                     byte[] outputInBytes = json.getBytes("UTF-8");
                     writer.write(outputInBytes);
                     urlConnection.connect();
                 } catch (IOException e) {
                     Log.e(LOG_TAG, "Error ", e);
                     // If the code didn't successfully get the weather data, there's no point in attempting
                     // to parse it.
                 } finally {
                     if (urlConnection != null) {
                         urlConnection.disconnect();
                     }

                     if (reader != null || writer != null) {
                         try {
                             if (reader != null) reader.close();
                             if (writer != null) writer.close();
                         } catch (final IOException e) {
                             Log.e(LOG_TAG, "Error closing stream", e);
                         }
                     }


                 }
                 return null;
             }




         };

         task.execute(theUser);




     }


}


