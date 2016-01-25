package com.adwalker.wall.platform.layout;

import com.adwalker.wall.platform.bean.WalkerAdBean;
import com.adwalker.wall.platform.network.GuServierManage;
import com.adwalker.wall.platform.util.MobileUtil;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AdShowWebActivity extends Activity {
	private WebView webview;
	private WalkerAdBean info;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		info = AdConstants.DATA_WEB;
		if (info != null && info.ad_url != null
				&& !info.ad_url.trim().equals("")
				&& MobileUtil.checkNetWork(AdShowWebActivity.this)) {
			webview = new WebView(AdShowWebActivity.this);
			webview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			setContentView(webview);
			// 设置WebView属性，能够执行Javascript脚本
			webview.getSettings().setJavaScriptEnabled(true);
			// 设置Web视图
			webview.setWebViewClient(new HelloWebViewClient());
			// 加载需要显示的网页
			webview.loadUrl(info.ad_url);
		} else {
			AdShowWebActivity.this.finish();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				GuServierManage.actionLogFromServer(AdShowWebActivity.this, AdConstants.FORM_OPEN, info.id, info.page_type,info.bannerTag,""+info.id);
			}
		}).start();
		
	}
	// Web视图
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@Override
	// 设置回退
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webview.canGoBack()) {
				webview.goBack(); // goBack()表示返回WebView的上一页面
			} else {
				AdShowWebActivity.this.finish();
			}
			return true;
		}
		return false;
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
