package dor.only.dorking.android.stocksmarketsnotifier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;
import dor.only.dorking.android.stocksmarketsnotifier.Database.DatabaseAccess;


public class ChooseStockActivity extends AppCompatActivity {
    private EditText mEditText;
    private ListView listView;
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





}