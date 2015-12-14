package com.adwalker.wall.platform;

import android.content.Context;
import android.content.Intent;

import com.adwalker.wall.init.AdWalkerListener;
import com.adwalker.wall.platform.layout.AdShowActivity;
import com.adwalker.wall.platform.network.GuScoreManage;


public class AdScoreWallSDK {
	public static AdScoreWallSDK instance = null;
	
	private AdScoreWallSDK(){
		super();
	}
	
	public static AdScoreWallSDK instance(){
		if (instance == null) {
			instance = new AdScoreWallSDK();
		}
		return instance;
	}
	
	public void showScoreWall(Context context,AdWalkerListener adWalkerListener){
			Intent intent = new Intent(context, AdShowActivity.class);
			AdConstants.adWalkerListener = adWalkerListener;
			intent.putExtra("pagetype", AdConstants.PageTypeScore);
			context.startActivity(intent);
	}
	/**
	 * 查询积分
	 */
	public void getScore( final Context context,final AdWalkerListener adWalkerListener) {
		GuScoreManage.getInstance().getScore(context,adWalkerListener);
	}

	/**
	 * 消耗积分 consume_score 要消费的积分
	 */
	public void consumeScore( final Context context,final AdWalkerListener adWalkerListener,final int consume_score) {
		GuScoreManage.getInstance().consumeScore(context,adWalkerListener,consume_score);
	}
	
	/**
	 * 用户信息
	 */
	public void setUserInfo(String userInfo) {
		AdConstants.userInfo = userInfo;
	}
}
