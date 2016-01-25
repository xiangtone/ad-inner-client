package com.adwalker.wall.platform.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.adwalker.wall.platform.bean.WalkerDetailBean;

/**
 * 工具类，包括加密等工具类
 */
public class GuUtil {
	/**
	 * BYTE[]转化为JSON
	 */
	public static JSONObject Bytes2Json(byte[] bytes) {
		if (bytes != null) {
			String data = new String(bytes);
			JSONObject json = null;
			try {
				json = new JSONObject(data);
			} catch (JSONException e) {
				GuLogUtil.e(AdConstants.LOG_ERR, "jsonOBJ: " + e);
			}
			return json;
		}
		return null;
	}
	
	/**
	 * JSON转换为DetailInfo
	 */
	public static WalkerDetailBean json2DetailInfo(JSONObject detailObject) {
		try {
			WalkerDetailBean detailInfo = new WalkerDetailBean();
			detailInfo.detail_icon_Url = detailObject
					.getString("detail_icon_Url");
			detailInfo.detail_first = detailObject.getString("detail_first");
			detailInfo.detail_second = detailObject.getString("detail_second");
			detailInfo.detail_third = detailObject.getString("detail_third");
			detailInfo.detail_fourth = detailObject.getString("detail_fourth");
//			detailInfo.detail_fifth = detailObject.getString("detail_fifth");
			detailInfo.detail_sixth = detailObject.getString("detail_sixth");
			detailInfo.isDownload = detailObject.getInt("isDownload");
			detailInfo.catagoryName = detailObject.getString("category_name");
			detailInfo.detail_seventh = detailObject
					.getString("detail_seventh");
			JSONArray detail_pictureVo = detailObject
					.getJSONArray("adDetailPicture");
			if (detail_pictureVo != null && detail_pictureVo.length() > 0) {
				detailInfo.detail_picturesUrl = new ArrayList<String>();
				for (int j = 0; j < detail_pictureVo.length(); j++) {
					detailInfo.detail_picturesUrl.add(detail_pictureVo
							.getJSONObject(j).getString("detail_picture_Url"));
				}
			}
			return detailInfo;
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "json: " + e);
		}
		return null;
	}
	
	
	public static String replace(String source, String oldString,
            String newString) {
		StringBuffer output = new StringBuffer();
		int lengthOfSource = source.length();
		int lengthOfOld = oldString.length();
		int posStart = 0;
		int pos; //
		while ( (pos = source.indexOf(oldString, posStart)) >= 0) {
		output.append(source.substring(posStart, pos));
		output.append(newString);
		posStart = pos + lengthOfOld;
		}
		if (posStart < lengthOfSource) {
		output.append(source.substring(posStart));
		}
		return output.toString();
	}
	
	/**
	 * 
	 * @param str
	 * @return 为空的判断 true为空，fase非空
	 */
	public static boolean eqString(String str) {
		if(str == null||str.equals("")){
			return true;
		}else{
			return false;
		}
	}
}
