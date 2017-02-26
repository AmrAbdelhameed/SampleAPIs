package com.example.amr.sampleapptounderstandapis;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MainGridViewAdapter extends ArrayAdapter<MainGridItem> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<MainGridItem> mGridData = new ArrayList<MainGridItem>();

    public MainGridViewAdapter(Context mContext, int layoutResourceId, ArrayList<MainGridItem> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    public void setGridData(ArrayList<MainGridItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView) row.findViewById(R.id.textView);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        MainGridItem item = mGridData.get(position);
        holder.text.setText(item.getIDD()+" "+item.getName()+" "+item.getNumber());
        return row;
    }

    static class ViewHolder {
        TextView text;
    }
}