package com.adwalker.wall.platform.network;

import java.util.Enumeration;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.Toast;

import com.adwalker.wall.init.AdInitialization;
import com.adwalker.wall.platform.AdConstants;
import com.adwalker.wall.platform.bean.AdHintBean;
import com.adwalker.wall.platform.bean.WalkerAdBean;
import com.adwalker.wall.platform.util.AdApkUtil;
import com.adwalker.wall.platform.util.ImageLoadUtil;
import com.adwalker.wall.platform.util.MobileUtil;

public class GuNotifyManage {
	public Handler handler;
	public String indexValue;
	public String catagory;
	public static final GuNotifyManage instance = new GuNotifyManage();
	
	private GuNotifyManage() {
		
	}

	/**
	 * 获取SDK实例
	 */
	public static GuNotifyManage getInstance(Handler handler) {
		if(handler != null){	
			instance.setHandler(handler);
		}
		return instance;
	}
	
	/**
	 * 获取SDK实例
	 */
	public static GuNotifyManage getInstance(Handler handler,String indexValue) {
		if(handler != null){	
			instance.setHandler(handler);
		}
		instance.setIndexValue(indexValue);
		return instance;
	}
	
	/**
	 * 获取SDK实例
	 */
	public static GuNotifyManage getInstance(Handler handler,String indexValue,String catagory) {
		if(handler != null){	
			instance.setHandler(handler);
		}
		instance.setIndexValue(indexValue);
		instance.setCatagory(catagory);
		return instance;
	}
	
	private void setIndexValue(String indexValue){
		this.indexValue = indexValue;
	}
	
	private void setCatagory(String catagory){
		this.catagory = catagory;
	}
	
	private void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	public static class NotifyTask extends Thread {
		private Context context;
		public WalkerAdBean wallInfo;
		public boolean notifyRunningFlag = true;

		public NotifyTask(Context context, WalkerAdBean wallInfo) {
			this.context = context;
			this.wallInfo = wallInfo;
		}

		public void run() {
			try {
				if(wallInfo.page_type!=0||AdConstants.isSignIn==0){//签到不发送日志
					GuServierManage.actionLogFromServer(context,
						AdConstants.BUTTON_DOWM, wallInfo.id,wallInfo.page_type,wallInfo.bannerTag,""+wallInfo.id);
				}	
					Notification notification = new Notification();
					notification.icon = android.R.drawable.stat_sys_download;
					notification.flags = Notification.FLAG_AUTO_CANCEL;
					
					Intent intent = new Intent("com.adwalker.wall.NOTIFY_DOWNLOADING_CANCEL");//广播下载行为
					Bundle data = new Bundle();
					data.putInt(AdConstants.NOTIFY_DOWNLOADING_ID,Math.abs(wallInfo.id));
					intent.putExtras(data);
					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							context, wallInfo.id, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					
					
					Intent it = new Intent(AdConstants.NOTIFY_CLEAR_ACTION);//通知栏清除行为标识
					Bundle bundle = new Bundle();
					bundle.putString(AdConstants.NOTIFY_CLEAR,AdConstants.NOTIFY_CLEAR_ALL);
					bundle.putInt(AdConstants.NOTIFY_CLEAR_ID, wallInfo.id);
					it.putExtras(bundle);
					PendingIntent pendingIt = PendingIntent.getBroadcast(context, wallInfo.id, it,
							PendingIntent.FLAG_UPDATE_CURRENT);
					
					notification.deleteIntent = pendingIt;
					do {
						SystemClock.sleep(900);
						String tickerText = AdHintBean.notify_task_downloading
								+ AdApkUtil.getPercentage(wallInfo.downloadInfo.mPercentage);
						notification.setLatestEventInfo(context,wallInfo.title, tickerText, pendingIntent);
//						(NotificationManager);
						NotificationManager nfm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
						nfm.notify(Math.abs(wallInfo.id), notification);
						
					} while (notifyRunningFlag);
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!notifyRunningFlag) {
				
					NotificationManager nfm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
				
					nfm.cancel(Math.abs(wallInfo.id));
			}
		}
		public WalkerAdBean getWallInfo(){
			return wallInfo;
		}
		public void finish() {
			try {
				notifyRunningFlag = false;
				if (wallInfo.state != AdConstants.APP_DOWNLOADED) {
					wallInfo.state = AdConstants.APP_DOWNLOADED;
				}
				if (wallInfo.downloadInfo.mDestination.contains("/data/")) {
					String command2 = "chmod 775 "
							+ wallInfo.downloadInfo.mDestination;
					try {
						Runtime runtime = Runtime.getRuntime();
						runtime.exec(command2);
					} catch (Exception e) {
						e.printStackTrace();
					}
					command2 = "chmod 775 "
							+ wallInfo.downloadInfo.mDestination
							+ wallInfo.downloadInfo.mFileName;
					try {
						Runtime runtime = Runtime.getRuntime();
						runtime.exec(command2);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if(wallInfo.page_type!=0||AdConstants.isSignIn==0){//签到不发送日志
					new Thread(new Runnable() {
						@Override
						public void run() {
							GuServierManage.actionLogFromServer(context,
									AdConstants.ACTION_DOWNLOADED, wallInfo.id,
									wallInfo.page_type,wallInfo.bannerTag,""+wallInfo.id);
						}
					}).start();
				}
				installApk(context, wallInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * 取消下载
	 */
	public  void notifyCancle(final int id) {
		
		new Thread(new Runnable() {
			public void run() {
				try {
					NotifyTask thread = AdInitialization.notifyList.get(String.valueOf(id));
					if (thread != null) {
						thread.notifyRunningFlag = false;
						
						if (thread.wallInfo.state == AdConstants.APP_DOWNLOADED) {
							if (handler != null) {
								Message msg = Message.obtain();
								msg.what = AdConstants.APP_DOWNLOADED;
								Bundle data = new Bundle();
								data.putLong("mId", id);
								msg.setData(data);
								handler.sendMessage(msg);
							}
						}
						if (thread.wallInfo.state == AdConstants.APP_UNDO) {
							AdInitialization.notifyList.remove(String.valueOf(thread.wallInfo.id));
							deleteDownloadingTask(thread.wallInfo.downloadInfo);
							if (handler != null) {
								Message msg = Message.obtain();
								msg.what = AdConstants.APP_UNDO;
								Bundle data = new Bundle();
								data.putLong("mId", id);
								msg.setData(data);
								handler.sendMessage(msg);
							}
						}
						if (thread.wallInfo.state == AdConstants.APP_DOWNLOADING) {
							AdInitialization.notifyList.remove(String.valueOf(thread.wallInfo.id));
							deleteDownloadingTask(thread.wallInfo.downloadInfo);
							thread.wallInfo.state = AdConstants.APP_UNDO;
							if (handler != null) {
								Message msg = Message.obtain();
								msg.what = AdConstants.APP_UNDO;
								Bundle data = new Bundle();
								data.putLong("mId", id);
								msg.setData(data);
								handler.sendMessage(msg);
							}
						}
						AdInitialization.notifyList.remove(String.valueOf(id));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * 删除下载中任务
	 */
	public static void deleteDownloadingTask(GuDownloadInfo downloadInfo) {
		try {
			AdApkUtil.deleteApkFile(downloadInfo.mDestination + downloadInfo.mFileName);
			GuDownLoadManager.getInstance().delDownTask(downloadInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	/**
//	 * 消除状态栏通知
//	 */
//	public static void notifyClear() {
//		if (SdkModel.notifyList == null) {
//			return;
//		}
//		try {
//			Enumeration<String> en = SdkModel.notifyList.keys();
//			while (en.hasMoreElements()) {
//				SdkModel.notifyList.get(en.nextElement()).notifyRunningFlag = false;
//			}
//			Set<String> set = SdkModel.notifyList.keySet();
//			for (String s : set) {
//				SdkModel.nfm.cancel(Integer.parseInt(s));
//				SdkModel.nfm.cancel(-Integer.parseInt(s));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			SdkModel.notifyList.clear();
//		}
//	}
	
	/**
	 * 添加资源下载任务(下载)
	 */
	public  boolean checkDownloadTask(Context context, WalkerAdBean wallInfo) {
		switch (wallInfo.state) {
		case AdConstants.APP_DOWNLOADING://下载中
			if (handler != null) {
				Message msg = Message.obtain();
				msg.what = AdConstants.APP_DOWNLOADING;
				Bundle data = new Bundle();
				data.putLong("mId", wallInfo.id);
				msg.setData(data);
				handler.sendMessage(msg);
			}
			Toast.makeText(context, AdHintBean.app_downloading_tip,
					Toast.LENGTH_SHORT).show();
			return false;
		case AdConstants.APP_INSTALLED://已安装
			if (handler != null) {
				Message msg = Message.obtain();
				msg.what = AdConstants.APP_INSTALLED;
				Bundle data = new Bundle();
				data.putLong("mId", wallInfo.id);
				msg.setData(data);
				handler.sendMessage(msg);
			}
			openApk(context, wallInfo,0);
			return false;
		default:
			break;
		}
		return addDownloadTask(context,wallInfo2DownloadInfo(context, wallInfo));
	}
	
	/**
	 * 添加下载任务
	 */
	private boolean addDownloadTask(Context context, WalkerAdBean wallInfo) {
		try {
			if (wallInfo == null) {
				return false;
			}
			if (checkDownload(context, wallInfo)) {
				if (AdInitialization.notifyList.get(String.valueOf(wallInfo.id)) == null) {
					boolean add = GuDownLoadManager.getInstance().addDownLoadTask(wallInfo.downloadInfo,context);
					if(add){
						notifyDownload(context, wallInfo,false);
						wallInfo.state = AdConstants.APP_DOWNLOADING;
						Toast.makeText(context,wallInfo.title+AdHintBean.add_task_success,Toast.LENGTH_SHORT).show();
						if (handler != null) {
							Message msg = Message.obtain();
							msg.what = AdConstants.APP_DOWNLOADING;
							Bundle data = new Bundle();
							data.putLong("mId", wallInfo.id);
							msg.setData(data);
							handler.sendMessage(msg);
						}
					}
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * 实例化下载信息
	 */
	private static WalkerAdBean wallInfo2DownloadInfo(Context context,
			WalkerAdBean wallInfo) {
		wallInfo.downloadInfo = new GuDownloadInfo();
		wallInfo.downloadInfo.mId = wallInfo.id;
		wallInfo.downloadInfo.mUrl = wallInfo.resourceUrl;
		wallInfo.downloadInfo.mFileName = wallInfo.fileName;
		wallInfo.downloadInfo.mName = wallInfo.title;
		wallInfo.downloadInfo.mDestination = ImageLoadUtil
				.getDownloadDir(context);
		return wallInfo;
	}


	/**
	 * 获取当前下载位置
	 * @param wallInfos 
	 */
	public static WalkerAdBean getCurrentPos(long id, List<WalkerAdBean> wallInfos) {
		synchronized (wallInfos) {
			for (WalkerAdBean wallInfo : wallInfos)
				if (id == wallInfo.id) {
					return wallInfo;
				}
		}
		return null;
	}
	
	/**
	 * 添加下载任务
	 */
	public boolean h5AddDownloadTask(Context context, WalkerAdBean wallInfo) {
		try {
			if (wallInfo == null) {
				return false;
			}
			if (checkDownload(context, wallInfo)) {
				if (AdInitialization.notifyList.get(String.valueOf(wallInfo.id)) == null) {
					boolean add = GuDownLoadManager.getInstance().addDownLoadTask(wallInfo.downloadInfo,context,indexValue,catagory,handler);
					wallInfo.state = AdConstants.APP_DOWNLOADING;
					if(add){
							notifyDownload(context, wallInfo,false);
							Toast.makeText(context,wallInfo.title+AdHintBean.add_task_success,Toast.LENGTH_SHORT).show();
					}
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 下载UI事件
	 */
	public static void notifyDownload(Context context, WalkerAdBean wallInfo,boolean isDownloaded) {
		NotifyTask notify = new NotifyTask(context, wallInfo);
		AdInitialization.notifyList.put(String.valueOf(wallInfo.id), notify);
		if(!isDownloaded){
			notify.start();
		}
	}

	/**
	 * 检测下载任务
	 */
	private static boolean checkDownload(Context context, WalkerAdBean wallInfo) {
		// 网络
		if (MobileUtil.checkNetWork(context)) {
			// SD卡容量
			if (AdApkUtil.getAvailableSpace(ImageLoadUtil.hasSD()) > wallInfo.resourceSize) {
				return true;
			} else {
				Toast.makeText(context, AdHintBean.space_full,Toast.LENGTH_SHORT).show();
				return false;
			}
		} else {
			Toast.makeText(context, AdConstants.NETWORK_NONE, Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	/**
	 * 完成下载任务
	 */
	public  void finishedDownlaod(long id) {
		notifyDownloaded(String.valueOf(id));
	}

	/**
	 * 下载完成安装通知
	 */
	public static void notifyDownloaded(String key) {
		synchronized (AdInitialization.notifyList) {
			if (AdInitialization.notifyList != null && AdInitialization.notifyList.size() > 0) {
				AdInitialization.notifyList.get(key).finish();
			}
		}
	}

	/**
	 * 安装APK
	 */
	public static boolean installApk(Context context, WalkerAdBean wallInfo) {
		if (wallInfo.downloadInfo == null) {
			return false;
		}
		if (AdApkUtil.installApk(context, wallInfo.downloadInfo.mDestination
				+ wallInfo.downloadInfo.mFileName)) {
			return true;
		}
		return false;
	}

	/**
	 * 打开APK
	 */
	public static boolean openApk(final Context context, final WalkerAdBean wallInfo,int tag) {
		if (AdApkUtil.openPackage(context, wallInfo.packageName)) {
			wallInfo.state = AdConstants.APP_INSTALLED;
			if(tag==0){
				//签到功能
				if(wallInfo.isDownload!=0&&wallInfo.isDownload!=-1){
					if(wallInfo.page_type==0&&AdConstants.sign_status>wallInfo.isDownload-1){
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								GuScoreManage.getInstance().addScoreFromServer(context, wallInfo);
								wallInfo.isDownload = -1;
							}
						}).start();
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 成功激活增加积分
	 */
	public void receiveInstalledApp(final Context context,
			final String packageName) {
		new Thread(new Runnable() {
			public void run() {
				synchronized (AdInitialization.notifyList) {
					try {
						Enumeration<String> en = AdInitialization.notifyList.keys();
						int reInfoflag = 0;
						WalkerAdBean wallInfo = new WalkerAdBean();
						WalkerAdBean info = new WalkerAdBean();
						
						while (en.hasMoreElements()) {
							wallInfo = AdInitialization.notifyList.get(en.nextElement()).wallInfo;
							if (wallInfo.packageName
									.equalsIgnoreCase(packageName) && wallInfo.state == AdConstants.APP_DOWNLOADED) {
								info = wallInfo;
								reInfoflag++;
							}
						}
						if (reInfoflag > 0) {
							if (openApk(context, info,1)) {
								instance.notifyCancle(info.id);
								if(info.adWalkerListener != null){
									GuScoreManage.getInstance().registerListener(info.adWalkerListener);
								}
								if(info.page_type==0){
									//积分墙
									if(AdConstants.sign_status > (wallInfo.isDownload - 1) && wallInfo.isDownload != -1){
										GuScoreManage.getInstance().addScoreFromServer(context, info);
									}
								}else{
									if(wallInfo.isDownload!=-1){
										GuScoreManage.getInstance().addScoreFromServer(context, info);
									}
								}
								if (handler != null) {
									Message msg = new Message();
									msg.what = 10;
									if(indexValue != null && !indexValue.equals("")){
										msg.arg1 = Integer.parseInt(indexValue);
									}else{
										msg.arg1 = 0;
									}
									if(catagory != null && !catagory.equals("")){
										msg.arg2 = Integer.parseInt(catagory);
									}else{
										msg.arg2 = 0;
									}
									msg.obj = "签到";
									handler.sendMessage(msg);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
	}
}
