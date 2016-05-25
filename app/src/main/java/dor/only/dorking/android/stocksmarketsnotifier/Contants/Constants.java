package dor.only.dorking.android.stocksmarketsnotifier.Contants;

import android.content.Context;
import android.content.SharedPreferences;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.UserFollows;

/**
 * Created by Yoni on 5/23/2016.
 */
public class Constants {

    //File name for shared preferences file
    public static final String SHARED_PREFS="DorisKing";
    //key in sp for fb ID
    public static final String SP_FACEBOOK_ID="MyfacebookID";
    //key in sp for Google ID
    public static final String SP_GOOGLE_ID="MyGoogleID";
    //key in sp for email+password
    public static final String SP_MY_EMAIL_PASSWORD="myEmailPassword";
    //key in sp for GCM token
    public static final String SP_GCM_REG_TOKEN="myGCMRegistertoken";
    //key in sp for user's first name
    public static final String SP_FIRST_NAME="my first name";
    //key in sp for user's last name
    public static final String SP_LAST_NAME="my last name";
    //key in sp for user's pic URI
    public static final String SP_PIC_URI="my pic uri";

    //key in sp for last contact (the last time the client tried to contact the server
    public static final String SP_LAST_CONTACT="last contact";


    /**
     *
     * @param context the Context so we can access shared preferences
     * @return boolean - true if user already has identity eg Email | FacebookID | GoogleID
     */

    public static boolean hasId(Context context){
        SharedPreferences sp=context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        //Do we have enough info to identify the guy? it's enough if he has Facebook ID,Email+password or google ID
        return  sp.contains(SP_FACEBOOK_ID) || sp.contains(SP_MY_EMAIL_PASSWORD) || sp.contains(SP_GOOGLE_ID);

    }

    public static boolean hasGCMToken(Context context){
        SharedPreferences sp=context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        //We have a GCM Token if it is in shared preferences, and not as an empty String.
        return !sp.getString(SP_GCM_REG_TOKEN,"").equals("");


    }



    /**
     *
     * @param context - so we can get shared preferences
     * @return boolean- whether or not the user is ready to be sent to server
     *  (FBID | GOOGLEID | EMAIL+PASSWORD) & GCMTOKEN
     */
    //a helper method which checks if all the user info is available so we can sent it to the server
    public static boolean userReady(Context context){
        SharedPreferences sp=context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);

        //At this point, I also want to make sure that we have a GCM Token,otherwise server can't push notifications
        boolean hasGCMToken = sp.contains(SP_GCM_REG_TOKEN);
        return hasId(context) && hasGCMToken;

    }


    public static UserFollows getUserFromPreferences(Context context){
        if(!userReady(context)) {return null;}
        SharedPreferences sp=context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        UserFollows theUser=new UserFollows();
        if(sp.contains(SP_GCM_REG_TOKEN)) {theUser.setGcmRegisterToken(sp.getString(SP_GCM_REG_TOKEN,""));}
        if(sp.contains(SP_MY_EMAIL_PASSWORD)){theUser.setEmailPassword(sp.getString(SP_MY_EMAIL_PASSWORD,""));}
        if(sp.contains(SP_GOOGLE_ID)){theUser.setGoogleID(sp.getString(SP_GOOGLE_ID,""));}
        if(sp.contains(SP_FACEBOOK_ID)){theUser.setFacebookID(sp.getString(SP_FACEBOOK_ID,""));}
        if(sp.contains(SP_FIRST_NAME)) {theUser.setFirstName(sp.getString(SP_FIRST_NAME,""));}
        if(sp.contains(SP_LAST_NAME)){theUser.setLastName(sp.getString(SP_LAST_NAME,""));}
        if(sp.contains(SP_PIC_URI)){theUser.setPicURI(sp.getString(SP_PIC_URI,""));}
        if(sp.contains(SP_LAST_CONTACT)){theUser.setLastContactDate(sp.getString(SP_LAST_CONTACT,""));}

            return theUser;



    }

    public static void writeToSharedPref(Context context,String key,String value){
        SharedPreferences sp=context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor ed=sp.edit();
        ed.putString(key,value);
        ed.apply(); //Notice that unlike commit, apply is off the main thread


    }

}
