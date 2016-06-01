package dor.only.dorking.android.stocksmarketsnotifier;

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
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.RealTimeSecurityData;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;
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
        AsyncTask<Security,Void,RealTimeSecurityData> theTask=new AsyncTask<Security, Void, RealTimeSecurityData>() {
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


            }
        };

        //We need Asynctasks to run in parallel,otherwise it will take forever eg first get info from yahoo and only then get webpage from Nasdq or vice versa
        // See https://developer.android.com/reference/android/os/AsyncTask.html

        theTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mTheSecurity);




        mStockWebsite.loadUrl(mTheSecurity.getMoreInfoUri());
        mStockWebsite.getSettings().setLoadWithOverviewMode(true);
        mStockWebsite.getSettings().setUseWideViewPort(true);
        mStockWebsite.getSettings().setBuiltInZoomControls(true);
        mStockWebsite.getSettings().setDisplayZoomControls(false);
        mStockWebsite.setOnLongClickListener(this);






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
            double percents=Double.valueOf(mHigherValuePercents.getText().toString());
            percents=Math.abs(percents);
            double newHigherAbsolute=currentPrice*(1+percents/100);
            mHigherValueAbsolute.setText(String.valueOf(newHigherAbsolute));

        }

        String lowerPercents;
        if(!(lowerPercents=mLowerValuePercents.getText().toString()).equals("")){
            double percents=Double.valueOf(mLowerValuePercents.getText().toString());
            percents=Math.abs(percents);
            if(percents>100){percents=100; mLowerValuePercents.setText("100");}
            double newLowerAbsolute=currentPrice*(1-percents/100);
            mLowerValueAbsolute.setText(String.valueOf(newLowerAbsolute));

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

            //Alright if we are here it means that the value itself is fine,so let's just change the value in the percents field
            double newHigherPercents=((numberEntered/currentPrice)-1)*100;
            mHigherValuePercents.setText(String.valueOf(newHigherPercents));
       }
        if(v==mHigherValuePercents){
            //Well,in here there can't be a bad value,so just update the field in the other "higher" edittext
            setValuesAccordingToPercents();

        }
        if(v==mLowerValueAbsolute){
            //First of all let's check if this is legal value
            double numberEntered=Double.valueOf(mLowerValueAbsolute.getText().toString());
            if(numberEntered>=currentPrice ||numberEntered<0){
                Toast.makeText(this, R.string.lower_input_error_message,Toast.LENGTH_LONG).show();
                setValuesAccordingToPercents();
                return;
            }

            //Alright if we are here it means that the value itself is fine,so let's just change the value in the percents field
            double newLowerPercents=Math.abs((1-numberEntered/currentPrice)*100);
            mLowerValuePercents.setText(String.valueOf(newLowerPercents));


        }
        if(v==mLowerValuePercents){
            //Well,in here there can't be a bad value,so just update the field in the other "higher" edittext
           setValuesAccordingToPercents();


        }
    }

    @Override
    public void onClick(View v) {
        if(v==mSendFollowButton){
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
            connectionServer.sendToServer(theFollow);






        }
    }
}
