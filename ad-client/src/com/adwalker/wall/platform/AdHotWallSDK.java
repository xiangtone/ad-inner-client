package com.adwalker.wall.platform;

import android.content.Context;
import android.content.Intent;
import com.adwalker.wall.platform.layout.AdShowActivity;


public class AdHotWallSDK {
	public static AdHotWallSDK instance = null;
	
	private AdHotWallSDK(){
		super();
	}
	
	public static AdHotWallSDK instance(){
		if (instance == null) {
			instance = new AdHotWallSDK();
		}
		return instance;
	}
	
	public void showHotWall(Context context){
			Intent intent = new Intent(context, AdShowActivity.class);	
			intent.putExtra("pagetype", AdConstants.PageTypeHot);
			context.startActivity(intent);
	}
	
}
