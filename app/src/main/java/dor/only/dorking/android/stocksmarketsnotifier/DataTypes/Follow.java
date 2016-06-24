package dor.only.dorking.android.stocksmarketsnotifier.DataTypes;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;


/**
 *
 * @author Yoni
 * This class represents the entire follow eg:
 * 1.The security that we want to follow
 * 2.The other follow details, eg type of follow,and parameters associated with the follow
 */

public class Follow implements Parcelable {

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        getTheSecurity().writeToParcel(out,flags);
        out.writeString(getFollowType());
        out.writeLong(getStart().getTime());
        out.writeLong(getExpiry().getTime());
        out.writeDoubleArray(getFollowParams());
    }

    public static final Parcelable.Creator<Follow> CREATOR
            = new Parcelable.Creator<Follow>() {
        public Follow createFromParcel(Parcel in) {
            Follow theFollow= new Follow();
            Security theSecurity=//in.readParcelable(Security.class.getClassLoader()); TODO this throws an exception-why?
            Security.CREATOR.createFromParcel(in);
            theFollow.setTheSecurity(theSecurity);
            theFollow.setFollowType(in.readString());
            theFollow.setStart(new Timestamp(in.readLong()));
            theFollow.setExpiry(new Timestamp(in.readLong()));
            double[] theParams=new double[Follow.NUMBER_OF_PARAMETERS];
            in.readDoubleArray(theParams);
            theFollow.setFollowParams(theParams);



            return theFollow;
        }
        public Follow[] newArray(int size) {
            return new Follow[size];
        }


    };



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
