package com.adwalker.wall.platform.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import com.adwalker.wall.platform.bean.AdHintBean;
import com.adwalker.wall.platform.bean.AppPackageBean;

public class AdApkUtil {
	
	/**
	 * 获取手机可用空间
	 */
	public static long getAvailableSpace(Boolean isSD) {
		if (isSD) {
			StatFs statFs = new StatFs(Environment
					.getExternalStorageDirectory().getPath());
			long blocksize = statFs.getBlockSize();
			long availableblock = statFs.getAvailableBlocks() - 4;
			return (long) ((availableblock > 0 ? availableblock : 0) * 1.0
					* blocksize * 1.0);
		} else {
			StatFs statFs_data = new StatFs(Environment.getDataDirectory()
					.getPath());
			long blocksize = statFs_data.getBlockSize();
			// 获取可供程序使用的Block的数量
			long availableblock = statFs_data.getAvailableBlocks() - 4;
			return (long) ((availableblock > 0 ? availableblock : 0) * 1.0
					* blocksize * 1.0);
		}
	}

	/**
	 * 格式转换百分比 单位"%"
	 */
	public static String getPercentage(double percentage) {
		return new DecimalFormat("0.0").format(percentage * 100) + "%"
				+ AdHintBean.notify_cancel_downloading;
	}
	
	/**
	 * 安装APK文件
	 */
	public static boolean installApk(Context context, String locationPath) {
		File file = new File(locationPath);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		boolean flag = true;
		if (file.exists()) {
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 删除APK文件
	 */
	public static boolean deleteApkFile(String locationPath) {
		File file = new File(locationPath);
		return file.delete();
	}

	
	/**
	 * 获取未存储的非系统应用
	 */
	public static List<AppPackageBean> NeedSaveInstalledApk(Context context) {
		List<AppPackageBean> infoList = getAllInstalledApkList(context);
		List<AppPackageBean> saveList = new ArrayList<AppPackageBean>();
		AdDbBase dbHelper = new AdDbBase(context);
		dbHelper.mdb = dbHelper.getWritableDatabase();
		if (infoList != null && infoList.size() > 0) {
			for (int i = 0; i < infoList.size(); i++) {
				AppPackageBean info = infoList.get(i);
				String appName = info.appName;
				String packageName = info.packageName;
				boolean isNeedSave = AdDbBase.needSave(dbHelper,
						appName, packageName);
				if (isNeedSave) {
					saveList.add(info);
				}
			}
		}
		dbHelper.mdb.close();
		dbHelper.mdb= null;
		dbHelper.close();
		return saveList;
	}

	/**
	 * 获取非系统应用的JSON串
	 */
	public static String NeedSaveInstalledApkToJson(
			List<AppPackageBean> saveList) {
		if (saveList != null && saveList.size() > 0) {
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = null;
			try {
				for (int i = 0; i < saveList.size(); i++) {
					AppPackageBean info = saveList.get(i);
					jsonObject = new JSONObject();
					jsonObject.put("appName", info.appName);
					jsonObject.put("packageName", info.packageName);
					jsonArray.put(jsonObject);
				}
				return jsonArray.toString();
			} catch (JSONException e) {
				GuLogUtil.e(AdConstants.LOG_ERR,
						"apktojson: " + e);
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * 获取已安装应用信息列表
	 */
	private static List<AppPackageBean> getAllInstalledApkList(Context context) {
		List<AppPackageBean> appPackageInfos = new ArrayList<AppPackageBean>();
		try {
			PackageManager pManager = context.getPackageManager();
			List<PackageInfo> appList = getAllApkList(context);
			AppPackageBean appPackageInfo = null;
			PackageInfo packageInfo = null;
			for (int i = 0; i < appList.size(); i++) {
				appPackageInfo = new AppPackageBean();
				packageInfo = appList.get(i);
				appPackageInfo.packageName = packageInfo.packageName;
				appPackageInfo.appVersionName = packageInfo.versionName;
				appPackageInfo.appVersionCode = packageInfo.versionCode;
				appPackageInfo.appName = pManager.getApplicationLabel(
						packageInfo.applicationInfo).toString();
				appPackageInfos.add(appPackageInfo);
			}
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "applist: " + e);
		}
		return appPackageInfos;
	}

	/**
	 * 获取手机内非系统应用
	 */
	private static List<PackageInfo> getAllApkList(Context context) {
		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo = null;
		// 获取手机内所有应用
		List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
		for (int i = 0; i < packageInfoList.size(); i++) {
			packageInfo = packageInfoList.get(i);
			// 判断是否为非系统预装的应用程序
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
				// 去除非本资源包的安装包
				if (!packageInfo.packageName.equalsIgnoreCase(context
						.getPackageName())) {
					apps.add(packageInfo);
				}
			}
		}
		return apps;
	}
	
	
	/**
	 * APK程序是否已安装
	 */
	public static boolean isInstalled(Context context, String packageName) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo packageInfo = pm.getPackageInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			if (packageInfo != null) {
				return true;
			}
		} catch (Exception e) {
//			LogUtil.error(Constants.LOG_ERR_TAG,
//					"isInstalled: " + e);
		}
		return false;
	}

	/**
	 * 打开应用
	 */
	public static boolean openPackage(Context context, String packageName) {
		try {
			Intent intent = new Intent();
			intent = context.getPackageManager().getLaunchIntentForPackage(
					packageName);
			if (intent != null) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				return true;
			}
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "openPackage: " + e);
		}
		return false;
	}
	
	
}
