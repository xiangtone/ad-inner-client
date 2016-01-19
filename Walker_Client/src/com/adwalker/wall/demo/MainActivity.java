package com.adwalker.wall.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import com.adwalker.wall.banner.WalkerBanner;
import com.adwalker.wall.frame.AdFrameSDK;
import com.adwalker.wall.init.AdWalkerListener;
import com.adwalker.wall.platform.AdHotWallSDK;
import com.adwalker.wall.platform.AdWalker;
import com.adwalker.wall.platform.AdScoreWallSDK;
import com.adwalker.wall.platform.layout.AdBannerLayout;
import com.adwalker.wall.service.AdwalkerWindowService;

public class MainActivity extends Activity {

	private Context context;
	int time;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.main);
		// LinearLayout bannerLayout = (LinearLayout)
		// findViewById(R.id.banner_linear);

		Button pobFrame = (Button) findViewById(R.id.guframe);
		Button scoreWall = (Button) findViewById(R.id.scoreWall);
		Button hotWall = (Button) findViewById(R.id.hotWall);
		Button getscore = (Button) findViewById(R.id.getscore);
		Button consumescore = (Button) findViewById(R.id.consumescore);
		Button timepobFrame = (Button) findViewById(R.id.timeAD);
		Button showWinAd = (Button) findViewById(R.id.showWinAd);
		Button closeWinAd = (Button) findViewById(R.id.closeWinAd);

		// 初始化,当应用启动时调用
		AdWalker.instance().init(this, "AWC242AO5NVWYHO60OWANJ4MWYNCBVF59U",
				"adwalker");

		AdWalker.instance(new AdWalkerListener() {

		}).init(this, "AWC242AO5NVWYHO60OWANJ4MWYNCBVF59U", "adwalker");

		// 推荐墙
		hotWall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AdHotWallSDK.instance().showHotWall(MainActivity.this);// 显示推荐墙
			}
		});

		// 插屏倒数广告
		timepobFrame.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean orientationInt = isScreenChange(context);
				if (orientationInt == true) { // 横屏
					AdFrameSDK.instance().initPopFrame(MainActivity.this,
							new AdWalkerListener() {
								// 插屏预加载成功
								public void pobLoadingSucess() {
									AdFrameSDK.instance().showPopFrame(
											MainActivity.this, null,
											AdFrameSDK.TIMEHORIZONTAL);// 显示插屏广告
								};
							}, AdFrameSDK.TIMEHORIZONTAL);

				} else if (orientationInt == false) { // 竖屏

					AdFrameSDK.instance().initPopFrame(MainActivity.this,
							new AdWalkerListener() {
								// 插屏预加载成功
								public void pobLoadingSucess() {
									AdFrameSDK.instance().showPopFrame(
											MainActivity.this, null,
											AdFrameSDK.TIMEVERTICAL);// 显示插屏广告
											
								};

							}, AdFrameSDK.TIMEVERTICAL);

				}

			}
		});

		// 插屏广告
		pobFrame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// 2015/12/7 16:24 修改横竖屏 增加了判断屏幕横竖屏状态 使用不同尺寸的dialog

				boolean orientationInt = isScreenChange(context);

				if (orientationInt == true) { // 横屏

					AdFrameSDK.instance().initPopFrame(MainActivity.this,
							new AdWalkerListener() {
								// 插屏预加载成功
								public void pobLoadingSucess() {
									AdFrameSDK.instance().showPopFrame(
											MainActivity.this, null,
											AdFrameSDK.HORIZONTAL);// 显示插屏广告
								};
							}, AdFrameSDK.HORIZONTAL);

				} else if (orientationInt == false) { // 竖屏

					AdFrameSDK.instance().initPopFrame(MainActivity.this,
							new AdWalkerListener() {
								// 插屏预加载成功
								public void pobLoadingSucess() {

									AdFrameSDK.instance().showPopFrame(
											MainActivity.this, null,
											AdFrameSDK.VERTICAL);// 显示插屏广告
								};

							}, AdFrameSDK.VERTICAL);
				}

			}
		});

		// Banner广告
		// AdBannerLayout layout =
		// WalkerBanner.instance().getLayout(this);//获取banner容器
		// bannerLayout.addView(layout);
		// WalkerBanner.instance().showBanner(this,layout);//显示banner

		// 积分墙
		scoreWall.setOnClickListener(new View.OnClickListener() {// 显示积分墙

					@Override
					public void onClick(View v) {
						AdScoreWallSDK.instance().showScoreWall(
								MainActivity.this, new AdWalkerListener() {
									/**
									 * 激活回调 earnScore:赚取的积分 balanceScore: 总余额
									 * unit: 单位
									 */
									public void AdActivating(int earnScore,
											int balanceScore, String unit) {
										System.out.println("成功赚钱" + earnScore
												+ "" + unit);
									};
								});
					}
				});

		// 查询用户积分
		getscore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AdScoreWallSDK.instance().getScore(MainActivity.this,
						new AdWalkerListener() {
							/**
							 * 查询积分 score:积分 unit: 单位
							 */
							@Override
							public void getSucess(int score, String unit) {
								System.out.println("查询余额" + score + "" + unit);
							}

							/**
							 * 失败回调 code:状态码 error: 错误信息
							 */
							@Override
							public void callFailed(int code, String error) {
								System.out.println("查询失败");
							}

						});
			}
		});

		// 消费用户积分
		consumescore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AdScoreWallSDK.instance().consumeScore(MainActivity.this,
						new AdWalkerListener() {
							/**
							 * 消耗积分 consumeScore:已消耗积分 balanceScore: 总余额 unit:
							 * 单位
							 */
							public void consumeSucess(int consumeScore,
									int balanceScore, String unit) {
								
								System.out.println("已消耗：" + balanceScore + ""
										+ unit + "剩余:" + balanceScore + ""
										+ unit);
								
							};
							/**
							 * 失败回调 code:状态码 error: 错误信息
							 */
							public void callFailed(int code, String error) {
								System.out.println("消耗失败");
							};
						}, 10);
			}
		});

		// 显示桌面悬浮窗
		showWinAd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent show = new Intent(context, AdwalkerWindowService.class);
				show.putExtra(AdwalkerWindowService.OPERATION,
						AdwalkerWindowService.OPERATION_SHOW);
				startService(show);

			}
		});

		// 关闭桌面悬浮窗
		closeWinAd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent hide = new Intent(context, AdwalkerWindowService.class);
				hide.putExtra(AdwalkerWindowService.OPERATION,
						AdwalkerWindowService.OPERATION_HIDE);
				startService(hide);
			}
		});

	}

	// 注销
	protected void onDestroy() {
		AdWalker.instance().release();
		super.onDestroy();
	}

	/**
	 * 判断屏幕横竖屏
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isScreenChange(Context context) {

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

}
