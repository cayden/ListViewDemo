package com.cayden.listview;

import java.util.ArrayList;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	ArrayList<ApkEntity> apk_list;
	LayoutInflater inflater;

	public MyAdapter(Context context, ArrayList<ApkEntity> apk_list) {
		this.apk_list = apk_list;
		this.inflater = LayoutInflater.from(context);
	}

	public void onDateChange(ArrayList<ApkEntity> apk_list) {
		this.apk_list = apk_list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return apk_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return apk_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ApkEntity entity = apk_list.get(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_layout, null);
			holder.name_tv = (TextView) convertView
					.findViewById(R.id.item3_apkname);
			holder.des_tv = (TextView) convertView
					.findViewById(R.id.item3_apkdes);
			holder.info_tv = (TextView) convertView
					.findViewById(R.id.item3_apkinfo);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name_tv.setText(entity.getName());
		holder.des_tv.setText(entity.getDes());
		holder.info_tv.setText(entity.getInfo());
		return convertView;
	}

	class ViewHolder {
		TextView name_tv;
		TextView des_tv;
		TextView info_tv;
	}
}
