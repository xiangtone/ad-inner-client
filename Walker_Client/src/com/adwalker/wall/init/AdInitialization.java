package com.adwalker.wall.init;

import java.util.Hashtable;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import com.adwalker.wall.platform.AdConstants;
import com.adwalker.wall.platform.bean.AppPackageBean;
import com.adwalker.wall.platform.bean.AdHintBean;
import com.adwalker.wall.platform.network.GuNotifyManage;
import com.adwalker.wall.platform.network.GuNotifyManage.NotifyTask;
import com.adwalker.wall.platform.util.AdApkUtil;
import com.adwalker.wall.platform.util.GuLogUtil;
import com.adwalker.wall.platform.util.GuUtil;
import com.adwalker.wall.platform.util.MobileUtil;

public class AdInitialization {
	private Context context;
	private boolean isInitalize;
	private AdWalkerListener adListener = null;
	private static boolean registFlag=false;
	private static AdInitialization instance = null;
	public static Hashtable<String, NotifyTask> notifyList = null;
	
	private AdInitialization() {
		super();
	}

	public static AdInitialization getInstance() {
		if (instance == null) {
			instance = new AdInitialization();
		}
		return instance;
	}

	/**
	 * 初始化用户
	 */
	public void init(String appKey,String appChannel,
			final Context context,final AdWalkerListener adWalkerListener) {
		if(!GuUtil.eqString(appKey)){//防止参数为空，改变缓存数据
			SharedPreferences sp = context.getSharedPreferences(AdConstants.SHARED, Context.MODE_PRIVATE);
			sp.edit().putString("APP_KEY", appKey).commit();
			sp.edit().putString("APP_CHANNEL", appChannel).commit();
		}
		if(notifyList == null){
			MobileUtil.init(context);
			notifyList = new Hashtable<String, NotifyTask>();
		}
		setRecevier(context,adWalkerListener);
		
		isInitalize = true;
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				mobileInitalize();
			}
		}).start();
		
	}

	private void mobileInitalize() {
		int count = 1;
		while (isInitalize) {
			AdInitModel initM = AdInitModel.getInstance();
			initM.registerListener(adListener);
			try {
				if (!initM.initializeFromServer(context)) {//初始化失败
					count--;
					if (count > 0) {
						Thread.sleep(AdConstants.NETWORK_INTERVAL);
					} else {
						isInitalize = false;
						if(adListener!=null){
							adListener.callFailed(AdConstants.NETWORK_ERR,AdHintBean.init_err);
						}
					}
				} else {//初始化成功
					isInitalize = false;
					List<AppPackageBean> saveList = AdApkUtil.NeedSaveInstalledApk(context);
					String saveString = AdApkUtil.NeedSaveInstalledApkToJson(saveList);
					//发送已安装列表
					if (saveString != null) {
						initM.softListFromServer(context,saveList, saveString);
					}
				}
			} catch (Exception e) {
				GuLogUtil.e(AdConstants.LOG_ERR, "init: "+ e);
			}
		}
	
	}

	public void setRecevier(Context context,AdWalkerListener adWalkerListener) {
		try {
			this.context = context;
			this.adListener = adWalkerListener;
			if(!registFlag){
				// 安装包事件
				IntentFilter installFilter = new IntentFilter();
				installFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
				installFilter.addDataScheme("package");
				context.getApplicationContext().registerReceiver(notifReceiver, installFilter);
				registFlag=true;
			}	
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "initRecevierErr: " + e.fillInStackTrace());
		}
	}
	
	public BroadcastReceiver notifReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			// 安装包事件
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
				String packageName = intent.getDataString().replace("package:","");
				GuNotifyManage.getInstance(null).receiveInstalledApp(context, packageName);
			}
		}
	};
	
	
	/**
	 * 释放....
	 */
	public void inRelease() {
		try {
			instance = null;
			isInitalize = false;
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "inRelease: " + e.getMessage());
		}
	}
}
