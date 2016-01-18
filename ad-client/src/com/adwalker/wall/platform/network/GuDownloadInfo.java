package com.adwalker.wall.platform.network;


import java.io.Serializable;


public class GuDownloadInfo implements Serializable ,Cloneable{

	private static final long serialVersionUID = -7173982657471215283L;
	/***/
	public long mId;
	/**下载的url*/
	public String mUrl;
	/**文件名*/
	public String mFileName;
	/**标题*/
	public String mName;
	/**目标目录*/
	public String mDestination;
	/**下载进度*/
	public double mPercentage;
	/**文件总大小*/
	public long mTotalBytes;
	/**当前下载了字节数*/
	public long mCurrentBytes;
//	/**上下文*/
//	public Context mContext;
	/**下载过程中的错误*/
	public Throwable exception;
	/**文件的大小*/
	public long resourceSize;
	//
	/**开始时间*/
	public long mStartTime;
	/**结束时间*/
	public long mFinishTime;
//	/**下载实时速率*/
//	public double mRate;
//	/**下载平均速率*/
//	public double mAverageRate;

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
