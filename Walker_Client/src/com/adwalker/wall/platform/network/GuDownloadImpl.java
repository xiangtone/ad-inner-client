package com.adwalker.wall.platform.network;


/**
 * 应用程序接口AppApiInterface实现类，引擎调用该类中的方法与应用程序交互(如UI)
 */
public class GuDownloadImpl implements GuDownloadInterface {
	private static GuDownloadImpl instance = null;

	public static GuDownloadImpl getInstance() {
		if (instance == null) {
			instance = new GuDownloadImpl();
		}
		return instance;
	}


	/**
	 * 通知UI，一项下载任务完成
	 */
	@Override
	public void notifyDownloadFinish(GuDownloadInfo downloadInfo) {
		GuNotifyManage.getInstance(null).finishedDownlaod(downloadInfo.mId);
	}

	
	/**
	 * 通知UI，一项下载任务出错
	 */
	@Override
	public void notifyDownloadError(GuDownloadInfo downloadInfo) {
		//移除通知
		GuNotifyManage.getInstance(null).notifyCancle((int)downloadInfo.mId);
	}


}
