package com.adwalker.wall.platform.layout;
import java.io.File;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import com.adwalker.wall.init.AdInitialization;
import com.adwalker.wall.platform.bean.WalkerAdBean;
import com.adwalker.wall.platform.network.GuDownloadInfo;
import com.adwalker.wall.platform.network.GuNotifyManage;
import com.adwalker.wall.platform.network.GuScoreManage;
import com.adwalker.wall.platform.network.GuNotifyManage.NotifyTask;
import com.adwalker.wall.platform.util.AdApkUtil;
import com.adwalker.wall.platform.util.GuDes3;
import com.adwalker.wall.platform.util.ImageLoadUtil;
import com.adwalker.wall.platform.util.MobileUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

@SuppressLint("NewApi")
public class AdShowActivity extends Activity {
	private final static int TIMEOUT = 40000;
	private WebView webView;
	private boolean isError;
	private Timer timer;
	private AdLoadingView loadingView;
	private WalkerAdBean wallInfo;
	private Context context;
	private AdLoadingView loading;
	private FrameLayout frameLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		context = this;	
		if(AdInitialization.notifyList == null){
			AdInitialization.notifyList = new Hashtable<String, NotifyTask>();
		}
		
		int pagetype = getIntent().getIntExtra("pagetype",0);
		String url = "";
		if(pagetype==AdConstants.PageTypeScore){
			 url = AdConstants.SERVER_SDK + AdConstants.SCORE_WALL_ADRESS;
		}else{
			 url = AdConstants.SERVER_SDK + AdConstants.RECOMMEND_WALL_ADRESS;
		}
		setWebView(url);
		frameLayout = new FrameLayout(context);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		frameLayout.setLayoutParams(layoutParams);
		frameLayout.addView(webView);
		addLoadingView();
		setContentView(frameLayout);
		
	}
	
    @SuppressLint("SetJavaScriptEnabled")
	private void setWebView(String url){
    	webView = new WebView(this);
		webView.setVerticalScrollBarEnabled(false);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);	
		webView.setHorizontalScrollBarEnabled(false);
		webView.getSettings().setAppCachePath(ImageLoadUtil.getDownloadDir(this));
		//设置缓存大小为10M
		webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 10);
		//无论是否有网络，只要本地有缓存，都使用缓存。本地没有缓存时才从网络上获取。
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		//使用localStorage则必须打开，且至少2.1版本
		webView.getSettings().setDomStorageEnabled(true);
		if (!MobileUtil.checkNetWork(context)) {
			String str = "file:///android_asset/guweb404.html"; 
			webView.loadUrl(str);
		}
		
		//监听网页加载速度以及设置网页标题
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				setProgress(newProgress * 100);
				if(newProgress == 100){
					removeLodingView();
				}
			}
		});
		
		webView.setWebViewClient(new WebViewClient(){
			
			//在页面加载开始时调用
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
//				htmlStart = new Date().getTime();
				mHandler.sendEmptyMessage(14);
			}

			//在页面加载结束时调用
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				removeLodingView();
//				htmlEnd = new Date().getTime();
				mHandler.sendEmptyMessage(15);
			}
			
			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
			}

			@Override
			public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN){
					return true;
				}
				return super.shouldOverrideKeyEvent(view, event);
			}
			
			//在点击请求的是链接是才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边。
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}
			
//			//重写此方法可以让webview处理https请求
//			@Override
//			public void onReceivedSslError(WebView view,
//					SslErrorHandler handler, SslError error) {
//				super.onReceivedSslError(view, handler, error);
//			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				removeLodingView();
				isError = true;
				String str = "file:///android_asset/guweb404.html"; 
				view.loadUrl(str);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});
				
		webView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype, long contentLength) {
				 //只提供下载开始接口，并传入五个参数，想要真正下载必须在这里启动子线程实现下载过程
			}
		});
		
		
		
		
		
		//js回调
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public void addLoadingView(int type){
				Message msg = new Message();
				msg.what = 7;
				msg.arg1 = type;
				mHandler.sendMessage(msg);
			}
		},"loading");
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public void removeLoadingView(){
				mHandler.sendEmptyMessage(8);
			}
		},"remove");
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public void backToActivity(){
				AdShowActivity.this.finish();
			}
		},"back");
		
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public void recoreTime(String state){
			}
		}, "record");
		
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public void openApp(String packageName){
				AdApkUtil.openPackage(context, packageName);
			}
		},"open");
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public void addScore(final String adId){
				new Thread(new Runnable() {
					@Override
					public void run() {
						WalkerAdBean wallInfo = new WalkerAdBean();
						wallInfo.id = Integer.parseInt(adId);
						wallInfo.page_type = 0;
						wallInfo.bannerTag = 0;
						GuScoreManage.getInstance().addScoreFromServer(AdShowActivity.this, wallInfo);
					}
				}).start();
			}
		}, "score");
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public boolean isInstalled(String packageName){
				boolean flag = false;
				flag = AdApkUtil.isInstalled(context, packageName);
				return flag;
			}
		},"installed");
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public void toast(String message){
				Toast.makeText(AdShowActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		},"toast");
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public boolean hasNetwork(){
				if (!MobileUtil.checkNetWork(AdShowActivity.this)) {
					return false;
				}else{
					return true;
				}
			}
		}, "isNetwork");
		
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public String wallParam(String pageSize,String pageNo,String pageType,String isSign){
				String param = "uuid="+MobileUtil.getMobileId(context)+"&pageNo="+pageNo+"&pageSize="+pageSize+"&page_type="+pageType+"&image_type=1&version="+AdConstants.WALKER_VERSION+"&appkey="+MobileUtil.getAPP_KEY(context)+"&channel="+MobileUtil.getAPP_CHANNEL(context)+"&terminalType=mobile&imsi="+MobileUtil.getImsi(context)+"&isSign="+isSign;
				String decodeParam = "";
				try {
					decodeParam = GuDes3.encode(param);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return decodeParam;
			}
		}, "dataparam");
		
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public String getNotifyParam(String pageSize,String pageNo,String pageType,String noticeType){
				String param = "uuid="+MobileUtil.getMobileId(context)+"&pageNo="+pageNo+"&pageSize="+pageSize+"&page_type="+pageType+"&image_type=1&version="+AdConstants.WALKER_VERSION+"&appkey="+MobileUtil.getAPP_KEY(context)+"&channel="+MobileUtil.getAPP_CHANNEL(context)+"&terminalType=mobile&imsi="+MobileUtil.getImsi(context)+"&isSign=1"+"&noticeType="+noticeType;
				String decodeParam = "";
				try {
					decodeParam = GuDes3.encode(param);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return decodeParam;
			}
		}, "notifyParam");
		
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public String pvParam(String id,String ids,String actionCode,String pageType){
				String param = "";
				if(id == null || id.equals("")){
					param = "uuid="+MobileUtil.getMobileId(context)+"&id=&ids="+ids+"&ac="+actionCode+"&appkey="+MobileUtil.getAPP_KEY(context)+"&channel="+MobileUtil.getAPP_CHANNEL(context)+"&page_type="+pageType+"&version="+AdConstants.WALKER_VERSION+"&terminalType=mobile"+"&imsi="+MobileUtil.getImsi(context);
				}else{
					param = "uuid="+MobileUtil.getMobileId(context)+"&id="+id+"&ids=&ac="+actionCode+"&appkey="+MobileUtil.getAPP_KEY(context)+"&channel="+MobileUtil.getAPP_CHANNEL(context)+"&page_type="+pageType+"&version="+AdConstants.WALKER_VERSION+"&terminalType=mobile"+"&imsi="+MobileUtil.getImsi(context);
				}
				String decodeParam = "";
				try {
					decodeParam = GuDes3.encode(param);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return decodeParam;
			}
		}, "pvparam");
		
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public String getAdParam(String adId,String pagetype){
				String param = "adId="+adId+"&uuid="+MobileUtil.getMobileId(context)+"&appkey="+MobileUtil.getAPP_KEY(context)+"&channel="+MobileUtil.getAPP_CHANNEL(context)+"&version="+AdConstants.WALKER_VERSION+"&terminalType=emobile&imsi="+MobileUtil.getImsi(context)+"&page_type="+pagetype;
				String decodeParam = "";
				try {
					decodeParam = GuDes3.encode(param);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return decodeParam;
			}
			
		}, "adParam");
		
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public String getFeedBackParam(String userFeedback,String emailAddr){
				String param = "uuid="+MobileUtil.getMobileId(context)+"&appkey="+MobileUtil.getAPP_KEY(context)+"&version="+AdConstants.WALKER_VERSION+"&userFeedback="+userFeedback+"&emailAddr="+emailAddr;
				String decodeParam = "";
				try {
					decodeParam = GuDes3.encode(param);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return decodeParam;
			}
		}, "feedback");
		
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public int getAndroidViersion(){
				int version = 0;
		        try {
		            version = Integer.valueOf(android.os.Build.VERSION.SDK);
		        } catch (NumberFormatException e) {
		            e.printStackTrace();
		        }
		        return version;
			}
		}, "version");
		
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public boolean isLoaded(int resourceSize,String fileName){
				String path = ImageLoadUtil.getDownloadDir(context)+fileName;
				File file = new File(path);
				if(file.exists() && file.length() >= resourceSize){
					 return true;
				}else{
					return false;
				}
			}
		}, "loaded");
				
		webView.addJavascriptInterface(new Object(){
			@SuppressWarnings("unused")
			public void clickOnAndroid(final String url,final String adId,final String title,final String packageName,final String pageType,final long resourceSize,final int isDownload,final int sign_status){
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(url != null && !url.equals("")){
							int index = url.lastIndexOf("/");
							String fileName = url.substring(index+1);
							AdConstants.sign_status = sign_status;
							if(wallInfo == null || AdInitialization.notifyList.get(adId) == null){
								wallInfo = new WalkerAdBean();
							}else{
								wallInfo = AdInitialization.notifyList.get(adId).wallInfo;
								if(wallInfo != null){
									if(wallInfo.state == AdConstants.APP_DOWNLOADING){
										return;
									}
								}else{
									return;
								}
							}
							if(isDownload == 0){
								AdConstants.isSignIn = 0;
							}else{
								//签到不发送日志
								AdConstants.isSignIn = 1;
							}
							wallInfo.id = Integer.parseInt(adId);
							wallInfo.title = title;
							wallInfo.packageName = packageName;
							int temp = pageType.indexOf(",");
							String indexValue = "";
							String catagory = "";
							if(temp == -1){
								wallInfo.page_type = Integer.parseInt(pageType);
							}else{
								String[] array = pageType.split(",");
								wallInfo.page_type = Integer.parseInt(array[0]);
								indexValue = array[1];
								catagory = array[2];
							}
							wallInfo.isDownload = isDownload;
							wallInfo.resourceSize = (int)resourceSize;
							wallInfo.downloadInfo = new GuDownloadInfo();
							wallInfo.downloadInfo.mId = Integer.parseInt(adId);
							wallInfo.downloadInfo.mUrl = url;
							wallInfo.downloadInfo.mFileName = fileName;
							wallInfo.downloadInfo.mName = title;
							wallInfo.downloadInfo.mDestination = ImageLoadUtil
									.getDownloadDir(context);
							wallInfo.downloadInfo.resourceSize = resourceSize;
							int position = url.lastIndexOf("/");
							String apkName = url.substring(position + 1);
							String path = ImageLoadUtil.getDownloadDir(context)+apkName;
							File file = new File(path);
							if(file.exists() && file.length() >= resourceSize){
								wallInfo.state = AdConstants.APP_DOWNLOADED;
								if(AdInitialization.notifyList.get(adId) == null){
									GuNotifyManage.notifyDownload(AdShowActivity.this, wallInfo,true);
								}
								GuNotifyManage.installApk(AdShowActivity.this, wallInfo);
								return;
							}
//							if(SdkModel.notifyList != null && SdkModel.notifyList.get(String.valueOf(wallInfo.id)) != null){
//								NotifyThread thread = SdkModel.notifyList.get(String.valueOf(wallInfo.id));
//								WallInfo wInfo = thread.getWallInfo();
//								if(wInfo.state == Constants.APP_DOWNLOADING){
//									Toast.makeText(WallActivity.this, "下载中，请稍候...", Toast.LENGTH_SHORT).show();
//									DownLoadManager.getInstance().delDownTask(wallInfo.downloadInfo);
//									SdkModel.notifyList.remove(String.valueOf(wallInfo.id));
//								}
//							}
							wallInfo.adWalkerListener = AdConstants.adWalkerListener;
							if(wallInfo.page_type == 0 || wallInfo.page_type == 1 || wallInfo.page_type == 6){
								GuNotifyManage.getInstance(mHandler,indexValue,catagory).h5AddDownloadTask(context,wallInfo);
							}else{
								GuNotifyManage.getInstance(mHandler,indexValue).h5AddDownloadTask(context, wallInfo);
							}
						}else{
							Toast.makeText(AdShowActivity.this, "下载路径为空!", Toast.LENGTH_LONG).show();
						}
					}
				});
			}
		}, "wall");
		//设置默认编码方式
		webView.getSettings().setDefaultTextEncodingName("UTF-8");		
		webView.loadUrl(url);
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if ((keyCode == KeyEvent.KEYCODE_BACK)) { 
			 if(isError){
				 isError = false;
				 finish();
			 }
			 if(webView.canGoBack()){
				 //返回到第一个页面
				 webView.loadUrl("javascript:goBack()");
		         return true; 
			 }else{
				 finish();
			 } 
	     }
		 return false;
	}

	
	private void addLoadingView(){
		loadingView = new AdLoadingView(context);
		frameLayout.addView(loadingView);
	}
	
	
	private void removeLodingView(){
		if(loading != null){
			frameLayout.removeView(loading);
			loading = null;
		}
		if(loadingView != null){
			frameLayout.removeView(loadingView);
			loadingView = null;
		}
	}

	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case 7:
					int type = msg.arg1;
					if(loading != null){
						frameLayout.removeView(loading);
						loading = null;
					}
					if(loadingView != null){
						frameLayout.removeView(loadingView);
						loadingView = null;
					}
					if(type == 0){
						loading = new AdLoadingView(context);
						frameLayout.addView(loading);
					}else if(type == 3){
						loadingView = new AdLoadingView(context);
						frameLayout.addView(loadingView);
					}
					break;
				case 8:
					if(loading != null){
						frameLayout.removeView(loading);
						loading = null;
					}
					if(loadingView != null){
						frameLayout.removeView(loadingView);
						loadingView = null;
					}
					break;
				case 9:
					if(webView != null){
						webView.stopLoading();
						isError = true;
						String str = "file:///android_asset/guweb404.html"; 
						webView.loadUrl(str);
					}
					if(loading != null){
						frameLayout.removeView(loading);
						loading = null;
					}
					if(loadingView != null){
						frameLayout.removeView(loadingView);
						loadingView = null;
					}
					break;
				case 10:
					if(webView != null){
						int index = msg.arg1;
						int catagory = msg.arg2;
						String value = (String)msg.obj;
						webView.loadUrl("javascript:changeState('"+index+"','"+value+"','"+catagory+"')");
					}
					break;
				case 11:
					//下载异常
					if(webView != null){
						int index = msg.arg1;
						int catagory = msg.arg2;
						webView.loadUrl("javascript:backDefault('"+index+"','"+catagory+"')");
					}
					break;
				case 14:
					timer = new Timer();
					TimerTask task = new TimerTask() {
						
						@Override
						public void run() {
							if(webView.getProgress() < 100){
								mHandler.sendEmptyMessage(9);
								timer.cancel();
								timer.purge();
							}
						}
					};
					timer.schedule(task, TIMEOUT);
					break;
				case 15:
					if(timer != null){
						timer.cancel();
		                timer.purge();
					}
					break;
				case 16:
					if(webView != null){
						int index = msg.arg1;
						int catagory = msg.arg2;
						webView.loadUrl("javascript:backDefault('"+index+"','"+catagory+"')");
					}
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
//		webView.clearCache(true);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.sendEmptyMessage(15);
	}
}
