package dor.only.dorking.android.stocksmarketsnotifier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;
import dor.only.dorking.android.stocksmarketsnotifier.Database.DatabaseAccess;
import dor.only.dorking.android.stocksmarketsnotifier.gcm.RegistrationIntentService;


public class MainActivity extends AppCompatActivity {
    private EditText mEditText;
    private ListView listView;
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    List<Security> mStocksInformation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        mEditText=(EditText)findViewById(R.id.search_for_stocks);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateStocksList();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        updateStocksList();

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStocksInformation);
        SecurityListAdapter adapter = new SecurityListAdapter(this,mStocksInformation);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showSecurity=new Intent(getApplicationContext(),SecurityPresent.class);
                Security theSecurity=(Security)parent.getItemAtPosition(position);
                showSecurity.putExtra(SecurityPresent.THE_SECURITY,theSecurity);
                startActivity(showSecurity);
            }
        });

       /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String c=(String)parent.getItemAtPosition(position);
                int a=3;
            }
        }); */


        if (checkPlayServices()) {
            // Because this is the initial creation of the app, we'll want to be certain we have
            // a token. If we do not, then we will start the IntentService that will register this
            // application with GCM.
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
            //boolean sentToken = sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false);
            boolean sentToken=true;
            if (!sentToken) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }

    }


    //TODO make this method work with a cursor loader as in NOT ON THE UI THREAD!
    private void updateStocksList(){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String searchText;
        if(mEditText==null){searchText="";} else {searchText=mEditText.getText().toString();}
        mStocksInformation=databaseAccess.getStocksInfo(searchText);
        databaseAccess.close();
        SecurityListAdapter adapter = new SecurityListAdapter(this,mStocksInformation);
        listView.setAdapter(adapter);

    }



    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(getClass().getSimpleName(), "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}