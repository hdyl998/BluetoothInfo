package com.fengfz.bmc.common;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hdyl.bttech.R;

public class StringAdapter extends BaseAdapter {

	List<String> list;
	LayoutInflater mInflater;

	public StringAdapter(Context context, List<String> list) {
		mInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_string, null);

			viewHolder = new ViewHolder();
			viewHolder.tvMsg = (TextView) convertView.findViewById(R.id.tv_list_hex);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvMsg.setText(list.get(position));
		return convertView;
	}

	class ViewHolder {
		public TextView tvMsg;
	}

}
