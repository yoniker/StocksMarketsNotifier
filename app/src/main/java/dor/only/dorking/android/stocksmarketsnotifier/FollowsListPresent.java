package dor.only.dorking.android.stocksmarketsnotifier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.FollowAndStatus;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;
import dor.only.dorking.android.stocksmarketsnotifier.Database.Followsdb;
import dor.only.dorking.android.stocksmarketsnotifier.SecurityDataGetter.FollowListAdapter;

public class FollowsListPresent extends AppCompatActivity {

    List<FollowAndStatus> mTheList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follows_list_present);
        ListView listView=(ListView)findViewById(R.id.listview_follows);
        Followsdb db=new Followsdb(this);
        //TODO implement it so IT WON'T HAVE DISK ACCESS ON THE UI THREAD! :)
        mTheList=db.getAllFollows(null);
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
