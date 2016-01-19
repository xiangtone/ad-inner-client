package com.adwalker.wall.frame;

import com.adwalker.wall.init.AdWalkerListener;

import android.content.Context;

public class AdFrameSDK {
	public final static int HORIZONTAL = 2;//图片-插屏横屏
	public final static int VERTICAL = 3;//图片-插屏竖屏
	
	//
	public final static int TIMEHORIZONTAL = 44;//图片-插屏横屏计时
	public final static int TIMEVERTICAL = 55;//图片-插屏竖屏计时
	
	public static AdFrameSDK instance = null;
	
	public AdFrameSDK() {
		
	}

	public static AdFrameSDK instance() {
		if (instance == null) {
			instance = new AdFrameSDK();
		}
		return instance;
	}
	
	
	/**
	 * 插屏预加载 
	 * frameFlag 2-横屏, 3-竖屏  
	 */
	
	public void initPopFrame(final Context context,final AdWalkerListener adWalkerListener,final int frameFlag) {
		
		AdFrameModel.checkpobFrame(context, frameFlag,adWalkerListener);
		
	}
	/**
	 * 显示插屏广告 view Activity中的view plaue_flag 2-横屏插屏广告 3-竖屏插屏广告 默认--竖屏插屏广告 
	 */
	public void showPopFrame(Context context,AdWalkerListener adWalkerListener,int plaue_flag) {
		
		AdFrameModel.showFrame(context, plaue_flag,adWalkerListener);
		
	}
}
