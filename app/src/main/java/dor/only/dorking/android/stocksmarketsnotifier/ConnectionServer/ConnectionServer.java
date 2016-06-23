package dor.only.dorking.android.stocksmarketsnotifier.ConnectionServer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
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
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowContract;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowProvider;
import dor.only.dorking.android.stocksmarketsnotifier.Database.UtilityForDatabase;

/**
 * Created by Yoni on 5/23/2016.
 * This class represents the connection to the server, and is in charge of sending requests off the main thread to the server
 */
public class ConnectionServer {
    private static final String SERVER_NAME = "5.102.220.176:8080";
    private static final String USERS = "users";
    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String PUT = "PUT";
    private static final String DELETE="DELETE";
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

     private static boolean  isSuccessful(int status){
          final int MIN_SUCCESS_STATUS_CODE=200;
          final int MAX_SUCCESS_STATUS_CODE=299;
         return (status>=MIN_SUCCESS_STATUS_CODE && status<=MAX_SUCCESS_STATUS_CODE);

    }

    private void handleRequestError( String content,String url, String httpMethod,String response, int status){
        //For now,handling the error means just putting it in the database
        //If it already exists,let's ++numberoftries otherwise let's persist it with numberoftries=1..
        Cursor requestSearchResult=null;
        try{

         requestSearchResult=mContext.getContentResolver().query(FollowContract.RequestEntry.CONTENT_URI,
                new String[]{FollowContract.RequestEntry.COLUMN_TRIES},
                FollowContract.sRequestDetails,
                new String[]{content,url,httpMethod},
                null);

        if(requestSearchResult.moveToFirst()){
            int tries=requestSearchResult.getInt(0);
            RequestToServer theRequest=new RequestToServer(content,url,httpMethod,response,status,++tries);
            ContentValues values=FollowProvider.requestContentValues(theRequest);
            mContext.getContentResolver().update(FollowContract.RequestEntry.CONTENT_URI,
                    values,
                    FollowContract.sRequestDetails,
                    new String[]{content,url,httpMethod});

        }

        else{
        RequestToServer theRequest=new RequestToServer(content,url,httpMethod,response,status,1);
        ContentValues values=FollowProvider.requestContentValues(theRequest);
        mContext.getContentResolver().insert(FollowContract.RequestEntry.CONTENT_URI,values);}
        return;}
        finally {
            if(requestSearchResult!=null){requestSearchResult.close();}
        }



    }




    public ConnectionServer(Context mContext) {
        this.mContext = mContext;

    }

    @WorkerThread
    public ServerResponse handleRequest(RequestToServer theRequest){
        final String HTTPS="HTTPS";
        if(theRequest==null){return null;}
        String method=theRequest.getHttpMethod();
        String content=theRequest.getContent();
        Uri theUri=Uri.parse(theRequest.getUrl());
        String scheme=theUri.getScheme(); //Either http or https
        boolean schemeIsHttps=scheme.toUpperCase().equals(HTTPS);
        HttpsURLConnection urlSConnection=null;
        HttpURLConnection urlConnection = null;
        BufferedWriter out = null;
        InputStream in = null;
        int status=0;
        String theResponse="";

        try {

            URL url = new URL(theUri.toString());

            //  open the connection

            if (!schemeIsHttps) {

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept","application/json");

                //TODO setFixedLengthStreamingMode(int)
                if (method != null && method != "") {
                    urlConnection.setRequestMethod(method);
                }
                out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            } else {
                urlSConnection = (HttpsURLConnection) url.openConnection();
                urlSConnection.setRequestProperty("content-type", "application/json");
                urlSConnection.setRequestProperty("accept","application/json");
                urlSConnection.setDoOutput(true);
                if (method != null && method != "") {
                    urlSConnection.setRequestMethod(method);
                }
                out = new BufferedWriter(new OutputStreamWriter(urlSConnection.getOutputStream()));

            }

            if(content!=null && !content.equals(""))
            {out.write(content);
            out.flush();
            }

            if (!schemeIsHttps) {
                int serverStatusReturned = urlConnection.getResponseCode();
                if(!isSuccessful(serverStatusReturned)){
                    InputStream error = urlConnection.getErrorStream();
                    BufferedReader br = new BufferedReader( new InputStreamReader( error ) );
                    StringBuffer text = new StringBuffer();
                    String line;

                    while ( (line = br.readLine())!= null)
                    text.append( line );

                    line="f";


                }
                in = urlConnection.getInputStream();
                status = urlConnection.getResponseCode();

            } else {
                in = urlSConnection.getInputStream();
                status = urlSConnection.getResponseCode();

            }
            theResponse = Constants.convertStreamToString(in);

        } catch(Exception e){

            e.printStackTrace();

            String errorMessage=e.toString();


        }

        finally{
            if(!isSuccessful(status)){
                handleRequestError(content,theRequest.getUrl(),method,theResponse,status);
            } else {
                deleteRequestFromDBIfExists(theRequest);

            }
            try{
                if(in!=null){in.close();}
                if(out!=null){out.close();}
                if(urlConnection!=null){urlConnection.disconnect();}
                if(urlSConnection!=null){urlSConnection.disconnect();}



            }

            catch(IOException e){}

        }







        ServerResponse serverResponse=new ServerResponse();
        serverResponse.setStatus(status);
        serverResponse.setBody(theResponse);



        return serverResponse;



    }

    private void deleteRequestFromDBIfExists(RequestToServer theRequest){

       mContext.getContentResolver().delete(FollowContract.RequestEntry.CONTENT_URI,
               FollowContract.sRequestDetails,
               new String[]{theRequest.getContent(),theRequest.getUrl(),theRequest.getHttpMethod()});



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
                String requestContents = gson.toJson(theUser);
                Uri builtUri;
                if(CONNECTION_MODE_CHOSEN==CONNECTION_MODE_LOCAL) {
                    builtUri= Uri.parse(LOCAL_SERVER_BASE_URL).buildUpon().appendPath(USERS).build();
                }
                else{
                    builtUri= Uri.parse(HEROKU_SERVER_BASE_URL).buildUpon().appendPath(USERS).build(); }
                RequestToServer theRequest=new RequestToServer(requestContents,builtUri.toString(),RequestToServer.POST,"",0,0);
               ServerResponse serverResponse= handleRequest(theRequest);




                try{
                JSONObject serverResponseInJson = new JSONObject(serverResponse.getBody());
                JSONArray allLinks = serverResponseInJson.getJSONArray(SERVER_LINKS);
                for (int i = 0; i < allLinks.length(); ++i) {
                    JSONObject link = (JSONObject) allLinks.get(i);
                    //Let's check if it is the 'self' link, which should give us the new user which was created
                    //If so,then we should save it into sharedpreferences
                    if (link.has(SERVER_REL) && link.has(SERVER_URL)) {
                        if (link.getString(SERVER_REL).equals(SERVER_SELF)) {
                            Constants.writeToSharedPref(mContext, Constants.SP_USER_LINK, link.getString(SERVER_URL));

                        }

                    } }
                }

                catch(JSONException e){}


                    return null;


                }




        };
        task.execute(theUser);
    }

   /* @WorkerThread
    void writeFollowToDatabase(FollowAndStatus theFollowAndStatus){

        //First lets check if the follow is already in the Database..
        //For now we are assuming that each security has at most one  follow so we can find it based on that
        //Let's find the security ID first (based on its ticker and stock market).


        long securityId=Constants.getSecurityId(mContext,theFollowAndStatus.getFollow().getTheSecurity());
        if(securityId==Constants.SECURITY_NOT_FOUND){
            //If there is no such security that exists on the device, I am going to assume that the local device is more up-to-date than the server.
            Log.e(LOG_TAG,"Follow should already be in local database when sending it to server.");

            return;
        }



        final String selectOnlyActiveFollows="not "+FollowContract.FollowEntry.COLUMN_STATUS+"=\""+FollowAndStatus.STATUS_HISTORY+'"';
        Cursor followSearchResult=mContext.getContentResolver().query(FollowContract.FollowEntry.CONTENT_URI,
                new String[] {FollowContract.FollowEntry._ID},
                FollowContract.FollowEntry.COLUMN_SECURITY_ID+"=? and "+selectOnlyActiveFollows,
                new String[]{String.valueOf(securityId)},
                null);
        if(followSearchResult.moveToFirst()){

            long idInContentProvider=followSearchResult.getLong(0);
            if(!(idInContentProvider>0)){
                Log.e(LOG_TAG,"Found a follow with an illegal id");
                return;
            }
            ContentValues values=new ContentValues();
            values.put(FollowContract.FollowEntry.COLUMN_URI_TO_SERVER,theFollowAndStatus.getFollowURIToServer());
            values.put(FollowContract.FollowEntry.COLUMN_STATUS,FollowAndStatus.STATUS_SENT_SUCCESSFULLY);
            mContext.getContentResolver().update(FollowContract.FollowEntry.CONTENT_URI,
                    values,
                    FollowContract.FollowEntry._ID+"=?",
                    new String[]{String.valueOf(idInContentProvider)});



        } else{
            Log.e(LOG_TAG,"Follow should already be in local database when sending it to server.");
            return;
        }

    }*/


    //Helper class which will implement a task sending a follow to the server
    //Needed because we need context in that class (so we can get the right URI to post for).

    private  class postFollowTask extends AsyncTask<FollowAndStatus, Void, FollowAndStatus>{
    private Context mContext;
        private boolean mFollowAlreadyExists;

        //a class which deals specifically with serializing timestamps

        private class DateTimeSerializer implements JsonSerializer<Timestamp> {

            @Override
            public JsonElement serialize(Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
                TimeZone tz = TimeZone.getTimeZone("UTC");
                //For info about SimpleDate check out this: https://developer.android.com/reference/java/text/SimpleDateFormat.html
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
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
            String httpMethod= putFollow? RequestToServer.PUT:RequestToServer.POST;
            int status=0;
            String bodyOfMessage="";
            URL theURL=null;
            String theResponse="";
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Timestamp.class,new DateTimeSerializer());

            Gson gson = gsonBuilder.create();
            bodyOfMessage = gson.toJson(theFollow);
            Uri theUriForNewFollows=Uri.parse(userURI).buildUpon().appendPath(SERVER_FOLLOWS_PATH).build();
            String url=putFollow?receivedURI:theUriForNewFollows.toString();
            RequestToServer theRequest=new RequestToServer(bodyOfMessage,url,httpMethod,"",0,0);
            ServerResponse serverResponse=handleRequest(theRequest);
            FollowAndStatus followSent=null;
            try {
                JSONObject serverResponseInJson = new JSONObject(serverResponse.getBody());
                JSONArray allLinks = serverResponseInJson.getJSONArray(SERVER_LINKS);
                followSent = new FollowAndStatus();
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
            } catch (JSONException e){}

            //Update the follow in the database to have the server's Uri
            if(isSuccessful(serverResponse.getStatus())) {
                UtilityForDatabase.updateFollowStatusAndUriInDatabase(mContext,theFollowandStatus);
            }



            return null;
        }
    }


    public void sendToServer(FollowAndStatus theFollow,boolean followAlreadyExists) {
        if(theFollow==null){return;}
        postFollowTask theTask=new postFollowTask(mContext,followAlreadyExists);
        theTask.execute(theFollow);
        return;





        }

    public void deleteFromServer(FollowAndStatus theFollow){
        AsyncTask<FollowAndStatus,Void,Void> deleteTask=new AsyncTask<FollowAndStatus, Void, Void>() {
            @Override
            protected Void doInBackground(FollowAndStatus... params) {
                FollowAndStatus theFollow=params[0];
                if(theFollow==null){return null;}
                String followUri=theFollow.getFollowURIToServer();
                RequestToServer theRequest=new RequestToServer("",followUri,RequestToServer.DELETE,"",0,0);
                handleRequest(theRequest);
                return null;
            }
        };


        deleteTask.execute(theFollow);
    }



}






