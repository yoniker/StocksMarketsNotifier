package dor.only.dorking.android.stocksmarketsnotifier;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.FollowAndStatus;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowContract;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowProvider;
import dor.only.dorking.android.stocksmarketsnotifier.SecurityDataGetter.FollowListAdapter;



public class FollowsListPresent extends AppCompatActivity {

    List<FollowAndStatus> mTheList;

    private List<FollowAndStatus> getAllFollows(){
        Cursor result=getContentResolver().query(FollowContract.sFollowWithSecurity,null,null,null,null);
        ArrayList<FollowAndStatus> theList=new ArrayList<>();
        while(result.moveToNext()){
            FollowAndStatus followToAdd= FollowProvider.cursorToFollowAndStatus(result);
            theList.add(followToAdd);

        }
        return theList;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follows_list_present);
        ListView listView=(ListView)findViewById(R.id.listview_follows);
        //TODO implement it so IT WON'T HAVE DISK ACCESS ON THE UI THREAD! :)
        //mTheList=db.getAllFollows(null);
        mTheList=getAllFollows();
        FollowListAdapter adapter = new FollowListAdapter(this,mTheList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showSecurity=new Intent(getApplicationContext(),SecurityPresent.class);
                FollowAndStatus theFollow=(FollowAndStatus)parent.getItemAtPosition(position);
                Security theSecurity=theFollow.getFollow().getTheSecurity();
                showSecurity.putExtra(SecurityPresent.THE_SECURITY,theSecurity);
                startActivity(showSecurity);
            }
        });


    }
}
