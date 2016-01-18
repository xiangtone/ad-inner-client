package com.adwalker.wall.frame;

import java.io.InputStream;
import java.util.List;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.adwalker.wall.init.AdInitialization;
import com.adwalker.wall.init.AdWalkerListener;
import com.adwalker.wall.platform.AdWalker;
import com.adwalker.wall.platform.AdConstants;
import com.adwalker.wall.platform.bean.WalkerAdBean;
import com.adwalker.wall.platform.layout.AdDetailActivity;
import com.adwalker.wall.platform.layout.AdShowWebActivity;
import com.adwalker.wall.platform.network.GuNotifyManage;
import com.adwalker.wall.platform.network.GuServierManage;
import com.adwalker.wall.platform.util.AdApkUtil;
import com.adwalker.wall.platform.util.ImageLoadUtil;
import com.adwalker.wall.platform.util.FormatTools;
import com.adwalker.wall.platform.util.MobileUtil;
import com.adwalker.wall.platform.util.GuUtil;

public class AdFrameModel {
	
	private static ImageView plaqueImage1;
	private static Dialog dialog;
	private static Button downButton;
	private static Boolean isInstalled = false;
	private static InputStream gifInStream;
	private static WalkerAdBean wallInfo;
	private static int preloadingStatus = 0;//0预加载中，1预加载完成,2预加载失败
	private static RelativeLayout plaqueRelative;
	private static boolean isShowPlaque = true;//预加载开关控制
	
	public static void checkpobFrame(final Context context, final int frameFlag,final AdWalkerListener adWalkerListener){
		if (!MobileUtil.checkNetWork(context)) {
			adWalkerListener.callFailed(404, "网络异常,插屏预加载失败!");
			return;
		}
		if(GuUtil.eqString(MobileUtil.getMobileId(context))){
			AdWalker.instance(new AdWalkerListener() {
					@Override
					public void initSucess() {
						preloadingPlaque(context, frameFlag,adWalkerListener);
					}
				}).init(context,"","");
			
		}else{
			preloadingPlaque(context,frameFlag,adWalkerListener);
		}
	}
	

	private static void preloadingPlaque(final Context context, final int plaue_flag,final AdWalkerListener adWalkerListener) {
		if(isShowPlaque){
			isShowPlaque = false;
			new Thread(new Runnable() {				
				@Override
				public void run() {
						preloadingStatus = 0;
						int plaueFlagTag = getPlaueFlag(plaue_flag);
						List<WalkerAdBean> wallInfoList = AdFrameDataUtil.adListFromServer(context, -1,AdConstants.PAGE_TYPE_PLAQUE, plaueFlagTag);
						if (wallInfoList != null && wallInfoList.size() > 0) {
							wallInfo = wallInfoList.get(0);
							isInstalled = AdApkUtil.isInstalled(context, wallInfo.packageName);
							gifInStream = ImageLoadUtil.saveGifImg(context, wallInfo.adimage_url);
							preloadingStatus = 1;
							if(adWalkerListener!=null){
								adWalkerListener.pobLoadingSucess();
							}
						}else{
							preloadingStatus = 2;
							isShowPlaque = true;
							if(adWalkerListener!=null){
								adWalkerListener.callFailed(400, "暂时插屏数据，请检查你应用状态！");
							}
						}	
				}
			}).start();
		}else{
			adWalkerListener.pobLoadingSucess();
		}
	}
	
	private static int getPlaueFlag(int plaue_flag){
		int plaueFlag;
		if (plaue_flag == AdConstants.PLAQUE_CROSS) {
			plaueFlag = AdConstants.PLAQUE_CROSS;
		} else {
			plaueFlag = AdConstants.PLAQUE_VERTICAL;
		}
		return plaueFlag;
	}
	
	public static void showFrame(final Context context,final int plaue_flag, final AdWalkerListener adWalkerListener) {
		if(preloadingStatus == 1){
			final LinearLayout linear = getFrameView(context, wallInfo,wallInfo.adimage_width/2,wallInfo.adimage_height/2);
			if (context!=null&&!((Activity) context).isFinishing()){
				((Activity) context).runOnUiThread(new Runnable() {
					public void run() {
			        	dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
			        	dialog.setContentView(linear);
			        	dialog.setCancelable(false);
			        	dialog.show();
			        	preloadingStatus = 0;
						isShowPlaque = true;
			        	if(adWalkerListener!=null){
			        		adWalkerListener.AdShowSucess();
						}
			        	dialog.setOnDismissListener(new OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialog) {
								if(adWalkerListener!=null){
									adWalkerListener.AdClose();
								}
							}
						});
					}
				});
				//发送展示成功日志
				new Thread(new Runnable() {
					@Override
					public void run() {
						  GuServierManage.actionLogFromServer(context, AdConstants.SHOW_SUCCESS, wallInfo.id, wallInfo.page_type,wallInfo.bannerTag,""+wallInfo.id);
					}
				}).start();
			}
		}else if(preloadingStatus == 2){
			preloadingStatus = 0;
			isShowPlaque = true;
			if(adWalkerListener!=null){
				adWalkerListener.callFailed(402, "获取插屏数据失败!");
			}
		}else if(preloadingStatus == 0){
			if(adWalkerListener!=null){
				adWalkerListener.callFailed(405, "插屏数据加载中...");
			}
		}
	
	}
	
	
	
	private  static LinearLayout getFrameView(final Context context, final WalkerAdBean wallInfo, int width, int height) {

		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		linearLayout.setGravity(Gravity.CENTER);
		linearLayout.setBackgroundColor(Color.rgb(59, 62, 72));
		linearLayout.getBackground().setAlpha(150);//0~255透明度值  
		
		plaqueRelative = new RelativeLayout(context);
		plaqueRelative.setBackgroundDrawable( FormatTools.getImageFromAssetsFile2("gupobframebg.png", context));
		plaqueRelative.setPadding(6, 6, 6, 6);
		plaqueRelative.setLayoutParams(new LayoutParams(
				MobileUtil.dip2px(context, width + 6), MobileUtil.dip2px(context, height + 6)));
		plaqueRelative.setGravity(Gravity.CENTER);
		//关闭按钮
		Button cancelButton = new Button(context);
		LayoutParams cancelParams = new LayoutParams(MobileUtil.dip2px(context, 35), MobileUtil.dip2px(context,35));
		cancelParams.addRule(RelativeLayout.ALIGN_RIGHT,12345);
		cancelButton.setLayoutParams(cancelParams);
		cancelButton.setBackgroundDrawable(FormatTools.getImageFromAssetsFile2("guyclose.png", context));
		
		//下载按钮
		downButton = new Button(context);
		RelativeLayout.LayoutParams downParams = new RelativeLayout.LayoutParams(MobileUtil.dip2px(
				context, 225), MobileUtil.dip2px(context, 82));
		downParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		downButton.setLayoutParams(downParams);
		downButton.setVisibility(View.GONE);
		if(isInstalled){
			downButton.setBackgroundDrawable( FormatTools.getImageFromAssetsFile2("guopenbtn.png", context));
		}else{
			downButton.setBackgroundDrawable( FormatTools.getImageFromAssetsFile2("gudownbtn.png", context));
		}
		
		//插屏图片
		ImageView pobView = null;
		LayoutParams plaqueParams = new LayoutParams(MobileUtil.dip2px(context, width), MobileUtil.dip2px(context, height));
		pobView = new ImageView(context);
		pobView.setId(12345);
		pobView.setLayoutParams(plaqueParams);
		pobView.setScaleType(ScaleType.FIT_XY);
		pobView.setImageDrawable(FormatTools.InputStream2Drawable(gifInStream));
		plaqueRelative.addView(pobView,0);
		//阴影图片
		plaqueImage1 = AdFrameDataUtil.getImageView(context, width, height);
		plaqueRelative.addView(cancelButton,1);
		linearLayout.addView(plaqueRelative);
		
		downButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(wallInfo.state==AdConstants.APP_DOWNLOADED){
					wallInfo.state = AdConstants.APP_UNDO;
					AdInitialization.notifyList.remove(String.valueOf(wallInfo.id));
				}
				if(wallInfo.state==AdConstants.APP_UNDO||wallInfo.state==AdConstants.APP_INSTALLED){
					GuNotifyManage.getInstance(null).checkDownloadTask(context, wallInfo);
				}
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		if(pobView!=null){
			pobView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (wallInfo.ad_type == AdConstants.JUMP_TYPE_DETAIL) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putParcelable("wallInfo",wallInfo);
						bundle.putInt("adType", AdConstants.PAGE_TYPE_PLAQUE);
						intent.putExtras(bundle);
						intent.setClass(context, AdDetailActivity.class);
						context.startActivity(intent);
					} else if(wallInfo.ad_type == AdConstants.JUMP_TYPE_WEB ){
						AdConstants.DATA_WEB = wallInfo;
						Intent it = new Intent();
						it.setClass(context, AdShowWebActivity.class);
						context.startActivity(it);
					} else if(wallInfo.ad_type == AdConstants.JUMP_TYPE_DOWN){
						dialog.dismiss();
						if(wallInfo.state==AdConstants.APP_DOWNLOADED){
							wallInfo.state = AdConstants.APP_UNDO;
							AdInitialization.notifyList.remove(String.valueOf(wallInfo.id));
						}
						if(wallInfo.state==AdConstants.APP_UNDO||wallInfo.state==AdConstants.APP_INSTALLED){
							GuNotifyManage.getInstance(null).checkDownloadTask(context, wallInfo);
						}
					}else 
					{
						switch (downButton.getVisibility()) {
						case View.VISIBLE:
							downButton.setVisibility(View.GONE);
							plaqueRelative.removeViewAt(3);
							plaqueRelative.removeViewAt(2);
							break;
						case View.GONE:
							plaqueRelative.addView(plaqueImage1, 2);
							plaqueRelative.addView(downButton, 3);
							downButton.setVisibility(View.VISIBLE);
							new Thread(new Runnable() {
								@Override
								public void run() {
									GuServierManage.actionLogFromServer(context, AdConstants.BUTTON_OPEN, wallInfo.id, wallInfo.page_type,wallInfo.bannerTag,""+wallInfo.id);
								}
							}).start();
							break;
						}
					}
				}
			});

		}
		return linearLayout;
	}
	

}