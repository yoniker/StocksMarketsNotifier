package dor.only.dorking.android.stocksmarketsnotifier;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.FollowAndStatus;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;
import dor.only.dorking.android.stocksmarketsnotifier.Database.FollowProvider;

/**
 * Created by Yoni on 6/2/2016.
 */
public class FollowListAdapter  extends CursorAdapter {


    public FollowListAdapter(Context theContext, Cursor theCursor, int flags) {
        super(theContext, theCursor, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.follow_item_list, parent, false);

        return view;
    }



    private String convertCursorRowToUXFormat(Cursor theCursor){
        FollowAndStatus theFollow= FollowProvider.cursorToFollowAndStatus(theCursor);
       Security theSecurity= theFollow.getFollow().getTheSecurity();
        return theSecurity.getTicker()+" "+theSecurity.getName()+" "+theFollow.getPriceStarted();

    }

    private int getStatusImageResource(Cursor theCursor){
        FollowAndStatus theFollow= FollowProvider.cursorToFollowAndStatus(theCursor);
        String status=theFollow.getStatus();
        if(status.equals(FollowAndStatus.STATUS_SENT)) return R.drawable.ic_stopwatch;
        return R.drawable.ic_success;

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        TextView tv = (TextView)view.findViewById(R.id.text_follow_item);
        tv.setText(convertCursorRowToUXFormat(cursor));
        ImageView imageStatus=(ImageView)view.findViewById(R.id.image_follow_status);

        imageStatus.setImageResource(getStatusImageResource(cursor));
    }



}