package com.adwalker.wall.banner;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adwalker.wall.init.AdInitialization;
import com.adwalker.wall.init.AdWalkerListener;
import com.adwalker.wall.platform.AdConstants;
import com.adwalker.wall.platform.AdWalker;
import com.adwalker.wall.platform.bean.WalkerAdBean;
import com.adwalker.wall.platform.layout.AdBannerLayout;
import com.adwalker.wall.platform.layout.AdDetailActivity;
import com.adwalker.wall.platform.layout.AdShowWebActivity;
import com.adwalker.wall.platform.network.GuNotifyManage;
import com.adwalker.wall.platform.network.GuServierManage;
import com.adwalker.wall.platform.util.AdAnimation;
import com.adwalker.wall.platform.util.AdApkUtil;
import com.adwalker.wall.platform.util.ImageLoadUtil;
import com.adwalker.wall.platform.util.FormatTools;
import com.adwalker.wall.platform.util.GuLogUtil;
import com.adwalker.wall.platform.util.MobileUtil;
import com.adwalker.wall.platform.util.GuUtil;

public class BannerManage {
	private static ImageView plaqueImage1;
	private static ImageView dowmnImage;
	private static Boolean isInstalled = false;
	public static int i = 1;
	private static List<AnimationSet> inList;
	private static List<AnimationSet> outList;
	private static int banner_height = 50;
	private static int bannerIndex = 0;
	private static int interval = 5;
	private static AdWalkerListener mListener = null;


	public static void showBanner(final Context context,
			final AdBannerLayout bannerView, final int banner_flag,
			AdWalkerListener adWalkerListener) {
		mListener = adWalkerListener;
		interval = 5;
		if (!MobileUtil.checkNetWork(context)) {
			if (mListener != null) {
				mListener.callFailed(400, "网络异常!");
			}
			return;
		} 
		// 获取banner轮转动画
		AdAnimation adAnimation = new AdAnimation(context);
		inList = adAnimation.getInAnimationSets();
		outList = adAnimation.getOutAnimationSets();
		
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while (Thread.currentThread().getName()
						.equals((i - 1) + "walkerBanner")) {
					try {
						if (!(GuUtil.eqString(MobileUtil.getMobileId(context)))) {
							if (BannerDataUtil.isTopActivy(context)) {
								replaceBanner(context, bannerView, banner_flag);
							}
						} else {
							AdWalker.instance(new AdWalkerListener() {
								@Override
								public void initSucess() {
									if (BannerDataUtil.isTopActivy(context)) {
										replaceBanner(context, bannerView,banner_flag);
									}
								}

								@Override
								public void callFailed(int code, String error) {
									if (mListener != null) {
										mListener.callFailed(404, "初始化失败....");
									}
								}
							}).init(context,"", "");

						}
						Thread.sleep(interval * 1000);
					} catch (Exception e) {
						GuLogUtil.e(AdConstants.LOG_ERR, "showBannerE: " + e);
						if (mListener != null) {
							mListener.callFailed(402, "获取图片失败!");
						}
					}
				}

			}

		});
		thread.setName((i++) + "walkerBanner");
		thread.start();
	}
	
	public static AdBannerLayout getLayout(Context context) {
//		if (MobileUtil.getScale(context) == 0) {
//			DisplayMetrics metrics = new DisplayMetrics();
//			WindowManager windowManager = (WindowManager) context
//					.getSystemService(Context.WINDOW_SERVICE);
//			windowManager.getDefaultDisplay().getMetrics(metrics);
////			MobileUtil.SCALE = metrics.density;
//		}
		ImageView bannerImage = new ImageView(context);
		LinearLayout.LayoutParams bannerImageParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, MobileUtil.dip2px(context,
						banner_height));
		bannerImage.setScaleType(ScaleType.FIT_XY);
		bannerImage.setLayoutParams(bannerImageParams);
		AdBannerLayout bannerFlipper = new AdBannerLayout(context);
		LinearLayout.LayoutParams bannerFlipperParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, MobileUtil.dip2px(context,
						banner_height));
		bannerFlipper.setLayoutParams(bannerFlipperParams);
		bannerFlipper.addView(bannerImage);
		BannerDataUtil.saveTopActivy(context);
		return bannerFlipper;
	}

	
	private static void replaceBanner(final Context context,
			final AdBannerLayout bannerView, int banner_flag) {
		banner_flag = AdConstants.BANNER_BANNER;
		List<WalkerAdBean> wallInfoList = BannerDataUtil.adListFromServer(context, -1,
				AdConstants.PAGE_TYPE_BANNER, banner_flag);
		if (wallInfoList != null && wallInfoList.size() > 0) {
			final WalkerAdBean wallInfo = wallInfoList.get(0);
			isInstalled = AdApkUtil.isInstalled(context,
					wallInfo.packageName);
			Drawable bannerDrawable = ImageLoadUtil.getDrawable(context,
					wallInfo.adimage_url, null);

			if (bannerDrawable != null) {
				final RelativeLayout relaty = new RelativeLayout(context);
				RelativeLayout.LayoutParams relatyImageParams = new RelativeLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				relaty.setLayoutParams(relatyImageParams);

				dowmnImage = new ImageView(context);
				RelativeLayout.LayoutParams dowmnImageParams = new RelativeLayout.LayoutParams(
						MobileUtil.dip2px(context, 122), MobileUtil.dip2px(
								context, 50));
				dowmnImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
				dowmnImageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				dowmnImage.setLayoutParams(dowmnImageParams);
				dowmnImage.setScaleType(ScaleType.FIT_XY);
				dowmnImage.setVisibility(View.GONE);
				if (isInstalled) {
					dowmnImage.setImageDrawable(FormatTools.getImageFromAssetsFile2("guopenbtn.png",
									context));
				} else {
					dowmnImage.setImageDrawable(FormatTools.getImageFromAssetsFile2("gudownbtn.png",
									context));
				}

				dowmnImage.setPadding(0, 0, 4, 0);
				final ImageView bannerImage = new ImageView(context);
				RelativeLayout.LayoutParams bannerImageParams = new RelativeLayout.LayoutParams(
						LayoutParams.FILL_PARENT, MobileUtil.dip2px(
								context, banner_height));
				bannerImage.setLayoutParams(bannerImageParams);
				bannerImage.setScaleType(ScaleType.FIT_XY);
				bannerImage.setImageDrawable(bannerDrawable);
				plaqueImage1 = BannerDataUtil.getnewImageView(context);
				relaty.addView(bannerImage, 0);
				
				dowmnImage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (wallInfo.state == AdConstants.APP_DOWNLOADED) {
							wallInfo.state = AdConstants.APP_UNDO;
							AdInitialization.notifyList.remove(String
									.valueOf(wallInfo.id));
						}
						if (wallInfo.state == AdConstants.APP_UNDO
								|| wallInfo.state == AdConstants.APP_INSTALLED) {
							GuNotifyManage.getInstance(null)
									.checkDownloadTask(context, wallInfo);
						}
					}
				});
				bannerImage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (wallInfo != null && wallInfo.id != null) {
							if (wallInfo.ad_type == AdConstants.JUMP_TYPE_DETAIL) {
								Intent intent = new Intent();
								Bundle bundle = new Bundle();
								bundle.putParcelable("wallInfo", wallInfo);
								bundle.putInt("adType",
										AdConstants.PAGE_TYPE_BANNER);
								intent.putExtras(bundle);
								intent.setClass(context,
										AdDetailActivity.class);
								context.startActivity(intent);
							} else if (wallInfo.ad_type == AdConstants.JUMP_TYPE_WEB) {
								AdConstants.DATA_WEB = wallInfo;
								Intent it = new Intent();
								it.setClass(context, AdShowWebActivity.class);
								context.startActivity(it);
							} else if (wallInfo.ad_type == AdConstants.JUMP_TYPE_DOWN) {
								if (wallInfo.state == AdConstants.APP_DOWNLOADED) {
									wallInfo.state = AdConstants.APP_UNDO;
									AdInitialization.notifyList.remove(String
											.valueOf(wallInfo.id));
								}
								if (wallInfo.state == AdConstants.APP_UNDO
										|| wallInfo.state == AdConstants.APP_INSTALLED) {
									GuNotifyManage.getInstance(null)
											.checkDownloadTask(context,
													wallInfo);
								}
							} else {
								switch (dowmnImage.getVisibility()) {
								case View.VISIBLE:
									if (relaty != null) {
										relaty.removeViewAt(2);
										relaty.removeViewAt(1);
									}
									dowmnImage.setVisibility(View.GONE);
									break;
								default:
									if (relaty != null) {
										while (relaty.getChildCount() > 1) {
											relaty.removeViewAt(1);
										}
										relaty.addView(plaqueImage1, 1);
										relaty.addView(dowmnImage, 2);
									}
									dowmnImage.setVisibility(View.VISIBLE);
									new Thread(new Runnable() {
										@Override
										public void run() {
											GuServierManage
													.actionLogFromServer(
															context,
															AdConstants.BUTTON_OPEN,
															wallInfo.id,
															wallInfo.page_type,
															wallInfo.bannerTag,
															""
																	+ wallInfo.id);
										}
									}).start();
									break;
								}
							}
						}
					}
				});

				if (bannerIndex < inList.size()) {
					bannerView.setInAnimation(inList.get(bannerIndex));
					bannerView.setOutAnimation(outList.get(bannerIndex));
					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							bannerView.removeAllViews();
							bannerView.addView(relaty);
							bannerView.showNext();
						}
					});
					bannerIndex += 1;
				} else {
					bannerIndex = 0;
					bannerView.setInAnimation(inList.get(bannerIndex));
					bannerView.setOutAnimation(outList.get(bannerIndex));
					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							bannerView.removeAllViews();
							bannerView.addView(relaty);
							bannerView.showNext();
						}
					});
				}
				interval = wallInfo.interval;
				if (mListener != null) {
					mListener.AdShowSucess();
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						GuServierManage.actionLogFromServer(context,
								AdConstants.SHOW_SUCCESS, wallInfo.id,
								wallInfo.page_type, wallInfo.bannerTag, ""
										+ wallInfo.id);
					}
				}).start();
			} else {
				if (mListener != null) {
					mListener.callFailed(400, "获取图片失败!");
				}
			}
		} else {
			if (mListener != null) {
				mListener.callFailed(400, "获取数据失败!");
			}
		}
	
	}



}
