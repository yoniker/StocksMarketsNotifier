package dor.only.dorking.android.stocksmarketsnotifier;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.FollowAndStatus;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowContract;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowProvider;


public class FollowsListPresent extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID_ALL_FOLLOWS=0;
    private FollowListAdapter mAdapter;


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, FollowContract.sFollowWithSecurity,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);


    }

    /*
    private List<FollowAndStatus> getAllFollows(){
        Cursor result=getContentResolver().query(FollowContract.sFollowWithSecurity,null,null,null,null);
        ArrayList<FollowAndStatus> theList=new ArrayList<>();
        while(result.moveToNext()){
            FollowAndStatus followToAdd= FollowProvider.cursorToFollowAndStatus(result);
            theList.add(followToAdd);

        }
        return theList;


    } */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follows_list_present);
        ListView listView=(ListView)findViewById(R.id.listview_follows);
        //TODO implement it so IT WON'T HAVE DISK ACCESS ON THE UI THREAD! :)
      //  mTheList=getAllFollows();
        mAdapter = new FollowListAdapter(this,null,0);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showSecurity=new Intent(getApplicationContext(),SecurityPresent.class);
                Cursor cursor=(Cursor) parent.getItemAtPosition(position);
                FollowAndStatus theFollow= FollowProvider.cursorToFollowAndStatus(cursor);
                Security theSecurity=theFollow.getFollow().getTheSecurity();
                showSecurity.putExtra(SecurityPresent.THE_SECURITY,theSecurity);
                startActivity(showSecurity);
            }
        });


        getSupportLoaderManager().initLoader(LOADER_ID_ALL_FOLLOWS, null, this);
    }
}
