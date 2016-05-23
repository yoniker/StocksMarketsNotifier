package dor.only.dorking.android.stocksmarketsnotifier.DataTypes;

import java.sql.Timestamp;


/**
 *
 * @author Yoni
 * This class represents the entire follow eg:
 * 1.The security that we want to follow
 * 2.The other follow details, eg type of follow,and parameters associated with the follow
 */

public class Follow {


    //For now we will support up to 4 numerical parameters for the follow's desciption
    public static final int NUMBER_OF_PARAMETERS=4;

    private Security mTheSecurity;
    private String mFollowType;
    private double[] mFollowParams=new double[NUMBER_OF_PARAMETERS];
    //From what point did we start following it?
    private Timestamp mStart;
    //What is the expiry date?
    private Timestamp mExpiry;

    public static final String FOLLOW_TYPE_BETWEEN="between";



    public String getFollowType() {
        return mFollowType;
    }

    public void setFollowType(String followType) {
        this.mFollowType = followType;
    }

    public double[] getFollowParams() {
        return mFollowParams;
    }

    public void setFollowParams(double[] mFollowParams) {
        this.mFollowParams = mFollowParams;
    }

    public Timestamp getStart() {
        return mStart;
    }

    public void setStart(Timestamp mStart) {
        this.mStart = mStart;
    }

    public Timestamp getExpiry() {
        return mExpiry;
    }

    public void setExpiry(Timestamp mExpiry) {
        this.mExpiry = mExpiry;
    }

    public static int getNumberOfParameters() {
        return NUMBER_OF_PARAMETERS;
    }

    public static String getFollowTypeBetween() {
        return FOLLOW_TYPE_BETWEEN;
    }



    public Security getTheSecurity() {
        return mTheSecurity;
    }

    public void setTheSecurity(Security theSecurity) {
        this.mTheSecurity = theSecurity;
    }




}
