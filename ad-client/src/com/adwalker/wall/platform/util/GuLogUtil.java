package com.adwalker.wall.platform.util;

import com.adwalker.wall.demo.BuildConfig;

import android.util.Log;


public class GuLogUtil {
	private static final boolean DEBUG = BuildConfig.DEBUG;
	public static final int LOG_LEVEL = 0;
	public static void i(String tag,String msg){
		if (LOG_LEVEL<3&&DEBUG) {
			Log.i(tag, msg);
		}
	}
	public static void i(String tag,String msg,Throwable tr){
		if (LOG_LEVEL<3&&DEBUG) {
			Log.i(tag, msg,tr);
		}
	}
	public static void w(String tag,String msg){
		if (LOG_LEVEL<4&&DEBUG) {
			Log.w(tag, msg);
		}
	}
	public static void w(String tag,String msg,Throwable tr){
		if (LOG_LEVEL<4&&DEBUG) {
			Log.w(tag, msg,tr);
		}
	}
	public static void e(String tag,String msg){
		if (LOG_LEVEL<5&&DEBUG) {
			Log.e(tag, msg);
		}
	}
	public static void e(String tag,String msg,Throwable tr){
		if (LOG_LEVEL<5&&DEBUG) {
			Log.e(tag, msg,tr);
		}
	}
	public static void v(String tag,String msg){
		if (LOG_LEVEL<1&&DEBUG) {
			Log.v(tag, msg);
		}
	}
	public static void v(String tag,String msg,Throwable tr){
		if (LOG_LEVEL<1&&DEBUG) {
			Log.v(tag, msg,tr);
		}
	}
//	public static void d(String tag,String msg){
//		if (LOG_LEVEL<2&&DEBUG) {
//			Log.d(tag, msg);
//		}
//	}
//	public static void d(String tag,String msg,Throwable tr){
//		if (LOG_LEVEL<2&&DEBUG) {
//		Log.d(tag, msg,tr);
//		}
//	}
}
