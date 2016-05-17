package dor.only.dorking.android.stocksmarketsnotifier;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import dor.only.dorking.android.stocksmarketsnotifier.DataTypes.Security;

public class SecurityListAdapter extends BaseAdapter {
    private Context mContext;
    List<Security> theList;

    public SecurityListAdapter(Context c, List<Security> theList) {
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

            Security theSecurity=theList.get(position);

            tv.setText(theSecurity.getName()+"/"+theSecurity.getTicker());




        return tv;
    }

}