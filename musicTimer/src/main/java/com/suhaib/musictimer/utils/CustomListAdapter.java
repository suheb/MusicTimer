package com.suhaib.musictimer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haloappstudio.musictimer.R;

import java.util.List;

public class CustomListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ResolveInfo> mAppItem;
    private PackageManager mPackageManager;

    public CustomListAdapter(Context mContext, PackageManager mPackageManager, List<ResolveInfo> mAppItem) {
        this.mContext = mContext;
        this.mAppItem = mAppItem;
        this.mPackageManager = mPackageManager;
    }

    @Override
    public int getCount() {
        return mAppItem.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.app_list_item, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

        imgIcon.setImageDrawable(mAppItem.get(position).loadIcon(mPackageManager));
        txtTitle.setText(mAppItem.get(position).loadLabel(mPackageManager));
        return convertView;
    }

}
