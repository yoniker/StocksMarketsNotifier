package dor.only.dorking.android.stocksmarketsnotifier.DataTypes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yoni on 5/17/2016.
 */
public class Security implements Parcelable {
    private String name;
    private String ticker;
    private String moreInfoUri;
    private String country;
    private String stocksMarketName;



    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getName());
        out.writeString(getTicker());
        out.writeString(getMoreInfoUri());
        out.writeString(getCountry());
        out.writeString(getStocksMarketName());
    }

    public static final Parcelable.Creator<Security> CREATOR
            = new Parcelable.Creator<Security>() {
        public Security createFromParcel(Parcel in) {
            Security theSecurity= new Security();
            theSecurity.setName(in.readString());
            theSecurity.setTicker(in.readString());
            theSecurity.setMoreInfoUri(in.readString());
            theSecurity.setCountry(in.readString());
            theSecurity.setStocksMarketName(in.readString());


            return theSecurity;
        }
        public Security[] newArray(int size) {
            return new Security[size];
        }


    };


    public String getCountry() {
        return country;
    }

    public String getStocksMarketName() {
        return stocksMarketName;
    }

    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    public String getMoreInfoUri() {
        return moreInfoUri;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setStocksMarketName(String stocksMarketName) {
        this.stocksMarketName = stocksMarketName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setMoreInfoUri(String moreInfoUri) {
        this.moreInfoUri = moreInfoUri;
    }






}
