package com.adwalker.wall.frame;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.adwalker.wall.demo.MainActivity;
import com.adwalker.wall.demo.R;
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
import com.adwalker.wall.platform.util.IpUtil;
import com.adwalker.wall.platform.util.MobileUtil;
import com.adwalker.wall.platform.util.GuUtil;

public class AdFrameModel {
	private static ImageView plaqueImage1;
	private static Dialog dialog;
	private static Button downButton;
	private static Boolean isInstalled = false;
	private static InputStream gifInStream;
	private static WalkerAdBean wallInfo;
	private static int preloadingStatus = 0;// 0预加载中，1预加载完成,2预加载失败
	private static RelativeLayout plaqueRelative;
	private static boolean isShowPlaque = true;// 预加载开关控制
	private static Button cancelButton;

	public static void checkpobFrame(final Context context,
			final int frameFlag, final AdWalkerListener adWalkerListener) {

		if (!MobileUtil.checkNetWork(context)) {

			adWalkerListener.callFailed(404, "网络异常,插屏预加载失败!");

			return;
		}

		if (GuUtil.eqString(MobileUtil.getMobileId(context))) {
			AdWalker.instance(new AdWalkerListener() {

				@Override
				public void initSucess() {

					preloadingPlaque(context, frameFlag, adWalkerListener);

				}

			}).init(context, "", "");

		} else {

			preloadingPlaque(context, frameFlag, adWalkerListener);

		}
	}

	private static void preloadingPlaque(final Context context,
			final int plaue_flag, final AdWalkerListener adWalkerListener) {

		if (isShowPlaque) {

			isShowPlaque = false;

			new Thread(new Runnable() {
				@Override
				public void run() {
					preloadingStatus = 0;

					int plaueFlagTag = getPlaueFlag(plaue_flag);

					List<WalkerAdBean> wallInfoList = AdFrameDataUtil
							.adListFromServer(context, -1,
									AdConstants.PAGE_TYPE_PLAQUE, plaueFlagTag);

					if (wallInfoList != null && wallInfoList.size() > 0) {
						wallInfo = wallInfoList.get(0);
						isInstalled = AdApkUtil.isInstalled(context,
								wallInfo.packageName);

						gifInStream = ImageLoadUtil.saveGifImg(context,
								wallInfo.adimage_url);

						preloadingStatus = 1;

						if (adWalkerListener != null) {
							// web闪退 //2015/12/9 解决闪退问题 在主线程里面更新界面

							if (context instanceof Activity) {
								((Activity) context)
										.runOnUiThread(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												adWalkerListener
														.pobLoadingSucess();

											}
										});

							}

						}
					} else {
						preloadingStatus = 2;
						isShowPlaque = true;
						if (adWalkerListener != null) {
							adWalkerListener
									.callFailed(400, "暂时插屏数据，请检查你应用状态！");
						}
					}
				}
			}).start();

		} else {
			adWalkerListener.pobLoadingSucess();
		}
	}

	private static int getPlaueFlag(int plaue_flag) {
		int plaueFlag;

		if (plaue_flag == AdConstants.PLAQUE_CROSS) {
			plaueFlag = AdConstants.PLAQUE_CROSS;
		} else if (plaue_flag == AdConstants.PLAQUE_VERTICAL) {
			plaueFlag = AdConstants.PLAQUE_VERTICAL;
		} else if (plaue_flag == AdConstants.TIMEPLAQUE_CROSS) {
			plaueFlag = AdConstants.PLAQUE_CROSS;
		} else {
			plaueFlag = AdConstants.PLAQUE_VERTICAL;
		}

		return plaueFlag;

	}

	public static void showFrame(final Context context, final int plaue_flag,
			final AdWalkerListener adWalkerListener) {

		// 预加载完成
		if (preloadingStatus == 1) {

			// 添加横竖屏切换
			final LinearLayout linear = getFrameView(context, wallInfo,
					wallInfo.adimage_width / 2, wallInfo.adimage_height / 2,
					plaue_flag);

			if (context != null && !((Activity) context).isFinishing()) {

				((Activity) context).runOnUiThread(new Runnable() {

					public void run() {

						if (plaue_flag == AdFrameSDK.TIMEHORIZONTAL) { // 如果是横屏TimeDialog

							TimedialogShow(context, adWalkerListener, linear);

						} else if (plaue_flag == AdFrameSDK.TIMEVERTICAL) {
							// gifInStream
							TimedialogShow(context, adWalkerListener, linear);

						} else {

							dialog = new Dialog(
									context,
									android.R.style.Theme_Translucent_NoTitleBar);
							dialog.setCancelable(false);
							if ( gifInStream != null && !gifInStream.equals("") ) {
								
								dialog.show();
							}

							preloadingStatus = 0;
							isShowPlaque = true;

							if (adWalkerListener != null) {

								adWalkerListener.AdShowSucess();

							}

							dialog.setOnDismissListener(new OnDismissListener() {
								@Override
								public void onDismiss(DialogInterface dialog) {
									if (adWalkerListener != null) {
										adWalkerListener.AdClose();
									}
								}
							});
							dialog.setOnKeyListener(new OnKeyListener() {

								@Override
								public boolean onKey(DialogInterface dialog,
										int keyCode, KeyEvent event) {

									dialog.cancel();

									return false;

								}
							});
							// 测试代码 anim styles
							// Window window = dialog.getWindow();
							// window.setWindowAnimations(R.style.main_menu_animstyle);

							dialog.setContentView(linear);

						}

					}
				});

				// 发送展示成功日志
				new Thread(new Runnable() {
					@Override
					public void run() {

						GuServierManage.actionLogFromServer(context,
								AdConstants.SHOW_SUCCESS, wallInfo.id,
								wallInfo.page_type, wallInfo.bannerTag, ""
										+ wallInfo.id);

					}
				}).start();
				
				
			}

		} else if (preloadingStatus == 2) {
			preloadingStatus = 0;
			isShowPlaque = true;
			if (adWalkerListener != null) {
				adWalkerListener.callFailed(402, "获取插屏数据失败!");
			}
		} else if (preloadingStatus == 0) {
			if (adWalkerListener != null) {
				adWalkerListener.callFailed(405, "插屏数据加载中...");
			}
		}

	}

	private static LinearLayout getFrameView(final Context context,
			final WalkerAdBean wallInfo, int width, int height, int plaue_flag) {
		// linearLayout 装了plaqueRelative的背景和imageView 返回
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));

		linearLayout.setGravity(Gravity.CENTER);
		linearLayout.setBackgroundColor(Color.rgb(59, 62, 72));
		linearLayout.getBackground().setAlpha(150);// 0~255透明度值

		// plaqueRelative 装了背景图片和imageView
		plaqueRelative = new RelativeLayout(context);
		plaqueRelative.setBackgroundDrawable(FormatTools
				.getImageFromAssetsFile2("gupobframebg.png", context));
		plaqueRelative.setPadding(6, 6, 6, 6);
		plaqueRelative.setGravity(Gravity.CENTER);

		cancelButton = new Button(context);

		LayoutParams cancelParams = new LayoutParams(MobileUtil.dip2px(context,

		35), MobileUtil.dip2px(context, 35));

		cancelParams.addRule(RelativeLayout.ALIGN_RIGHT, 12345);

		cancelButton.setLayoutParams(cancelParams);

		cancelButton.setBackgroundDrawable(FormatTools.getImageFromAssetsFile2(
				"guyclose.png", context));

		if (plaue_flag == AdFrameSDK.HORIZONTAL) {

			plaqueRelative.setLayoutParams(new LayoutParams(MobileUtil.dip2px(
					context, width + 250), MobileUtil.dip2px(context,
					height + 50)));// 设置dialog宽度高度

		} else if (plaue_flag == AdFrameSDK.VERTICAL) {

			plaqueRelative.setLayoutParams(new LayoutParams(MobileUtil.dip2px(
					context, width + 50), MobileUtil.dip2px(context,
					height + 250)));// 设置dialog宽度高度

		} else if (plaue_flag == AdFrameSDK.TIMEHORIZONTAL) {
			// 继续判断plaue_flag
			plaqueRelative.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT));// 设置dialog宽度高度

			// plaqueRelative.setLayoutParams(new
			// LayoutParams(MobileUtil.dip2px(
			// context, width + 50), MobileUtil.dip2px(context,
			// height + 250)));// 设置dialog宽度高度

			cancelButton.setVisibility(View.GONE);
			plaqueRelative.setBackgroundDrawable(null);

		} else if (plaue_flag == AdFrameSDK.TIMEVERTICAL) {

			// plaqueRelative.setLayoutParams(new
			// LayoutParams(MobileUtil.dip2px(
			// context, width + 250), MobileUtil.dip2px(context,
			// height + 50)));// 设置dialog宽度高度
			plaqueRelative.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT));
			cancelButton.setVisibility(View.GONE);
			plaqueRelative.setBackgroundDrawable(null);

		}

		// 下载按钮
		downButton = new Button(context);
		RelativeLayout.LayoutParams downParams = new RelativeLayout.LayoutParams(
				MobileUtil.dip2px(context, 225), MobileUtil.dip2px(context, 82));
		downParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		downButton.setLayoutParams(downParams);
		downButton.setVisibility(View.GONE);

		if (isInstalled) {
			downButton.setBackgroundDrawable(FormatTools
					.getImageFromAssetsFile2("guopenbtn.png", context));
		} else {
			downButton.setBackgroundDrawable(FormatTools
					.getImageFromAssetsFile2("gudownbtn.png", context));
		}

		// 判断加载XXXdialog
		if (wallInfo.ad_type == AdConstants.JUMP_TYPE_WEB) {

			AdConstants.DATA_WEB = wallInfo;

			// Intent it = new Intent();
			// it.setClass(context, AdShowWebActivity.class);
			// context.startActivity(it);
			// getDialogWebview(context, AdConstants.DATA_WEB.ad_url);

			linearLayout.addView(getWebViewDialog(context, width, height,
					AdConstants.DATA_WEB.ad_url));

		} else if (wallInfo.ad_type == AdConstants.JUMP_TYPE_DOWN) {

			// 加载imgdialog
			linearLayout.addView(getImgDialog(context, width, height));

		}

		downButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (wallInfo.state == AdConstants.APP_DOWNLOADED) {
					wallInfo.state = AdConstants.APP_UNDO;
					AdInitialization.notifyList.remove(String
							.valueOf(wallInfo.id));
				}
				if (wallInfo.state == AdConstants.APP_UNDO
						|| wallInfo.state == AdConstants.APP_INSTALLED) {
					GuNotifyManage.getInstance(null).checkDownloadTask(context,
							wallInfo);
				}
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 这是退出动画
//				Animation anim = AnimationUtils.loadAnimation(context,
//						R.anim.myanim_out);
//				plaqueRelative.startAnimation(anim);

				dialog.dismiss();
			}
		});

		// if (pobView != null) {
		// pobView.setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// if (wallInfo.ad_type == AdConstants.JUMP_TYPE_DETAIL) {
		// // Intent intent = new Intent();
		// // Bundle bundle = new Bundle();
		// // bundle.putParcelable("wallInfo",wallInfo);
		// // bundle.putInt("adType",
		// // AdConstants.PAGE_TYPE_PLAQUE);
		// // intent.putExtras(bundle);
		// // intent.setClass(context, AdDetailActivity.class);
		// // context.startActivity(intent);
		// } else if (wallInfo.ad_type == AdConstants.JUMP_TYPE_WEB) {
		//
		// AdConstants.DATA_WEB = wallInfo;
		// // Intent it = new Intent();
		// // it.setClass(context, AdShowWebActivity.class);
		// // context.startActivity(it);
		//
		// } else if (wallInfo.ad_type == AdConstants.JUMP_TYPE_DOWN) {
		// dialog.dismiss();
		// if (wallInfo.state == AdConstants.APP_DOWNLOADED) {
		// wallInfo.state = AdConstants.APP_UNDO;
		// AdInitialization.notifyList.remove(String
		// .valueOf(wallInfo.id));
		// }
		// if (wallInfo.state == AdConstants.APP_UNDO
		// || wallInfo.state == AdConstants.APP_INSTALLED) {
		// GuNotifyManage.getInstance(null).checkDownloadTask(
		// context, wallInfo);
		// }
		// } else {
		// switch (downButton.getVisibility()) {
		// case View.VISIBLE:
		// downButton.setVisibility(View.GONE);
		// plaqueRelative.removeViewAt(3);
		// plaqueRelative.removeViewAt(2);
		// break;
		// case View.GONE:
		// plaqueRelative.addView(plaqueImage1, 2);
		// plaqueRelative.addView(downButton, 3);
		// downButton.setVisibility(View.VISIBLE);
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// GuServierManage.actionLogFromServer(
		// context, AdConstants.BUTTON_OPEN,
		// wallInfo.id, wallInfo.page_type,
		// wallInfo.bannerTag, ""
		// + wallInfo.id);
		// }
		// }).start();
		// break;
		// }
		// }
		// }
		// });
		//
		// }

		return linearLayout;
	}

	public static RelativeLayout getImgDialog(final Context context, int width,
			int height) {

		// 插屏图片
		ImageView pobView = null;
		pobView = new ImageView(context);
		LayoutParams plaqueParams = new LayoutParams(
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.FILL_PARENT));

		pobView.setId(12345);
		pobView.setLayoutParams(plaqueParams);

		pobView.setScaleType(ScaleType.FIT_XY);
		pobView.setImageDrawable(FormatTools.InputStream2Drawable(gifInStream));

		plaqueRelative.addView(pobView, 0);
		// 阴影图片
		plaqueImage1 = AdFrameDataUtil.getImageView(context, width, height);
		plaqueRelative.addView(cancelButton, 1);

		// 测试代码
		// Window window = plaqueRelative.getWindow();
		// window.setWindowAnimations(R.style.main_menu_animstyle);

		//加载动画
//		Animation anim = AnimationUtils
//				.loadAnimation(context, R.anim.myanim_in);
//		plaqueRelative.startAnimation(anim);

		if (pobView != null) {
			pobView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (wallInfo.ad_type == AdConstants.JUMP_TYPE_DETAIL) {
						// Intent intent = new Intent();
						// Bundle bundle = new Bundle();
						// bundle.putParcelable("wallInfo",wallInfo);
						// bundle.putInt("adType",
						// AdConstants.PAGE_TYPE_PLAQUE);
						// intent.putExtras(bundle);
						// intent.setClass(context, AdDetailActivity.class);
						// context.startActivity(intent);
					} else if (wallInfo.ad_type == AdConstants.JUMP_TYPE_WEB) {

						// AdConstants.DATA_WEB = wallInfo;
						// Intent it = new Intent();
						// it.setClass(context, AdShowWebActivity.class);
						// context.startActivity(it);

					} else if (wallInfo.ad_type == AdConstants.JUMP_TYPE_DOWN) {
						// 点击广告关闭
						// 退出广告效果
//						Animation anim = AnimationUtils.loadAnimation(context,
//								R.anim.myanim_out);
//						plaqueRelative.startAnimation(anim);
						dialog.dismiss();
						if (wallInfo.state == AdConstants.APP_DOWNLOADED) {
							wallInfo.state = AdConstants.APP_UNDO;
							AdInitialization.notifyList.remove(String
									.valueOf(wallInfo.id));
						}
						if (wallInfo.state == AdConstants.APP_UNDO
								|| wallInfo.state == AdConstants.APP_INSTALLED) {
							GuNotifyManage.getInstance(null).checkDownloadTask(
									context, wallInfo);
						}
					} else {
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

									GuServierManage.actionLogFromServer(
											context, AdConstants.BUTTON_OPEN,
											wallInfo.id, wallInfo.page_type,
											wallInfo.bannerTag, ""
													+ wallInfo.id);
								}
							}).start();
							break;
						}
					}
				}
			});

		}

		return plaqueRelative;
	}

	public static RelativeLayout getWebViewDialog(final Context context, int width,
			int height, String url) {

		WebView webpobView = null;

		// MobileUtil.dip2px(context,width), MobileUtil.dip2px(context, height)

		LayoutParams plaqueParams = new LayoutParams(
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.FILL_PARENT));

		webpobView = new WebView(context);

		webpobView.setId(12345);
		webpobView.setLayoutParams(plaqueParams);
		webpobView.loadUrl(url);

		// 设置支持javascript
		webpobView.getSettings().setJavaScriptEnabled(true);
		// 启动缓存
		webpobView.getSettings().setAppCacheEnabled(true);

		webpobView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view,final String reloadurl) {
				view.loadUrl(reloadurl);
				
				
				// 发送统计数据
				final String date = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss")
						.format(new Date());
				final int AdClickWeb = 0; //点击了网页
				final int AdShow = 0; //展示了广告
				  //final String ClickToUrl = "";
				
				// 测试发送统计数据
				new Thread(new Runnable() {

					@Override
					public void run() {

						sendCount(date, AdClickWeb, AdShow, reloadurl,MobileUtil.getMobileId(context),"",IpUtil.getMobileIpAddress(context));

					}
				}).start();

				return true;
			}
		});

		plaqueRelative.addView(webpobView, 0);
		// 阴影图片
		plaqueImage1 = AdFrameDataUtil.getImageView(context, width, height);
		plaqueRelative.addView(cancelButton, 1);

		return plaqueRelative;
	}

	public static void TimedialogShow(Context context,
			final AdWalkerListener adWalkerListener, final LinearLayout linear) {

		dialog = new Dialog(context,
				android.R.style.Theme_Translucent_NoTitleBar);

		dialog.setCancelable(false);

		if ( gifInStream != null && !gifInStream.equals("") ) {
			dialog.show();
		}
		// 增加倒数时间dialog miss
		final Timer timer = new Timer();
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				dialog.cancel();
				timer.cancel();
			}
		};
		timer.schedule(tt, 3000);

		preloadingStatus = 0;
		isShowPlaque = true;
		if (adWalkerListener != null) {
			adWalkerListener.AdShowSucess();
		}
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (adWalkerListener != null) {
					adWalkerListener.AdClose();
				}
			}
		});
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				dialog.cancel();

				return false;

			}
		});

		dialog.setContentView(linear);

	}
	/**
	 * 发送WebView点击统计数据
	 * @param date 时间
	 * @param AdClickWeb 广告web点击
	 * @param AdShow 广告展示
	 * @param ClickToUrl 点击跳转后的url
	 * @param uid 
	 * @param isAdShow 是否展示广告窗
	 */
	public static void sendCount(String date, int AdClickWeb, int AdShow,
			String ClickToUrl,String uid,String isAdShow,String ipAddress) {
		
		// 添加其他统计
		String accountUrl = AdConstants.adUrl_service + "?date=" + date + "&AdClickWeb="
				+ AdClickWeb + "&AdShow=" + AdShow + "&ClickToUrl="
				+ ClickToUrl+"&uid="+uid+"&isAdShow="+isAdShow+"&ipAddress="+ipAddress;
		
		HttpGet httpGet = new HttpGet(accountUrl);// 编者按：与HttpPost区别所在，这里是将参数在地址中传递

		try {
			HttpResponse response = new DefaultHttpClient().execute(httpGet);

			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				Log.e("url--", "统计发送成功");
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}