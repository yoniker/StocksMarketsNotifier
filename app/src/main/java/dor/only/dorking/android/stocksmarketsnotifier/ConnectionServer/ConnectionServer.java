package dor.only.dorking.android.stocksmarketsnotifier.ConnectionServer;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

import dor.only.dorking.android.stocksmarketsnotifier.Contants.Constants;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Follow;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.FollowAndStatus;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.UserFollows;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowProvider;

/**
 * Created by Yoni on 5/23/2016.
 * This class represents the connection to the server, and is in charge of sending requests off the main thread to the server
 */
public class ConnectionServer {
    private static final String SERVER_NAME = "46.121.92.82:8080";
    private static final String USERS = "users";
    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String PUT = "PUT";
    private static final String LOG_TAG = "CONNECTIONSERVER";
    private final static String LOCAL_SERVER_BASE_URL = "http://" + SERVER_NAME + "/simpleapp";
    //When sending users for example to Heroku the address should be https://limitless-forest-61362.herokuapp.com/users
    private final static String HEROKU_SERVER_BASE_URL= "https://limitless-forest-61362.herokuapp.com";

    //Some constants which should exist both here and the server (that compatibility is what makes our app and the server 'synchronized' with each other
    //This is how the server calls the array of links it is giving us
    private static final String SERVER_LINKS = "links";
    private static final String SERVER_REL = "rel";
    private static final String SERVER_SELF = "self";
    private static final String SERVER_URL = "url";
    //The path which we need to append to the user's URI to get to the follows on the server
    private static final String SERVER_FOLLOWS_PATH="follows";

    //Some constants for debugging purposes
    private static final int CONNECTION_MODE_LOCAL=0;
    private static final int CONNECTION_MODE_HEROKU=1;

    private static final int CONNECTION_MODE_CHOSEN=CONNECTION_MODE_HEROKU;

    private Context mContext;

    public ConnectionServer(Context mContext) {
        this.mContext = mContext;

    }


    public void sendToServer(UserFollows theUser) {


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
                Uri builtUri;
                if(CONNECTION_MODE_CHOSEN==CONNECTION_MODE_LOCAL) {
                    builtUri= Uri.parse(LOCAL_SERVER_BASE_URL).buildUpon().appendPath(USERS).build();
                }
                else{
                    builtUri= Uri.parse(HEROKU_SERVER_BASE_URL).buildUpon().appendPath(USERS).build(); }

                HttpsURLConnection urlSConnection=null;
                HttpURLConnection urlConnection = null;
                BufferedWriter out = null;
                InputStream in = null;

                try {

                    URL url = new URL(builtUri.toString());

                    //  open the connection

                    if(CONNECTION_MODE_CHOSEN==CONNECTION_MODE_LOCAL){

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    //TODO setFixedLengthStreamingMode(int)
                //    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod(POST);
                    out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));}
                    else if(CONNECTION_MODE_CHOSEN==CONNECTION_MODE_HEROKU){
                        urlSConnection = (HttpsURLConnection) url.openConnection();
                        urlSConnection.setRequestProperty("Content-Type", "application/json");
                        //TODO setFixedLengthStreamingMode(int)
                        //    urlConnection.setDoOutput(true);
                        urlSConnection.setRequestMethod(POST);
                        out = new BufferedWriter(new OutputStreamWriter(urlSConnection.getOutputStream()));

                    }
                    out.write(json);
                    out.flush();
                    //TODO maybe in the future,if this isn't 200, try to resolve the issue or reschedule...
                    // int status=urlConnection.getResponseCode();
                   if(CONNECTION_MODE_CHOSEN==CONNECTION_MODE_LOCAL){
                    in = urlConnection.getInputStream();} else {in=urlSConnection.getInputStream();}
                    String theResponse = Constants.convertStreamToString(in);
                    //Here is an example response from the server:
                    //{"links":[{"rel":"self","url":"http://46.121.92.236/securitiesFollowServer/users/1"}],"theMessage":""}
                    JSONObject serverResponse = new JSONObject(theResponse);
                    JSONArray allLinks = serverResponse.getJSONArray(SERVER_LINKS);
                    for (int i = 0; i < allLinks.length(); ++i) {
                        JSONObject link = (JSONObject) allLinks.get(i);
                        //Let's check if it is the 'self' link, which should give us the new user which was created
                        //If so,then we should save it into sharedpreferences
                        if (link.has(SERVER_REL) && link.has(SERVER_URL)) {
                            if (link.getString(SERVER_REL).equals(SERVER_SELF)) {
                                Constants.writeToSharedPref(mContext, Constants.SP_USER_LINK, link.getString(SERVER_URL));

                            }

                        }


                    }

                    //urlConnection.connect();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    String theerror=e.toString();
                    theerror+=" ";
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    try{if(in!=null){in.close();}
                    if(out!=null){out.close();}} catch(IOException e){}




                    return null;


                }


            }


        };
        task.execute(theUser);
    }


    //Helper class which will implement a task sending a follow to the server
    //Needed because we need context in that class (so we can get the right URI to post for).

    private static class postFollowTask extends AsyncTask<FollowAndStatus, Void, FollowAndStatus>{
    private Context mContext;
        private boolean mFollowAlreadyExists;

        //a class which deals specifically with serializing timestamps

        private class DateTimeSerializer implements JsonSerializer<Timestamp> {

            @Override
            public JsonElement serialize(Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
                TimeZone tz = TimeZone.getTimeZone("UTC");
                //For info about SimpleDate check out this: https://developer.android.com/reference/java/text/SimpleDateFormat.html
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
                df.setTimeZone(tz);
                String nowAsISO = df.format(src);

                return new JsonPrimitive(nowAsISO);
            }


        }
        public postFollowTask(Context mContext,boolean followAlreadyExists){
        this.mContext=mContext;
        this.mFollowAlreadyExists=followAlreadyExists;
        }

        @Override
        protected FollowAndStatus doInBackground(FollowAndStatus... params) {

            HttpURLConnection urlConnection=null;
            BufferedWriter out=null;
            InputStream in=null;
            FollowAndStatus theFollowandStatus=params[0];
            Follow theFollow=theFollowandStatus.getFollow();
            String userURI=Constants.userURI(mContext);
            String receivedURI=theFollowandStatus.getFollowURIToServer();
            //If follow already exists and we have a URI then PUT the follow to that URI.
            //If not, then just POST to the 'usual' URI.
            boolean putFollow=mFollowAlreadyExists && receivedURI!=null && !receivedURI.equals("");

            try{

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Timestamp.class,new DateTimeSerializer());

                Gson gson = gsonBuilder.create();
                String json = gson.toJson(theFollow);
                Uri theUri=Uri.parse(userURI).buildUpon().appendPath(SERVER_FOLLOWS_PATH).build();
                URL theURL= new URL(theUri.toString());
                if(putFollow){theURL=new URL(receivedURI);}
                urlConnection = (HttpURLConnection) theURL.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                //TODO setFixedLengthStreamingMode(int)
                //urlConnection.setDoOutput(true);
                if(putFollow) {urlConnection.setRequestMethod(PUT);} else {urlConnection.setRequestMethod(POST);}
                out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                out.write(json);
                out.flush();
                //TODO maybe in the future,if this isn't 200, try to resolve the issue or reschedule...
                // int status=urlConnection.getResponseCode();
                //TODO maybe in the future,if this isn't 200, try to resolve the issue or reschedule...
                // int status=urlConnection.getResponseCode();
                in = urlConnection.getInputStream();
                String theResponse = Constants.convertStreamToString(in);
                //Here is an example response from the server:
                //{"links":[{"rel":"self","url":"http://46.121.92.236/securitiesFollowServer/users/1"}],"theMessage":""}
                JSONObject serverResponse = new JSONObject(theResponse);
                JSONArray allLinks = serverResponse.getJSONArray(SERVER_LINKS);
                FollowAndStatus followSent=new FollowAndStatus();
                followSent.setFollow(theFollow);
                followSent.setStatus(FollowAndStatus.STATUS_SENT_SUCCESSFULLY);
                for (int i = 0; i < allLinks.length(); ++i) {
                    JSONObject link = (JSONObject) allLinks.get(i);
                    //Let's check if it is the 'self' link, which should give us the URI for the follow
                    if (link.has(SERVER_REL) && link.has(SERVER_URL)) {
                        if (link.getString(SERVER_REL).equals(SERVER_SELF)) {
                            followSent.setFollowURIToServer(link.getString(SERVER_URL));

                        }

                    }
                }

                return followSent;







                }
            catch(MalformedURLException e){
                String h=e.toString();


            }

            catch(IOException e){
                String h=e.toString();

            }
            catch(JSONException e){

                String h=e.toString();

            }


            return null;
        }

        @Override
        protected void onPostExecute(FollowAndStatus followSent) {
            super.onPostExecute(followSent);

            if(followSent!=null){
            FollowProvider thedb=new FollowProvider();

            thedb.addToFollowsDB(followSent);}

        }
    }


    public void sendToServer(FollowAndStatus theFollow,boolean followAlreadyExists) {
        if(theFollow==null){return;}
        postFollowTask theTask=new postFollowTask(mContext,followAlreadyExists);
        theTask.execute(theFollow);
        return;





        }



}






