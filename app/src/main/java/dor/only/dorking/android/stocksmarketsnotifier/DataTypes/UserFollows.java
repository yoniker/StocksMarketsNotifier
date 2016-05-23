package dor.only.dorking.android.stocksmarketsnotifier.DataTypes;

/**
 * @author Yoni
 * This class represents a user. a user should identify himself by at least one of the following three methods:
 * 1.Facebook ID (Facebook login provides a unique ID per user per application).
 * 2.GoogleID    (same idea)
 * 3.Email (+password in the future).
 *
 *
 * The main point for the time being is to identify the same user on different devices.
 * Information is not secured whatsoever for now
 *
 *
 * On top of that,when registering a user HAS to provide GCM register token
 *
 *
 *
 *
 */


public class UserFollows {
    //Some internal ID used within the databases
    private String mFacebookID;
    private String mGoogleID;
    private String mEmailPassword;
    private String mGCMRegisterToken;
    private String mFirstName;
    private String mLastName;
    private String mPicURI;
    //In the client context,this will mean the last time the client tried to contact the server
    private String mLastContactDate;

    //Some arbitary timestamp which will represent "unknown date"
    public static final String UNKNOWN_DATE="1983-01-01 02:00:00.0";



    public UserFollows(){
        setFacebookID("");
        setEmailPassword("");
        setGCMRegisterToken("");
        setFirstName("");
        setLastName("");
        setPicURI("");
        setLastContactDate(UNKNOWN_DATE);
        setEmailPassword("");
        setGoogleID("");


    }

    public String getFacebookID() {
        return mFacebookID;
    }
    public void setFacebookID(String mFacebookID) {
        this.mFacebookID = mFacebookID;
    }
    public String getGoogleID() {
        return mGoogleID;
    }
    public void setGoogleID(String mGoogleID) {
        this.mGoogleID = mGoogleID;
    }
    public String getEmailPassword() {
        return mEmailPassword;
    }
    public void setEmailPassword(String mEmailPassword) {
        this.mEmailPassword = mEmailPassword;

    }
    public String getGCMRegisterToken() {
        return mGCMRegisterToken;
    }
    public void setGCMRegisterToken(String mGCMRegisterToken) {
        this.mGCMRegisterToken = mGCMRegisterToken;
    }
    public String getFirstName() {
        return mFirstName;
    }
    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }
    public String getLastName() {
        return mLastName;
    }
    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }
    public String getPicURI() {
        return mPicURI;
    }
    public void setPicURI(String mPicURI) {
        this.mPicURI = mPicURI;
    }
    public String getLastContactDate() {
        return mLastContactDate;
    }
    public void setLastContactDate(String mLastContactDate) {
        this.mLastContactDate = mLastContactDate;
    }



}
