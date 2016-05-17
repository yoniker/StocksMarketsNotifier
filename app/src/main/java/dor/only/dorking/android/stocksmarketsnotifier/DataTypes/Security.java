package dor.only.dorking.android.stocksmarketsnotifier.DataTypes;

/**
 * Created by Yoni on 5/17/2016.
 */
public class Security {
    private String name;
    private String ticker;
    private String moreInfoUri;
    private String country;


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

    private String stocksMarketName;

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
