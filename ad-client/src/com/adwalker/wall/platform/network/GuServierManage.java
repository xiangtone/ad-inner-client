package com.adwalker.wall.platform.network;

import org.json.JSONObject;

import android.content.Context;
import com.adwalker.wall.platform.AdConstants;
import com.adwalker.wall.platform.bean.WalkerDetailBean;
import com.adwalker.wall.platform.util.GuLogUtil;
import com.adwalker.wall.platform.util.MobileUtil;
import com.adwalker.wall.platform.util.GuUtil;

public class GuServierManage {

	public static void actionLogFromServer(Context context, int action,
			int ad_id, int page_type, int bannerTag,String ids) {
		if (!MobileUtil.checkNetWork(context)) {
			return;
		}
		String codeStr = "uuid=" +MobileUtil.getMobileId(context) + "&ac=" + action + "&id=" + ad_id
		+ "&page_type=" + page_type + "&bannerTag=" + bannerTag + "&ids=" + ids;
		
		String serverUrl = AdConstants.SERVER_ACTIONLOG;
		
		GuHttpNetwork.statisticsFromServer(context, serverUrl, codeStr);
	}
		
	public static WalkerDetailBean detailFromServer(Context context, int ad_id) {
		if (!MobileUtil.checkNetWork(context)) {
			return null;
		}
		String codeStr = "uuid="+MobileUtil.getMobileId(context) + "&adId=" + ad_id;
		String serverUrl = AdConstants.SERVER_DETAILS;

		byte[] bytes = GuHttpNetwork.dataFromServer(context, serverUrl,
				codeStr);
		if (bytes == null) {
			return null;
		}
		try {
			JSONObject returnJson = GuUtil.Bytes2Json(bytes);

			GuLogUtil.i(AdConstants.LOG_TAG, "detailFromServer returnJson: "
					+ returnJson.toString());

			String status = returnJson.getString("status");
			JSONObject data = returnJson.getJSONObject("data");
			if (data != null) {
				if (status.equalsIgnoreCase("ok")) {
					JSONObject detailObject = data.getJSONObject("adDetail");
					if (detailObject != null) {
						WalkerDetailBean detailInfo = GuUtil
								.json2DetailInfo(detailObject);
//						if (!Constants.DRAWABLE__DETAIL_IS_DOWNLOAD) {
//							Constants.DRAWABLE_DETAIL = DrawableUtil
//									.getDetailsDrawable(context);
//						}
						return detailInfo;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			GuLogUtil.e(AdConstants.LOG_ERR, "detailServerErr: " + e);
		}
		return null;
	}
}
