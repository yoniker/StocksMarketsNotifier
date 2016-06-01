package dor.only.dorking.android.stocksmarketsnotifier.SecurityDataGetter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.FollowAndStatus;
import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;

/**
 * Created by Yoni on 6/2/2016.
 */
public class FollowListAdapter  extends BaseAdapter {
    private Context mContext;
    List<FollowAndStatus> theList;

    public FollowListAdapter(Context c, List<FollowAndStatus> theList) {
        mContext = c;
        this.theList = theList;
    }

    public int getCount() {
        return theList.size();
    }

    public Object getItem(int position) {
        return theList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            //TODO if needed,create a layout file. Also see if there is a way to avoid accessing list.get(position).
            tv = new TextView(mContext);
            tv.setTextAppearance(mContext, android.R.style.TextAppearance_Large);
        } else {
            tv = (TextView) convertView;
        }

        FollowAndStatus theFollow=theList.get(position);

        Security theSecurity=theList.get(position).getFollow().getTheSecurity();

        tv.setText(theSecurity.getName()+"/"+theSecurity.getTicker()+" "+theFollow.getPriceStarted());




        return tv;
    }

}