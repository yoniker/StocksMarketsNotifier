package dor.only.dorking.android.stocksmarketsnotifier.DataTypes;

/**
 * Created by Yoni on 6/1/2016.
 */
public class FollowAndStatus {
    private Follow follow;
    private String Status;



    private String followURIToServer;
    private double priceStarted;



    //sent but we didn't get a response yet.
    public final static String STATUS_SENT="sent to server";
    //sent successfully means that we sent it to the server,it responded with a 200 code, and we did not get a notification yet for it.
    public final static String STATUS_SENT_SUCCESSFULLY="sent to server successfully";
    //We tried to send it and failed- TODO run a service if we failed and go over the entire list of failed to send follows
    public final static String STATUS_CONNECTION_FAILED="connection failed";
    //If it was already notified then just save it as part of the "history".
    public final static String STATUS_HISTORY="history";


    public String getFollowURIToServer() {
        return followURIToServer;
    }

    public void setFollowURIToServer(String followURIToServer) {
        this.followURIToServer = followURIToServer;
    }

    public double getPriceStarted() {
        return priceStarted;
    }

    public void setPriceStarted(double priceStarted) {
        this.priceStarted = priceStarted;
    }

    public Follow getFollow() {
        return follow;
    }

    public void setFollow(Follow follow) {
        this.follow = follow;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
