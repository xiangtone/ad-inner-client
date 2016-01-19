package com.adwalker.wall.frame;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import com.adwalker.wall.init.AdInitialization;
import com.adwalker.wall.platform.AdConstants;
import com.adwalker.wall.platform.bean.AdItemBean;
import com.adwalker.wall.platform.bean.WalkerAdBean;
import com.adwalker.wall.platform.network.GuHttpNetwork;
import com.adwalker.wall.platform.network.GuNotifyManage.NotifyTask;
import com.adwalker.wall.platform.util.AdApkUtil;
import com.adwalker.wall.platform.util.GuLogUtil;
import com.adwalker.wall.platform.util.MobileUtil;
import com.adwalker.wall.platform.util.GuUtil;

public class AdFrameDataUtil {

	public static ImageView getImageView(Context context, float width,
			float height) {
		ImageView view = new ImageView(context);
		LayoutParams params = new LayoutParams(
				MobileUtil.dip2px(context, width), MobileUtil.dip2px(context,
						height));
		view.setLayoutParams(params);
		view.setScaleType(ScaleType.FIT_XY);
		view.setBackgroundColor(Color.rgb(59, 62, 71));
		view.getBackground().setAlpha(150);// 0~255透明度值
		return view;

	}

	public static List<WalkerAdBean> adListFromServer(Context context,
			int page_number, int page_type, int image_type) {
		
		  //得到接口  
		String codeStr =  "uuid="+ MobileUtil.getMobileId(context) + "&pageNo=" + page_number
				+ "&pageSize=" + AdConstants.ADLIST_PAGE_SIZE + "&page_type="
				+ page_type + "&image_type=" + image_type;
		String serverUrl = AdConstants.SERVER_ADLIST;
	//	Log.e("codeStr           ", codeStr);
		
		byte[] bytes = GuHttpNetwork.dataFromServer(context, serverUrl, codeStr);
		if (bytes == null) {
			return null;
		}
		try {
			
			JSONObject returnJson = GuUtil.Bytes2Json(bytes);
			String status = returnJson.getString("status");
			JSONObject data = returnJson.getJSONObject("data");
			
			if (data != null) {
				if (status.equalsIgnoreCase("ok")) {
					List<WalkerAdBean> wallList = new ArrayList<WalkerAdBean>();
					if (page_type == AdConstants.PageTypeScore
							|| page_type == AdConstants.PageTypeHot) {
						JSONObject wallPage = data.getJSONObject("wallPage");
						if (AdConstants.ADLIST_TOTAL_NUM == 0) {
							AdConstants.ADLIST_TOTAL_NUM = wallPage
									.getInt("resultSize");
							AdConstants.ADLIST_TOTAL_PAGE = wallPage
									.getInt("pageCount");
						}
					}
					JSONArray wallVo = data.getJSONArray("adList");
					if (wallVo != null && wallVo.length() > 0) {
						for (int i = 0; i < wallVo.length(); i++) {
							JSONObject wallObject = wallVo.getJSONObject(i);
							WalkerAdBean wallInfo = json2WallInfo(wallObject,
									page_type);

							if (wallInfo != null) {
								if (AdApkUtil.isInstalled(context,
										wallInfo.packageName)) {
									wallInfo.state = AdConstants.APP_INSTALLED;
								}
								if (wallInfo.state == 0) {
									NotifyTask notifyThread = AdInitialization.notifyList
											.get(String.valueOf(wallInfo.id));
									if (notifyThread != null) {
										wallInfo.state = notifyThread.wallInfo.state;
									}
								}
								wallList.add(wallInfo);
							}
						}
						return wallList;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static WalkerAdBean json2WallInfo(JSONObject wallObject,
			int page_type) {
		try {
			WalkerAdBean wallInfo = new WalkerAdBean();
			wallInfo.id = wallObject.getInt("id");
			wallInfo.adimage_url = wallObject.getString("adimage_url");
			wallInfo.adimage_width = wallObject.getInt("adimage_width");
			wallInfo.adimage_height = wallObject.getInt("adimage_height");
			wallInfo.ad_url = wallObject.getString("ad_url");
			wallInfo.ad_type = wallObject.getInt("ad_type");
			wallInfo.resourceSize = wallObject.getInt("resourceSize");
			wallInfo.title = wallObject.getString("title");
			wallInfo.resourceUrl = wallObject.getString("resourceUrl");
			wallInfo.fileName = wallObject.getString("fileName");
			wallInfo.packageName = wallObject.getString("packageName");
			wallInfo.page_type = wallObject.getInt("page_type");
			wallInfo.interval = wallObject.getInt("interval");
			JSONObject generalObject = wallObject.getJSONObject("general");
			wallInfo.generalInfo = new AdItemBean();
			wallInfo.generalInfo.wall_icon_Url = generalObject
					.getString("wall_icon_Url");
			return wallInfo;
			
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "frameInfo: " + e);
		}
		return null;
	}
}
