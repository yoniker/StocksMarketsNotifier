package dor.only.dorking.android.stocksmarketsnotifier;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.RealTimeSecurityData;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;
import dor.only.dorking.android.stocksmarketsnotifier.SecurityDataGetter.SecurityDataGetter;

public class SecurityPresent extends AppCompatActivity implements View.OnLongClickListener {

    Security mTheSecurity;
    TextView mStockName;
    WebView mStockWebsite;
    TextView mRealTimeData;
    LinearLayout mFollowDetails;
    ProgressBar mProgressBarRealTimeValue;
    boolean mLoadedRTValues=false;
    RealTimeSecurityData mRealTimeSecurityData=null;


    public static final String THE_SECURITY="THE SECURITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_present);
        mFollowDetails=(LinearLayout)findViewById(R.id.llayout_follow_details);
        mFollowDetails.setVisibility(View.GONE);
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
                mRealTimeData.setText("Price:"+rtData.getPrice()+" date:"+rtData.getLastData()+" %change:"+rtData.getPercentChange());
                mProgressBarRealTimeValue.setVisibility(View.GONE);
                mFollowDetails.setVisibility(View.VISIBLE);
                mRealTimeSecurityData=rtData;
                mLoadedRTValues=true;


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
        mRealTimeData=(TextView)findViewById(R.id.text_rtinfo);





    }


    @Override
    public boolean onLongClick(View v) {
        if(v==mStockWebsite&& mLoadedRTValues){

           if( mFollowDetails.getVisibility()==View.VISIBLE){


            mFollowDetails.setVisibility(View.GONE);}
       else if(mFollowDetails.getVisibility()==View.GONE){
            mFollowDetails.setVisibility(View.VISIBLE);

        }
        }

        return false;
    }


}
