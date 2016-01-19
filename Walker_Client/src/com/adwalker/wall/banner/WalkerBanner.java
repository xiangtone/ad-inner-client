package com.adwalker.wall.banner;

import android.content.Context;

import com.adwalker.wall.init.AdWalkerListener;
import com.adwalker.wall.platform.AdConstants;
import com.adwalker.wall.platform.layout.AdBannerLayout;


//banner广告入口
public class WalkerBanner {

	public static WalkerBanner instance = null;
	private WalkerBanner() {
	}
	public static WalkerBanner instance() {
		if (instance == null) {
			instance = new WalkerBanner();
		}
		return instance;
	}

	
	public void showBanner(Context context,final AdBannerLayout layout) {
		BannerManage.showBanner(context, layout,AdConstants.BANNER_BANNER,null);
	}
	
	public void showBanner(Context context,AdWalkerListener adWalkerListener,final AdBannerLayout layout) {
		BannerManage.showBanner(context, layout,AdConstants.BANNER_BANNER,adWalkerListener);
	}
	
	public AdBannerLayout getLayout(Context context) {
		return BannerManage.getLayout(context);
	}
}
