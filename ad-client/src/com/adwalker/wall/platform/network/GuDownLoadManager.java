package com.adwalker.wall.platform.network;


import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.adwalker.wall.platform.AdConstants;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class GuDownLoadManager extends Observable {

	private static final GuDownLoadManager downLoadManager = new GuDownLoadManager();
	protected GuDownloadInterface impl;
	private ConcurrentMap<Long, GuDownLoadTask> concurrentMap =new ConcurrentHashMap<Long, GuDownLoadTask>();
	public static GuDownLoadManager getInstance(){
		return downLoadManager;
	}
	
	public boolean addDownLoadTask(GuDownloadInfo task,Context context,String indexValue,String catagory,Handler handler){
		if(impl == null){
			downLoadManager.setAppApiInterface(GuDownloadImpl.getInstance());
		}
		if (task!=null) {
			setChanged();
		}
		if(concurrentMap.size()<AdConstants.DOWNLOAD_MAX){
			concurrentMap.put(task.mId, new GuDownLoadTask(task,context,indexValue,catagory,handler));
			notifyObservers(task);
			return true;
		}else{
			Toast.makeText(context, "不好意思，最多同时只能下载2个咯！", Toast.LENGTH_SHORT).show();
			Message msg = new Message();
			try{
				msg.arg1 = Integer.parseInt(indexValue);
				msg.arg2 = Integer.parseInt(catagory);
			}catch(Exception e){
				msg.arg1 = 0;
				msg.arg2 = 0;
			}
			msg.what = 11;
			handler.sendMessage(msg);
		}
		return false;
	}
	
	
	/**
	 * 添加下载任务
	 * @param task
	 * @param context
	 * @return
	 */
	public boolean addDownLoadTask(GuDownloadInfo task,Context context){
		if(impl == null){
			downLoadManager.setAppApiInterface(GuDownloadImpl.getInstance());
		}
		if (task!=null) {
			setChanged();
		}
		if(concurrentMap.size()<AdConstants.DOWNLOAD_MAX){
			concurrentMap.put(task.mId, new GuDownLoadTask(task,context));
			notifyObservers(task);
			return true;
		}else{
			Toast.makeText(context, "不好意思，最多同时只能下载2个咯！", Toast.LENGTH_SHORT).show();
		}
		return false;
	}
	public void delDownTask(GuDownloadInfo task){
		GuDownLoadTask downLoadTask = concurrentMap.get(task.mId);
		if (downLoadTask!=null) {
			downLoadTask.cancel(true);
		}
		concurrentMap.remove(task.mId);
	}
	public void setAppApiInterface(GuDownloadInterface impl){
		this.impl = impl;
	}
	public void recycleAllTask(){
		Set<Entry<Long, GuDownLoadTask>> entrySet = concurrentMap.entrySet();
		for (Entry<Long, GuDownLoadTask> entry : entrySet) {
			entry.getValue().cancel(true);
		}
	}
}
