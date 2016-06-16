package dor.only.dorking.android.stocksmarketsnotifier.ConnectionServer;

/**
 * Created by Yoni on 6/16/2016.
 */
public class ServerResponse {

    private int status;
    private String body;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
