package com.adwalker.wall.banner;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

import com.adwalker.wall.init.AdInitialization;
import com.adwalker.wall.platform.bean.AdItemBean;
import com.adwalker.wall.platform.bean.WalkerAdBean;
import com.adwalker.wall.platform.network.GuHttpNetwork;
import com.adwalker.wall.platform.network.GuNotifyManage.NotifyTask;
import com.adwalker.wall.platform.util.AdApkUtil;
import com.adwalker.wall.platform.util.GuLogUtil;
import com.adwalker.wall.platform.util.MobileUtil;
import com.adwalker.wall.platform.util.GuUtil;

public class BannerDataUtil {
	
	private static String topActivityName = "";
	/**
	 * 检测某ActivityUpdate是否在当前Task的栈顶
	 */
	public static boolean isTopActivy(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		String cmpNameTemp = null;
		if (null != runningTaskInfos) {
			cmpNameTemp = (runningTaskInfos.get(0).topActivity).toString();
		}
		if (null == cmpNameTemp)
			return false;
		if (topActivityName.equals("")) {
			SharedPreferences sp = context.getSharedPreferences(
					AdConstants.SHARED, Context.MODE_PRIVATE);
			topActivityName = sp.getString("topActivityName", "");
		}
		return cmpNameTemp.equals(topActivityName);
	}

	/**
	 *保存记录
	 */
	public static void saveTopActivy(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		String cmpNameTemp = null;
		if (null != runningTaskInfos) {
			cmpNameTemp = (runningTaskInfos.get(0).topActivity).toString();
		}
		if (null == cmpNameTemp)
			return;
		if (topActivityName.equals("")) {
			SharedPreferences sp = context.getSharedPreferences(
					AdConstants.SHARED, Context.MODE_PRIVATE);
			String cmdNameTag = sp.getString("topActivityName", "");
			if ("".equals(cmdNameTag)) {
				topActivityName = cmpNameTemp;
				sp.edit().putString("topActivityName", topActivityName).commit();
			} else {
				topActivityName = cmdNameTag;
			}
		}
	}
	

public static ImageView getnewImageView(Context context) {
		ImageView pobImage = new ImageView(context);
		RelativeLayout.LayoutParams bannerImageParams = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, MobileUtil.dip2px(context,
						50));
		pobImage.setLayoutParams(bannerImageParams);
		pobImage.setBackgroundColor(Color.rgb(59, 62, 71));
		pobImage.setScaleType(ScaleType.FIT_XY);
		pobImage.getBackground().setAlpha(150);// 0~255透明度值
		return pobImage;
	}
	
	//请求banner数据
	public static List<WalkerAdBean> adListFromServer(Context context,
			int page_number, int page_type, int image_type) {
		String codeStr = "uuid="+ MobileUtil.getMobileId(context) + "&pageNo=" + page_number
				+ "&pageSize=" + AdConstants.ADLIST_PAGE_SIZE + "&page_type="
				+ page_type + "&image_type=" + image_type;
		String serverUrl = AdConstants.SERVER_ADLIST;
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
									NotifyTask notifyThread = AdInitialization.notifyList.get(String.valueOf(wallInfo.id));
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
			GuLogUtil.e(AdConstants.LOG_ERR, "bannerError: " + e);
		}
		return null;
	}

	
	private static WalkerAdBean json2WallInfo(JSONObject wallObject,
			int page_type) {
		try {
			WalkerAdBean wallInfo = new WalkerAdBean();
			wallInfo.id = wallObject.getInt("id");
			wallInfo.resourceSize = wallObject.getInt("resourceSize");
			wallInfo.title = wallObject.getString("title");
			wallInfo.resourceUrl = wallObject.getString("resourceUrl");
			wallInfo.fileName = wallObject.getString("fileName");
			wallInfo.packageName = wallObject.getString("packageName");
			wallInfo.page_type = wallObject.getInt("page_type");
			wallInfo.interval = wallObject.getInt("interval");
			wallInfo.adimage_url = wallObject.getString("adimage_url");
			wallInfo.adimage_width = wallObject.getInt("adimage_width");
			wallInfo.adimage_height = wallObject.getInt("adimage_height");
			wallInfo.ad_url = wallObject.getString("ad_url");
			wallInfo.ad_type = wallObject.getInt("ad_type");
			JSONObject generalObject = wallObject.getJSONObject("general");
			wallInfo.generalInfo = new AdItemBean();
			wallInfo.generalInfo.wall_icon_Url = generalObject
					.getString("wall_icon_Url");
			return wallInfo;
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "Bannerjson: " + e);
		}
		return null;
	}

}
