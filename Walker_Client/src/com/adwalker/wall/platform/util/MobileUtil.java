package com.adwalker.wall.platform.util;

import java.net.URLEncoder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.adwalker.wall.platform.bean.MobileBean;


public class MobileUtil {


	public static void init(Context context) {
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
			TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
			WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics metrics = new DisplayMetrics();
			windowManager.getDefaultDisplay().getMetrics(metrics);
			MobileBean.IMEI = telephonyManager.getDeviceId();
			MobileBean.IMSI =telephonyManager.getSubscriberId();
			MobileBean.SCALE = metrics.density;
			MobileBean.FONTSCALE = metrics.scaledDensity;
			
			if(Math.round(metrics.widthPixels)> Math.round(metrics.heightPixels)){
				MobileBean.SCREEN_WIDTH = Math.round(metrics.heightPixels);
				MobileBean.SCREEN_HEIGHT = Math.round(metrics.widthPixels);
			}else{
				MobileBean.SCREEN_WIDTH = Math.round(metrics.widthPixels);
				MobileBean.SCREEN_HEIGHT = Math.round(metrics.heightPixels);
			}
			
			SharedPreferences sp = context.getSharedPreferences(AdConstants.SHARED, Context.MODE_PRIVATE);
			sp.edit().putInt("SCREEN_WIDTH",MobileBean.SCREEN_WIDTH).commit();
			sp.edit().putInt("SCREEN_HEIGHT",MobileBean.SCREEN_HEIGHT).commit();
			sp.edit().putFloat("SCALE",metrics.density).commit();
			sp.edit().putFloat("FONTSCALE",metrics.scaledDensity).commit();
			sp.edit().putString("IMSI", MobileBean.IMSI).commit();
			sp.edit().putString("IMEI", MobileBean.IMEI).commit();
			
			MobileBean.APP_KEY = sp.getString("APP_KEY", "");
			//从mainifest中获取配置
			Bundle bundle = ai.metaData;
			if (bundle != null) {
				try{
					MobileBean.APP_CHANNEL = bundle.get("XY_APP_CHANNEL").toString();
					sp.edit().putString("APP_CHANNEL", MobileBean.APP_CHANNEL).commit();
				}catch (Exception e) {
					MobileBean.APP_CHANNEL = sp.getString("APP_CHANNEL", "");
				}
			}else{
				MobileBean.APP_CHANNEL = sp.getString("APP_CHANNEL", "");
			}
			
		} catch (NameNotFoundException e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "data: " + e);
		} catch (NullPointerException e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "image: " + e);
		}
	}
	
	// 获取注册信息
	public static String getRegisteredCode(Context context) {
		String uuid = getMobileId(context);
		if (!uuid.equals("")) {
			return "uuid=" + uuid;
		} else {
			TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
			return "imei=" + getImei(context) + "&telModel="
					+ getTelModel() + "&netEnv=" + getNetEnv(context)
					+ "&areaCode=" + getImsi(context) + "&telNum="
					+ "&operator="+ getOperator(telephonyManager) + "&os=" + getOs()
					+ "&brand=" + getBrand() + "&sw=" + getSCREEN_WIDTH(context) + "&sh="
					+ getSCREEN_HEIGHT(context) + "&uuid=" + uuid;
		}
	}
	
	/**
	 * 检测网络是否可用
	 */
	public static boolean checkNetWork(Context context) {
		ConnectivityManager cwjManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if ((info == null) || info.isAvailable() == false) {
			return false;
		}
		return true;
	}
	
	/**
	 * 当前是否正在使用wap连接网络
	 */
	public static boolean isUsingCmwap(Context context) {
		boolean result = false;
		ConnectivityManager cwjManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null
				&& info.getTypeName().equalsIgnoreCase("MOBILE")
				&& (info.getExtraInfo().contains("cmwap") || info
						.getExtraInfo().contains("CMWAP"))) {
			return true;
		}
		return result;
	}
	
	// 获取缓存数据
	public static Float getScale(Context context) {
		if(MobileBean.SCALE==null){
			SharedPreferences sp = context.getSharedPreferences(AdConstants.SHARED,
					Context.MODE_PRIVATE);
			return sp.getFloat("SCALE", 0);
		}
		return MobileBean.SCALE;
	
	}
	// 获取缓存数据
	public static Float getFontScale(Context context) {
		if(MobileBean.FONTSCALE==null){
			SharedPreferences sp = context.getSharedPreferences(AdConstants.SHARED,
					Context.MODE_PRIVATE);
			return sp.getFloat("FONTSCALE", 0);
		}
		return MobileBean.FONTSCALE;
		
	}
	// 获取屏幕宽度
	public static Integer getSCREEN_WIDTH(Context context) {
		
		if(MobileBean.SCREEN_WIDTH==null){
			SharedPreferences sp = context.getSharedPreferences(AdConstants.SHARED,
					Context.MODE_PRIVATE);
			return sp.getInt("SCREEN_WIDTH", 0);
		}
		return MobileBean.SCREEN_WIDTH;
		
	}
	// 获取屏幕高度
	public static Integer getSCREEN_HEIGHT(Context context) {
		if(MobileBean.SCREEN_HEIGHT==null){
			SharedPreferences sp = context.getSharedPreferences(AdConstants.SHARED,
					Context.MODE_PRIVATE);
			return sp.getInt("SCREEN_HEIGHT", 0);
		}
		return MobileBean.SCREEN_HEIGHT;
	}
	
    
	// 获取APP_KEY
	public static String getAPP_KEY(Context context) {
		if(MobileBean.APP_KEY==""){
		SharedPreferences sp = context.getSharedPreferences(AdConstants.SHARED,
				Context.MODE_PRIVATE);
		return sp.getString("APP_KEY", "");
		}
		return MobileBean.APP_KEY;
		
	}
	// 获取APP_CHANNEL
	public static String getAPP_CHANNEL(Context context) {
		if(MobileBean.APP_CHANNEL==""){
			SharedPreferences sp = context.getSharedPreferences(AdConstants.SHARED,
					Context.MODE_PRIVATE);
			return sp.getString("APP_CHANNEL", "");
		}
	return MobileBean.APP_CHANNEL;
	}
	
	// 获取IMEI
	private static String getImei(Context context) {
		if(MobileBean.IMEI==""){
			SharedPreferences sp = context.getSharedPreferences(AdConstants.SHARED,
					Context.MODE_PRIVATE);
			return sp.getString("IMEI", "");
		}
	return MobileBean.IMEI;
	
	}
		
	
	// 获取IMSI
	public static String getImsi(Context context) {
		if(MobileBean.IMSI==""){
			SharedPreferences sp = context.getSharedPreferences(AdConstants.SHARED,
					Context.MODE_PRIVATE);
			return sp.getString("IMSI", "");
		}
		
	return MobileBean.IMSI;
	
	}
	
	// 获取MAC地址
	public static String getMac(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.getConnectionInfo().getMacAddress();
	}

	// 获取UUID
	public static String getMobileId(Context context) {
		SharedPreferences sp = context.getSharedPreferences(AdConstants.SHARED,
				Context.MODE_PRIVATE);
		return sp.getString("uuid", "");
	}

	// 获取手机型号
	@SuppressLint("DefaultLocale")
	private static String getTelModel() {
		return android.os.Build.MODEL.toLowerCase().replace(" ", "");
	}

	// 获取Mobile网络下的cmwap、cmnet网络环境
	private static String getNetEnv(Context context) {
		String type = "NONET";
		try{
			Cursor cursor = context.getContentResolver().query(
					AdConstants.PREFERRED_APN_URI,
					new String[] { "_id", "apn", "type" }, null, null, null);
			cursor.moveToFirst();
			int counts = cursor.getCount();
			if (counts != 0) {// 适配平板外挂3G模块情况
				if (!cursor.isAfterLast()) {
					String apn = cursor.getString(1);
					// #777、ctnet 都是中国电信定制机接入点名称,中国电信的接入点：Net、Wap都采用Net即非代理方式联网即可
					// internet 是模拟器上模拟接入点名称
					if (apn.equalsIgnoreCase("cmnet")
							|| apn.equalsIgnoreCase("3gnet")
							|| apn.equalsIgnoreCase("uninet")
							|| apn.equalsIgnoreCase("#777")
							|| apn.equalsIgnoreCase("ctnet")
							|| apn.equalsIgnoreCase("internet")) {
						type = "WIFIAndCMNET";
					} else if (apn.equalsIgnoreCase("cmwap")
							|| apn.equalsIgnoreCase("3gwap")
							|| apn.equalsIgnoreCase("uniwap")) {
						type = "CMWAP";
					}
				} else {
					// 适配中国电信定制机,如海信EG968,上面方式获取的cursor为空，所以换种方式
					Cursor c = context.getContentResolver().query(
							AdConstants.PREFERRED_APN_URI, null, null, null, null);
					c.moveToFirst();
					String user = c.getString(c.getColumnIndex("user"));
					if (user.equalsIgnoreCase("ctnet")) {
						type = "WIFIAndCMNET";
					}
					c.close();
				}
			} else {
				type = "WIFIAndCMNET";// 平板外挂3G,采用非代理方式上网
			}
			cursor.close();
		}catch(Exception e) {
			
		};
		
		return type;
	}

	// 获取手机卡类型，移动、联通、电信
	private static String getOperator(TelephonyManager telephonyManager) {
		String type = "";
		String iNumeric = telephonyManager.getSimOperator();
		if (iNumeric.length() > 0) {
			if (iNumeric.equals("46000") || iNumeric.equals("46002")) {
				// 中国移动
				type = "China Mobile";// CMCC
			} else if (iNumeric.equals("46001")) {
				// 中国联通
				type = "China Unicom";// CU
			} else if (iNumeric.equals("46003")) {
				// 中国电信
				type = "China Telecom";// CT
			}
		}
		return type;
	}

	// 获取操作系统版本号
	public static String getOs() {
		return "Android" + android.os.Build.VERSION.RELEASE;
	}

	// 获取手机机型
	@SuppressWarnings("deprecation")
	private static String getBrand() {
		
		return URLEncoder.encode(android.os.Build.MANUFACTURER == null
						|| android.os.Build.MANUFACTURER.equals("") ? android.os.Build.BRAND
						.toLowerCase() : android.os.Build.MANUFACTURER
						.toLowerCase());
	}

	/**
	 * dip转换为px
	 */
	public static int dip2px(Context context, float dipValue) {
		return (int) (dipValue * getScale(context) + 0.5f);
	}

	/**
	 * px转换为dip
	 */
	public static int px2dip(Context context, float pxValue) {
		return (int) (pxValue / getScale(context) + 0.5f);
	}	
}
