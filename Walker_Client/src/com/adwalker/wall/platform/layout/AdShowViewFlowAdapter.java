package com.adwalker.wall.platform.layout;

import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.adwalker.wall.platform.network.GuImageManager;

public class AdShowViewFlowAdapter extends BaseAdapter {
	private Context mContext;
	List<String> detailList;

	public AdShowViewFlowAdapter(Context context, List<String> detailList) {
		this.mContext = context;
		this.detailList = detailList;
	}

	@Override
	public int getCount() {
		if (detailList != null && detailList.size() > 0) {
			return detailList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new ImageView(mContext);
			LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			convertView.setLayoutParams(imgParams);
		}
		if (detailList != null) {
			GuImageManager.INSTANCE.loadBitmap(mContext,detailList.get(position % detailList.size()),(ImageView) convertView, 240, 400);
		}
		return convertView;
	}
}
