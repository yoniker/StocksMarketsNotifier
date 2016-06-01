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

    private Security theSecurity;
    private String followType;
    private double[] followParams=new double[NUMBER_OF_PARAMETERS];
    //From what point did we start following it?
    private Timestamp start;
    //What is the expiry date?
    private Timestamp expiry;

    public static final String FOLLOW_TYPE_BETWEEN="between";

    public Security getTheSecurity() {
        return theSecurity;
    }

    public void setTheSecurity(Security theSecurity) {
        this.theSecurity = theSecurity;
    }

    public String getFollowType() {
        return followType;
    }

    public void setFollowType(String followType) {
        this.followType = followType;
    }

    public double[] getFollowParams() {
        return followParams;
    }

    public void setFollowParams(double[] followParams) {
        this.followParams = followParams;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getExpiry() {
        return expiry;
    }

    public void setExpiry(Timestamp expiry) {
        this.expiry = expiry;
    }










}
