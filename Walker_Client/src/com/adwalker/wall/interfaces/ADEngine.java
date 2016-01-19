package com.adwalker.wall.interfaces;

import android.content.Context;
import android.content.res.Configuration;

import com.adwalker.wall.demo.MainActivity;
import com.adwalker.wall.frame.AdFrameSDK;
import com.adwalker.wall.init.AdWalkerListener;
import com.adwalker.wall.platform.AdWalker;

public class ADEngine implements AdwalkerInterface {

	Context context;

	public ADEngine(Context context) {
		super();
		this.context = context;
	}

	public static ADEngine getInstance(Context context) {
		ADEngine adEngine = null;
		if (adEngine == null) {
			adEngine = new ADEngine(context);
			return adEngine;
		}
		return null;
	}

	@Override
	public void insertAD() {
		
		
		System.out.println("插屏广告执行成功");
		String key = "AWC242AO5NVWYHO60OWANJ4MWYNCBVF59U";
		// 初始化,当应用启动时调用
		AdWalker.instance().init(context, key, "adwalker");
		AdWalker.instance(new AdWalkerListener() {
		}).init(context, key, "adwalker");

		boolean orientationBoolean = isScreenChange(context);
		if (orientationBoolean == false) {
			// 竖屏
			AdFrameSDK.instance().initPopFrame(context, new AdWalkerListener() {

				// 插屏预加载成功
				public void pobLoadingSucess() {
					AdFrameSDK.instance().showPopFrame(context, null,
							AdFrameSDK.VERTICAL);// 显示插屏广告
				};

			}, AdFrameSDK.VERTICAL);

		} else {
			// 横屏
			AdFrameSDK.instance().initPopFrame(context, new AdWalkerListener() {

				// 插屏预加载成功
				public void pobLoadingSucess() {
					AdFrameSDK.instance().showPopFrame(context, null,
							AdFrameSDK.HORIZONTAL);// 显示插屏广告
				};

			}, AdFrameSDK.HORIZONTAL);

		}

	}

	public boolean isScreenChange(Context context) {

		Configuration mConfiguration = context.getResources()
				.getConfiguration(); // 获取设置的配置信息
		
		int ori = mConfiguration.orientation; // 获取屏幕方向

		if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {

			// 横屏
			return true;
		} else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {

			// 竖屏
			return false;
		}
		return false;
	}

	
	
	
	@Override
	public void insertADtimeout() {
		
		System.out.println("插屏广告执行成功");
		String key = "AWC242AO5NVWYHO60OWANJ4MWYNCBVF59U";
		// 初始化,当应用启动时调用
		AdWalker.instance().init(context, key, "adwalker");
		AdWalker.instance(new AdWalkerListener() {
		}).init(context, key, "adwalker");

		boolean orientationBoolean = isScreenChange(context);
		if (orientationBoolean == false) {
			// 竖屏
			AdFrameSDK.instance().initPopFrame(context, new AdWalkerListener() {

				// 插屏预加载成功
				public void pobLoadingSucess() {
					AdFrameSDK.instance().showPopFrame(context, null,
							AdFrameSDK.TIMEVERTICAL);// 显示插屏广告    //此处设置为倒计时竖屏   
					
				};

			}, AdFrameSDK.TIMEVERTICAL);//此处设置为倒计时竖屏   

		} else {
			// 横屏
			AdFrameSDK.instance().initPopFrame(context, new AdWalkerListener() {

				// 插屏预加载成功
				public void pobLoadingSucess() {
					AdFrameSDK.instance().showPopFrame(context, null,
							AdFrameSDK.TIMEHORIZONTAL);// 显示插屏广告   //此处设置为倒计时横屏  
				};

			}, AdFrameSDK.TIMEHORIZONTAL);//此处设置为倒计时横屏

		}
		
		
	}

}
