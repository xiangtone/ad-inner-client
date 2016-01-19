package com.adwalker.wall.service;

import java.util.ArrayList;
import java.util.List;

import com.adwalker.wall.demo.R;
import com.adwalker.wall.platform.util.FormatTools;
import com.adwalker.wall.platform.util.MobileUtil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class AdwalkerWindowService extends Service {
	// 2015/12/15 增加桌面弹出广告功能
	public static final String OPERATION = "operation";
	public static final int OPERATION_SHOW = 100; // 显示悬浮窗
	public static final int OPERATION_HIDE = 101; // 关闭悬浮窗

	private static final int HANDLE_CHECK_ACTIVITY = 200;
	private static final int CANCLE_IMG = 201; // 关闭广告
	private boolean isAdded = false; // 是否已增加悬浮窗
	private boolean isHomeCancel = false; // 是否点击取消
	private static WindowManager wm;
	private static WindowManager.LayoutParams params;
	private ImageView btn_floatView;
	private List<String> homeList; // 桌面应用程序包名列表

	private ActivityManager mActivityManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		homeList = getHomes();
		createFloatView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		int operation = intent.getIntExtra(OPERATION, OPERATION_SHOW);
		switch (operation) {
		case OPERATION_SHOW:
			mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
			mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY);
			break;
		case OPERATION_HIDE:
			mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
			break;
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_CHECK_ACTIVITY:
				if (isHome()) {
					isHomeCancel = false;
				}
				if (isHome()) {
					if (!isAdded) {
						wm.addView(btn_floatView, params);
						isAdded = true; // 已经增加悬浮窗
					}
				} else if (!isHome() && !isHomeCancel) {

					if (isAdded) {
						wm.removeView(btn_floatView);
						isAdded = false;
					}
				}
				mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 1000);
				break;

			// case CANCLE_IMG:
			// // btn_floatView.setVisibility(View.GONE);
			// wm.removeView(btn_floatView);
			// isHomeCancel = true;
			//
			// isAdded = false; // 已近删掉悬浮窗
			// break;

			}
		}
	};

	/**
	 * 创建悬浮窗
	 */
	private void createFloatView() {
		// btn_floatView = new Button(getApplicationContext());
		btn_floatView = new ImageView(getApplicationContext());
		// btn_floatView.setImageResource(R.drawable.mm);
		// btn_floatView.setText("啊啊悬浮窗啊啊啊的!@#$%^&*()_)(&!@#$%^&*()_");

		btn_floatView.setBackgroundDrawable(FormatTools.getImageFromAssetsFile2(
				"mm.jpg", getApplicationContext()));

		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();

		// 设置window type
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		/*
		 * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
		 * 即拉下通知栏不可见
		 */

		params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

		// 设置Window flag
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		/*
		 * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
		 * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		 */

		// 设置悬浮窗的长得宽
		// MobileUtil.dip2px(
		// context, width + 250), MobileUtil.dip2px(context,
		// height + 50)
		params.width = MobileUtil.dip2px(getApplicationContext(), 300);
		params.height = MobileUtil.dip2px(getApplicationContext(), 500);

		// 设置悬浮窗的Touch监听
		btn_floatView.setOnTouchListener(new OnTouchListener() {
			int lastX, lastY;
			int paramX, paramY;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					paramX = params.x;
					paramY = params.y;
					break;
				case MotionEvent.ACTION_MOVE:
					int dx = (int) event.getRawX() - lastX;
					int dy = (int) event.getRawY() - lastY;
					params.x = paramX + dx;
					params.y = paramY + dy;
					// 更新悬浮窗位置
					wm.updateViewLayout(btn_floatView, params);
					break;
				}
				return true;
			}
		});

		btn_floatView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		wm.addView(btn_floatView, params);

		isAdded = true;
	}

	/**
	 * 获得属于桌面的应用的应用包名称
	 * 
	 * @return 返回包含所有包名的字符串列表
	 */
	private List<String> getHomes() {
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = this.getPackageManager();
		// 属性
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);

		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.activityInfo.packageName);
		}

		return names;

	}

	/**
	 * 判断当前界面是否是桌面
	 */
	public boolean isHome() {
		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		}
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		return homeList.contains(rti.get(0).topActivity.getPackageName());
	}

}
