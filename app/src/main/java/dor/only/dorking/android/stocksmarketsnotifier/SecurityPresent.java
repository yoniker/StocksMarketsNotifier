package dor.only.dorking.android.stocksmarketsnotifier;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;

public class SecurityPresent extends AppCompatActivity {

    Security mTheSecurity;
    TextView mStockName;
    TextView mStockWebsite;

    public static final String THE_SECURITY="THE SECURITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_present);
        mTheSecurity=getIntent().getExtras().getParcelable(THE_SECURITY);
        if(mTheSecurity==null){
            //TODO better error handling
            finish();
        }

        mStockWebsite=(TextView)findViewById(R.id.text_stock_website);
        mStockName=(TextView)findViewById(R.id.text_stock_name);
        mStockName.setText(mTheSecurity.getName());
        mStockWebsite.setText(mTheSecurity.getMoreInfoUri());



    }


}
