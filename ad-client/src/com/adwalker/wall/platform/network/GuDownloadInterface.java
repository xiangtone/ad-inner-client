package com.adwalker.wall.platform.network;


public interface GuDownloadInterface {



	public abstract void notifyDownloadFinish(GuDownloadInfo downloadinfo);

	public abstract void notifyDownloadError(GuDownloadInfo downloadinfo);
	
}