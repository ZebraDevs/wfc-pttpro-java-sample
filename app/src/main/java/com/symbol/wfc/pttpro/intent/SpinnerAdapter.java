package com.symbol.wfc.pttpro.intent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter {

    private Context mContext;
    private List<String> mList;

    public SpinnerAdapter(Context context,
                          List<String> list) {
        super(context, R.layout.spinner_drop_down_view, list);
        mContext = context;
        this.mList = list;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.spinner_view_layout, null, true);

        ((TextView) convertView.findViewById(R.id.spinner_default_view)).setText(mList.get(position));

        return convertView;
    }


    public View getCustomView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.spinner_drop_down_view, null, true);
        TextView title = view.findViewById(R.id.title);
        title.setText(mList.get(position));
        ImageView image = view.findViewById(R.id.image_view);
        int drawableId = (mList.get(position).equals("Adhoc")) ? R.mipmap.ic_person_icon : R.mipmap.ic_group_icon_dark;
        image.setImageDrawable(mContext.getDrawable(drawableId));
        return view;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }
}
