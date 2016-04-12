package com.onlinegame.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class ExtraData {

	public static String getChannel(Context context) {

		String channel = "";
		try {
			channel = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA).metaData
					.getString("OLSDK_CHANNEL").toString();

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return channel;

	}

	public static String getAppkey(Context context) {
		
		String appkey = "";
		try {
			appkey = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA).metaData
					.getString("OLSDK_APPKEY");

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return appkey;
		
	}
}
