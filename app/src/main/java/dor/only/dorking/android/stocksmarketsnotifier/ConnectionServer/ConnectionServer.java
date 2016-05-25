package dor.only.dorking.android.stocksmarketsnotifier.ConnectionServer;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.UserFollows;

/**
 * Created by Yoni on 5/23/2016.
 * This class represents the connection to the server, and is in charge of sending requests off the main thread to the server
 */
public class ConnectionServer {
    private static final String SERVER_NAME="46.121.92.236:80";
    private static final String USERS="users";
    private static final String POST="POST";
    private static final String GET="GET";
    private static final String PUT="PUT";
    private static final String LOG_TAG="CONNECTIONSERVER";

     public static void  sendToServer(UserFollows theUser) {


         if (theUser == null) {
             return;
         }
         //Alright, so what we want to do is to send the user in a json format to the server so:

         AsyncTask<UserFollows, Void, Void> task = new AsyncTask<UserFollows, Void, Void>() {
             @Override
             protected Void doInBackground(UserFollows... params) {

                 UserFollows theUser = params[0];
                 Gson gson = new Gson();
                 String json = gson.toJson(theUser);
                 final String SERVER_BASE_URL = "http://" + SERVER_NAME + "/securitiesFollowServer";
                 Uri builtUri = Uri.parse(SERVER_BASE_URL).buildUpon()
                         .appendPath(USERS).build();

                 HttpURLConnection urlConnection=null;
                 BufferedWriter out=null;

                 try {

                     URL url = new URL(builtUri.toString());

                     //  open the connection
                     urlConnection = (HttpURLConnection) url.openConnection();
                     urlConnection.setRequestProperty("Content-Type", "application/json");
                     //TODO setFixedLengthStreamingMode(int)
                     //urlConnection.setDoOutput(true);     //Should make post request-doesnt work
                     urlConnection.setRequestMethod(POST); //that doesn't work either! :(
                     out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                     out.write(json);
                     out.flush();
                     int status=urlConnection.getResponseCode();

                     //urlConnection.connect();
                 } catch (IOException e) {
                     Log.e(LOG_TAG, "Error ", e);
                 }  finally {
                     if (urlConnection != null) {
                         urlConnection.disconnect();
                     }







                 return null;


             }


         }


     };
         task.execute(theUser);}}





