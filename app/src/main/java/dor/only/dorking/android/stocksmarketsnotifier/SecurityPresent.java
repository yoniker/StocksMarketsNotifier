package dor.only.dorking.android.stocksmarketsnotifier;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Calendar;

import dor.only.dorking.android.stocksmarketsnotifier.ConnectionServer.ConnectionServer;
import dor.only.dorking.android.stocksmarketsnotifier.Contants.Constants;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Follow;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.FollowAndStatus;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.RealTimeSecurityData;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowContract;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowProvider;
import dor.only.dorking.android.stocksmarketsnotifier.SecurityDataGetter.SecurityDataGetter;

public class SecurityPresent extends AppCompatActivity implements View.OnLongClickListener,View.OnFocusChangeListener,View.OnClickListener {

    private Security mTheSecurity;
    private  TextView mStockName;
    private WebView mStockWebsite;
    private TextView mRealTimeData;
    private LinearLayout mFollowDetails;
    private ProgressBar mProgressBarRealTimeValue;
    private boolean mLoadedRTValues=false;
    private RealTimeSecurityData mRealTimeSecurityData=null;
    private Button mSendFollowButton;
    private Button mDeleteFollowButton;
    private boolean mFollowExistedAlready=false;
    private FollowAndStatus mFollowAndStatus=null;

    private EditText mHigherValueAbsolute,mHigherValuePercents,mLowerValueAbsolute,mLowerValuePercents;


    public static final String THE_SECURITY="THE SECURITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_present);
        mRealTimeData=(TextView)findViewById(R.id.text_rtinfo);
        mFollowDetails=(LinearLayout)findViewById(R.id.llayout_follow_details);
        mFollowDetails.setVisibility(View.GONE);
        (mHigherValueAbsolute=(EditText)findViewById(R.id.editText_higher_value_absolute)).setOnFocusChangeListener(this);
        (mHigherValuePercents=(EditText)findViewById(R.id.editText_higher_value_percents)).setOnFocusChangeListener(this);
        (mLowerValueAbsolute=(EditText)findViewById(R.id.editText_lower_value_absolute)).setOnFocusChangeListener(this);
        (mLowerValuePercents=(EditText)findViewById(R.id.editText_lower_value_percents)).setOnFocusChangeListener(this);
        (mSendFollowButton=(Button)findViewById(R.id.button_start_follow)).setOnClickListener(this);
        (mDeleteFollowButton=(Button) findViewById(R.id.button_stop_follow)).setOnClickListener(this);


        mProgressBarRealTimeValue=(ProgressBar)findViewById(R.id.progressbar_real_time_values);
        mProgressBarRealTimeValue.setVisibility(View.VISIBLE);


        mTheSecurity=getIntent().getExtras().getParcelable(THE_SECURITY);
        if(mTheSecurity==null){
            //TODO better error handling
            finish();
        }

        mStockWebsite=(WebView) findViewById(R.id.web_stock_website_stock_website);
        mStockName=(TextView)findViewById(R.id.text_stock_name);
        mStockName.setText(mTheSecurity.getName());
        AsyncTask<Security,Void,RealTimeSecurityData> getDataFromYahooTask=new AsyncTask<Security, Void, RealTimeSecurityData>() {
            @Override
            protected RealTimeSecurityData doInBackground(Security... params) {
                Security theSecurity=params[0];
                RealTimeSecurityData theData= SecurityDataGetter.getDataFromYahoo(theSecurity);
                return theData;
            }

            @Override
            protected void onPostExecute(RealTimeSecurityData rtData) {
                super.onPostExecute(rtData);
                if(rtData!=null) {
                    mRealTimeData.setText("Price:" + rtData.getPrice()  + " %change:" + rtData.getPercentChange());
                    mProgressBarRealTimeValue.setVisibility(View.GONE);
                    mFollowDetails.setVisibility(View.VISIBLE);
                    mRealTimeSecurityData = rtData;
                    mLoadedRTValues = true;
                }

                loadFollows();


            }
        };

        //We need Asynctasks to run in parallel,otherwise it will take forever eg first get info from yahoo and only then get webpage from Nasdq or vice versa
        // See https://developer.android.com/reference/android/os/AsyncTask.html

        getDataFromYahooTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mTheSecurity);




        mStockWebsite.loadUrl(mTheSecurity.getMoreInfoUri());
        mStockWebsite.getSettings().setLoadWithOverviewMode(true);
        mStockWebsite.getSettings().setUseWideViewPort(true);
        mStockWebsite.getSettings().setBuiltInZoomControls(true);
        mStockWebsite.getSettings().setDisplayZoomControls(false);
        mStockWebsite.setOnLongClickListener(this);




    }


    //TODO: support more than one follow, for now I will implement just one follow per security
    private void loadFollows(){
        FollowProvider db=new FollowProvider();
        //TODO take this disk access off the UI thread (Cursor Loader)
        Cursor result=null;
        try {
            result = getContentResolver().query(FollowContract.buildFollowWithSecurity(mTheSecurity), null, null, null, null);


            if (result.moveToFirst()) {
                double lowerValue = result.getDouble(result.getColumnIndex(FollowContract.FollowEntry.COLUMN_PARAM1));
                double higherValue = result.getDouble(result.getColumnIndex(FollowContract.FollowEntry.COLUMN_PARAM2));
                mHigherValueAbsolute.setText(String.valueOf(higherValue));
                mLowerValueAbsolute.setText(String.valueOf(lowerValue));
                setValuesAccordingToAbsolutePrice();
                mSendFollowButton.setText("Update Follow");
                mFollowExistedAlready = true;
                mDeleteFollowButton.setVisibility(View.VISIBLE);
                mFollowAndStatus=FollowProvider.cursorToFollowAndStatus(result);



            }
        }finally {
            if(result!=null){result.close();}

        }




    }

    @Override
    public boolean onLongClick(View v) {
        if(v==mStockWebsite ) {





            if (mLoadedRTValues) {
                if (mFollowDetails.getVisibility() == View.VISIBLE) {


                    mFollowDetails.setVisibility(View.GONE);
                } else if (mFollowDetails.getVisibility() == View.GONE) {
                    mFollowDetails.setVisibility(View.VISIBLE);

                }
            }
        }

        return false;
    }


    /**
     * Helper method which changes the corresponding fields of higher and lower 'absolute' prices according to the percents present
     */
    private void setValuesAccordingToPercents(){
        double currentPrice=mRealTimeSecurityData.getPrice();
        String higherPercents;
        if(!(higherPercents=mHigherValuePercents.getText().toString()).equals("")){
            abs(mHigherValuePercents);
            double percents=Double.valueOf(higherPercents);
            double newHigherAbsolute=currentPrice*(1+percents/100);
            mHigherValueAbsolute.setText(String.valueOf(newHigherAbsolute));

        }

        String lowerPercents;
        if(!(lowerPercents=mLowerValuePercents.getText().toString()).equals("")){
            abs(mLowerValueAbsolute);
            double percents=Double.valueOf(lowerPercents);
            if(percents>100){percents=100; mLowerValuePercents.setText("100");}
            double newLowerAbsolute=currentPrice*(1-percents/100);
            mLowerValueAbsolute.setText(String.valueOf(newLowerAbsolute));

        }

    }

    private void setValuesAccordingToAbsolutePrice() {
        double currentPrice = mRealTimeSecurityData.getPrice();
        String higherLimit = mHigherValueAbsolute.getText().toString();
        if (!higherLimit.equals("")) {
            double numberEntered = Double.valueOf(higherLimit);
            if (numberEntered > currentPrice) {
                //Alright if we are here it means that the value itself is fine,so let's just change the value in the percents field
                double newHigherPercents = ((numberEntered / currentPrice) - 1) * 100;
                mHigherValuePercents.setText(String.valueOf(newHigherPercents));
            }
        }

        String lowerLimit = mLowerValueAbsolute.getText().toString();
        if (!lowerLimit.equals("")) {
            double numberEntered = Double.valueOf(lowerLimit);

            if (numberEntered < currentPrice || numberEntered >= 0) {

                //Alright if we are here it means that the value itself is fine,so let's just change the value in the percents field
                double newLowerPercents = Math.abs((1 - numberEntered / currentPrice) * 100);
                mLowerValuePercents.setText(String.valueOf(newLowerPercents));
            }


        }

    }




    /*

    a helper method.
    Input: any EditText object.
    If the text in that edittext is double and negative it will change it to abs(number).
     */
    private static void abs(EditText v){
        if(v==null||(v.getText().toString()).equals("")){return;}
        String theText=v.getText().toString();
        try{
           double theNumber= Double.valueOf(theText);
            if(theNumber<0){
                v.setText(String.valueOf(Math.abs(theNumber)));
            }


        }catch(NumberFormatException e){}

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
       //I want to do stuff only in case there is no focus on the view (the user might have entered some input) and so:
        if(hasFocus){return;}
        //For now,I am interested only in EditText objects which have some text in them
        if(!(v instanceof  EditText) || ((EditText) v).getText()==null || (((EditText) v).getText().toString()).equals("")){
            return;

        }

        //On top of that, I want to make sure that there is Data, and that data is valid:
        if(mRealTimeData==null){return;}
        double currentPrice=mRealTimeSecurityData.getPrice();
        if(currentPrice<=0){
            return;} //if current price is negative or 0 obviously something went wrong...

        //Also let's make sure that whatever is entered there is not negative!
        abs((EditText) v);

        if(v==mHigherValueAbsolute){
            //First of all let's check if this is legal value
            double numberEntered=Double.valueOf(mHigherValueAbsolute.getText().toString());
            if(numberEntered<=currentPrice){
                Toast.makeText(this, R.string.higher_value_input_error_message,Toast.LENGTH_LONG).show();
                setValuesAccordingToPercents();
                return;
            }

            setValuesAccordingToAbsolutePrice();
       }

        if(v==mLowerValueAbsolute){
            //First of all let's check if this is legal value
            double numberEntered=Double.valueOf(mLowerValueAbsolute.getText().toString());
            if(numberEntered>=currentPrice ||numberEntered<0){
                Toast.makeText(this, R.string.lower_input_error_message,Toast.LENGTH_LONG).show();
                setValuesAccordingToPercents();
                return;
            }
            setValuesAccordingToAbsolutePrice();


        }

        if(v==mHigherValuePercents){
            setValuesAccordingToPercents();
        }
        if(v==mLowerValuePercents){
           setValuesAccordingToPercents();
        }
    }


    void addToFollowsDB(FollowAndStatus followAndStatus){


        //TODO take it off the main thread (AsyncTask).
        Security theSecurity=followAndStatus.getFollow().getTheSecurity();
        Cursor securitySearchResult=null;
        long securityId;
        try{
        //First let's check if the security exists on the database. If it doesn't then add it, if it does let's get its id.
         securitySearchResult=getContentResolver().query(FollowContract.SecurityEntry.CONTENT_URI,
                new String[]{FollowContract.SecurityEntry._ID},
                FollowContract.sSecurityDetails,
                new String[]{theSecurity.getTicker(),theSecurity.getStocksMarketName()},
                null);



        if(securitySearchResult.moveToFirst()){securityId=securitySearchResult.getLong(0);
        } else {
            Uri addedSecurityUri=getContentResolver().insert(FollowContract.SecurityEntry.CONTENT_URI,FollowProvider.securityContentValues(theSecurity));
            securityId= ContentUris.parseId(addedSecurityUri);

        } }finally {
            if(securitySearchResult!=null){securitySearchResult.close();}

        }


        ContentValues followAndStatusValues=FollowProvider.followAndStatusContentValues(followAndStatus);
        followAndStatusValues.put(FollowContract.FollowEntry.COLUMN_SECURITY_ID,securityId);

        Cursor foundFollow=null;
        try {
            //For now we will support only one follow per security, so if a follow already exists for this particular security,we will just change it
             foundFollow = getContentResolver().query(FollowContract.FollowEntry.CONTENT_URI,
                    new String[]{FollowContract.FollowEntry._ID},
                    FollowContract.FollowEntry.COLUMN_SECURITY_ID + "=?",
                    new String[]{String.valueOf(securityId)},
                    null);

            if (foundFollow.moveToFirst()) {
                long followId = foundFollow.getLong(0);
                getContentResolver().update(FollowContract.FollowEntry.CONTENT_URI,
                        followAndStatusValues,
                        FollowContract.FollowEntry._ID + "=?",
                        new String[]{String.valueOf(followId)});

            } else {

                getContentResolver().insert(FollowContract.FollowEntry.CONTENT_URI, followAndStatusValues);
            }

            return;
        }
        finally {
            if(foundFollow!=null){foundFollow.close();}
        }







    }

    //a method which invokes when the user wants to start following (or update the information of an existing follow)

    private void sendFollow(){

        //Alright, First of all we need to make sure that the values we have for high/low are valid
        String  higherValueInTextbox=mHigherValueAbsolute.getText().toString();
        String lowerValueInTextbox=mLowerValueAbsolute.getText().toString();
        if(higherValueInTextbox.equals("") || lowerValueInTextbox.equals("")){
            Toast.makeText(this,"You need to fill both high and low limits to follow!",Toast.LENGTH_LONG).show();
            return;
        }
        double higherValue=Double.valueOf(higherValueInTextbox);
        double lowerValue=Double.valueOf(lowerValueInTextbox);
        double currentPrice=mRealTimeSecurityData.getPrice();
        if(lowerValue>=currentPrice || higherValue<=currentPrice){
            Toast.makeText(this,"Can't follow since current price is already within those limits!",Toast.LENGTH_LONG).show();
            return;

        }

        if(!Constants.followReady(this)){
            Toast.makeText(this,"Some information is missing on file,is server down?",Toast.LENGTH_LONG).show();
            return;

        }

        //Let's prepare the follow
        Follow theFollow=new Follow();
        theFollow.setFollowType(Follow.FOLLOW_TYPE_BETWEEN);
        theFollow.setTheSecurity(mTheSecurity);
        double[] theArray=new double[Follow.NUMBER_OF_PARAMETERS];
        theArray[0]=lowerValue;
        theArray[1]=higherValue;
        theFollow.setFollowParams(theArray);
        Timestamp rightNow=new Timestamp(Calendar.getInstance().getTime().getTime());
        theFollow.setStart(rightNow);
        theFollow.setExpiry(rightNow);

        ConnectionServer connectionServer=new ConnectionServer(this);
        FollowAndStatus followToSend= new FollowAndStatus();
        followToSend.setFollow(theFollow);
        if(mFollowExistedAlready && mFollowAndStatus!=null){
            followToSend.setFollowURIToServer(mFollowAndStatus.getFollowURIToServer());

        }
        FollowAndStatus followAndStatus=new FollowAndStatus();
        followAndStatus.setFollow(theFollow);
        //TODO change the status when we know it is successful (launch a service?)
        followAndStatus.setStatus(FollowAndStatus.STATUS_SENT);
        followAndStatus.setPriceStarted(mRealTimeSecurityData.getPrice());
        //TODO take this off the main thread (AsyncTask).
        addToFollowsDB(followAndStatus);
        connectionServer.sendToServer(followToSend,mFollowExistedAlready);


        Intent launchFollowsList=new Intent(this,FollowsListPresent.class);
        startActivity(launchFollowsList);




    }


    private void deleteFollow(){

        //First let's find the security ID
        long securityId=Constants.getSecurityId(this,mTheSecurity);
        if(securityId!=Constants.SECURITY_NOT_FOUND){
            //TODO do this off the UI thread (Asynctask)
            getContentResolver().delete(FollowContract.FollowEntry.CONTENT_URI,
                    FollowContract.FollowEntry.COLUMN_SECURITY_ID+"=?",
                    new String[]{String.valueOf(securityId)}
                    );
            getContentResolver().delete(FollowContract.SecurityEntry.CONTENT_URI,
                    FollowContract.SecurityEntry._ID+"=?",
                    new String[]{String.valueOf(securityId)});
            //TODO contact the server and delete the follow
            ConnectionServer connectionServer=new ConnectionServer(this);
            if(mFollowAndStatus!=null) {connectionServer.deleteFromServer(mFollowAndStatus);}


        }

        mDeleteFollowButton.setVisibility(View.GONE);
        mFollowExistedAlready=false;
        mFollowAndStatus=null;
        mLowerValueAbsolute.setText("");
        mLowerValuePercents.setText("");
        mHigherValueAbsolute.setText("");
        mHigherValuePercents.setText("");



    }

    @Override
    public void onClick(View v) {
        if(v==mSendFollowButton){
            sendFollow();
        }

        if(v==mDeleteFollowButton){
            deleteFollow();

        }
    }
}
