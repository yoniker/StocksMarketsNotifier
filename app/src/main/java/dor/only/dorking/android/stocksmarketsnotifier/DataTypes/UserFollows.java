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

    private String facebookID;
    private String googleID;
    private String emailPassword;
    private String gcmRegisterToken;
    private String firstName;
    private String lastName;
    private String picURI;
    private String lastContactDate;

    //Some arbitary timestamp which will represent "unknown date"
    public static final String UNKNOWN_DATE="1983-01-01 02:00:00.0";



    public UserFollows(){
        setFacebookID("");
        setEmailPassword("");
        setGcmRegisterToken("");
        setFirstName("");
        setLastName("");
        setPicURI("");
        setLastContactDate(UNKNOWN_DATE);
        setEmailPassword("");
        setGoogleID("");


    }











    public String getFacebookID() {
        return facebookID;
    }



    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }



    public String getGoogleID() {
        return googleID;
    }



    public void setGoogleID(String googleID) {
        this.googleID = googleID;
    }



    public String getEmailPassword() {
        return emailPassword;
    }



    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }



    public String getGcmRegisterToken() {
        return gcmRegisterToken;
    }



    public void setGcmRegisterToken(String gcmRegisterToken) {
        this.gcmRegisterToken = gcmRegisterToken;
    }



    public String getFirstName() {
        return firstName;
    }



    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }



    public String getLastName() {
        return lastName;
    }



    public void setLastName(String lastName) {
        this.lastName = lastName;
    }



    public String getPicURI() {
        return picURI;
    }



    public void setPicURI(String picURI) {
        this.picURI = picURI;
    }



    public String getLastContactDate() {
        return lastContactDate;
    }



    public void setLastContactDate(String lastContactDate) {
        this.lastContactDate = lastContactDate;
    }







}
