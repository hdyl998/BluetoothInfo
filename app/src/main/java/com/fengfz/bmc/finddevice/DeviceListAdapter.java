package com.fengfz.bmc.finddevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hdyl.bttech.R;

import java.util.List;
//设备信息适配器
public class DeviceListAdapter extends BaseAdapter {

	List<DeviceItem> list;
	LayoutInflater mInflater;

	public DeviceListAdapter(Context context, List<DeviceItem> list) {
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
			convertView = mInflater.inflate(R.layout.item_list_device, null);

			viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_itema_name);
			viewHolder.tvAddr = (TextView) convertView.findViewById(R.id.tv_itema_addr);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		DeviceItem item=list.get(position);//得到条目
		viewHolder.tvName.setText(item.name);//名字
		viewHolder.tvAddr.setText(item.addr);//地址
		return convertView;
	}

	class ViewHolder {
		TextView tvName,tvAddr;
	}

}
