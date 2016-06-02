package dor.only.dorking.android.stocksmarketsnotifier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import dor.only.dorking.android.stocksmarketsnotifier.Contants.Constants;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.UserFollows;
import dor.only.dorking.android.stocksmarketsnotifier.gcm.RegistrationIntentService;
import  dor.only.dorking.android.stocksmarketsnotifier.ConnectionServer.ConnectionServer;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    Button mSignInButton=null;
    EditText mTextBox=null;

    @Override
    public void onClick(View v) {
        if(v==mSignInButton){
            signIn();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mSignInButton=(Button)findViewById(R.id.button_sign_in);
        mTextBox=(EditText)findViewById(R.id.etext_user_name_email);
        mSignInButton.setOnClickListener(this);
        if(Constants.hasId(this)){
            //Automatic sign in
            signIn();

        }

        if (checkPlayServices()) {
            // Because this is the initial creation of the app, we'll want to be certain we have
            // a token. If we do not, then we will start the IntentService that will register this
            // application with GCM.
            if (!Constants.hasGCMToken(this)) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }




    }

    private void signIn(){
        String enteredText=mTextBox.getText().toString();
        //So if text is empty,and there is no ID yet, don't do anything!
        boolean someTextWasEntered=enteredText!=null && !enteredText.equals("");
        if(!someTextWasEntered&&!Constants.hasId(this)){return;}
        //Save the 'id' in shared preferences if there is some text there
        if(someTextWasEntered)
        {Constants.writeToSharedPref(this,Constants.SP_MY_EMAIL_PASSWORD,enteredText);}
            //Do the actual sign in procedure

        //If there is GCM also, then we have a user ready,just add this ID and send to the server
        if(Constants.hasGCMToken(this)){
            UserFollows theUser=Constants.getUserFromPreferences(this);
            //Since that text potentially might not be written yet in shared preferences- the write to sp is async on a different thread
            if(someTextWasEntered &&!Constants.followReady(this)) {theUser.setEmailPassword(enteredText);
            ConnectionServer connectionServer=new ConnectionServer(this);
            connectionServer.sendToServer(theUser);}

        }
            Intent launchChooseStock=new Intent(this,ChooseStockActivity.class);

        startActivity(launchChooseStock);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this,"Device not supported (Google play services version isn't Dor)",Toast.LENGTH_LONG).show();
                Log.i(getClass().getSimpleName(), "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
