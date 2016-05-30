package dor.only.dorking.android.stocksmarketsnotifier.SecurityDataGetter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.RealTimeSecurityData;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;

/**
 * Created by Yoni on 5/30/2016.
 */
public class SecurityDataGetter {

    private static boolean isValidNasdaqStock(Security theSecurity){

        if(theSecurity==null ||theSecurity.getTicker()==null){return false;}
        //Alright, first of all we need to verify that the market/country we are talking about is NASDAQ/USA.
        //Otherwise we need to implement a different method of getting the Data..
        if( !theSecurity.getCountry().equals(Security.USA) || !theSecurity.getStocksMarketName().equals(Security.STOCKMARKET_NASDAQ)){
            return false;
        }
        return true;
    }


    //a helper method,removes enclosing " " from a string,as well as \n at the end of the string if it exists.
    private static String unquote(String theString){
        if(theString==null||theString.length()<=1){return theString;}
        String toReturn=theString;
        if(theString.charAt(0)=='"'){
            toReturn=theString.substring(1);
        }

        if(toReturn.charAt(toReturn.length()-1)=='\n'){

            toReturn=(toReturn.substring(0,toReturn.length()-1));

        }
        if(toReturn.charAt(toReturn.length()-1)=='"'){
            //Java's substring returns the string until the second index NOT INCLUDING that's why length()-1 and not length()-2 is used.

            toReturn=(toReturn.substring(0,toReturn.length()-1));


        }
        return toReturn;


    }





    /**
     *
     * a helper method
     * input:unformatted time (eg 4:03pm).
     * output: the same time, but with a much needed space (eg 4:03 pm) so that java's parser can work with that string
     *
     */


    private static String formatTime(String theTime){
        //Let's find the index to which we want to add a space
        final String AM="am"; final String PM="pm";
        int theIndex=Math.max(theTime.indexOf(AM), theTime.indexOf(PM));
        String toReturn=theTime.substring(0,theIndex)+" "+theTime.substring(theIndex,theTime.length());
        return toReturn;



    }

    //http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    static public RealTimeSecurityData getDataFromYahoo(Security theSecurity){

        //Some constants which rely on the parameters that we used in using Yahoo's API
        final  int YAHOO_PERCENTS_CHANGE=0;
        final int YAHOO_TIME_AND_PRICE=1;
        final int YAHOO_DATE=2;
        final String GET="GET";
        final String YAHOO_SYMBOLS="s";
        if(!isValidNasdaqStock(theSecurity)){return null;}
        HttpURLConnection connection=null;
        InputStream inputStream=null;
        try{

            //Here is info about Yahoo's API in case we want to modify it in the future:
            //https://greenido.wordpress.com/2009/12/22/yahoo-finance-hidden-api/
            //http://www.jarloo.com/yahoo_finance/

            String theUrl="http://finance.yahoo.com/d/quotes.csv?"+YAHOO_SYMBOLS+"="+theSecurity.getTicker()+"&f=p2ld1";
            URL url = new URL(theUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(GET);
            inputStream = connection.getInputStream();
            String response = convertStreamToString(inputStream);
            //Let the parsing begin! Response is in csv so...
            String[] responseSections=response.split(",");
            for(int i=0; i<responseSections.length; ++i){
                responseSections[i]=unquote(responseSections[i]);}


            //Date is starightforward
            String theDate=responseSections[YAHOO_DATE];

            //This is Yahoo's format for date/price 4:02pm - <b>51.66</b>
            String timeAndPrice=responseSections[YAHOO_TIME_AND_PRICE];
            //so time is simply to the left of the first space
            String time=timeAndPrice.split(" ")[0];
            //and price is to the right of <b> and then left of </b>
            String price=timeAndPrice.split("<b>")[1];
            price=price.split("</b>")[0];

            //Here I need to add a space to the time string between the actual time and the am/pm part so Java's parser can parse it correctly
            time=formatTime(time);


            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
            //Yahoo returns the time in EST time so let's work with that time zone here
            TimeZone.setDefault(TimeZone.getTimeZone("EST"));
            dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
            Date date = dateFormat.parse(theDate+" "+time);


            //Change in percents is always a String,either for example "+0.23%" or "-2.43%".
            String percentsChange=responseSections[YAHOO_PERCENTS_CHANGE];
            //Alright we know it's percents,let's get rid of that char
            percentsChange=percentsChange.substring(0,percentsChange.length()-1);


            RealTimeSecurityData actualData=new RealTimeSecurityData();
            actualData.setLastData(new Timestamp(date.getTime()));
            actualData.setPercentChange(Double.valueOf(percentsChange));
            actualData.setPrice(Double.valueOf(price));
            actualData.setTheSecurity(theSecurity);
            return actualData;


        }

        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block if parsing date wasn't successful
            e.printStackTrace();
        }

        finally{
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }}
            if(connection!=null){
                connection.disconnect();
            }

        }



        return null;


    }


}
