package com.adwalker.wall.platform;

import android.content.Context;
import com.adwalker.wall.init.AdInitialization;
import com.adwalker.wall.init.AdWalkerListener;

public class AdWalker {
	
	public static AdWalker instance = null;
	private AdWalkerListener adWalkerListener = null;
	
	private AdWalker() {
	}

	/**
	 * 获取初始化实例
	 */
	public static AdWalker instance() {
		if (instance == null) {
			instance = new AdWalker();
		}
		return instance;
	}
	
	public static AdWalker instance(AdWalkerListener adWalkerListener) {
		if (instance == null) {
			instance = new AdWalker();
		}
		instance.setAdWalkerListener(adWalkerListener);
		return instance;
	}
	

	private void setAdWalkerListener(AdWalkerListener adWalkerListener) {
		this.adWalkerListener = adWalkerListener;
	}
		
	public AdWalkerListener getAdWalkerListener() {
		return adWalkerListener;
	}

	/**
	 * @param context 应用上下文
	 * @param appKey:应用标示
	 * @param appChannel
	 */
	public void init(Context context,String appKey,String appChannel) {
		AdInitialization.getInstance().init(appKey, appChannel, context, adWalkerListener);
	}	
	
	/**
	 * 关闭应用 释放内存资源
	 */
	public void release() {
		AdInitialization.getInstance().inRelease();
	}
}
