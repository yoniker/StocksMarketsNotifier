package dor.only.dorking.android.stocksmarketsnotifier.DataTypes;

/**
 * Created by Yoni on 5/30/2016.
 */
import java.sql.Timestamp;

public class RealTimeSecurityData {
    //Information about the security itself
    private Security theSecurity;
    //From when is the last data taken?
    private Timestamp lastData;
    //What was it actually bought and sold for?
    private double price;
    //The percents change from previous day
    private double percentChange;

    public Security getTheSecurity() {
        return theSecurity;
    }
    public void setTheSecurity(Security theSecurity) {
        this.theSecurity = theSecurity;
    }
    public Timestamp getLastData() {
        return lastData;
    }
    public void setLastData(Timestamp lastData) {
        this.lastData = lastData;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public double getPercentChange() {
        return percentChange;
    }
    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }


}
