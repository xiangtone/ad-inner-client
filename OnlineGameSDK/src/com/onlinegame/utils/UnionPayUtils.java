package com.onlinegame.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.onlinegame.dao.PayConstants;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppayplugin.demo.RSAUtil;


public class UnionPayUtils  implements Callback{
	private static Context context ;
	private static Activity act ;
	private static UnionPayUtils unionPayUtils = null;
	
	// 银联代码
	public static final String LOG_TAG = "PayDemo";
	private int mGoodsIdx = 0;
	private ProgressDialog mLoadingDialog = null;
	public static final int PLUGIN_VALID = 0;
	public static final int PLUGIN_NOT_INSTALLED = -1;
	public static final int PLUGIN_NEED_UPGRADE = 2;
	/*****************************************************************
	 * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
	 *****************************************************************/
	private final String mMode = "00";		
	private Handler mHandler = null;
	
	private static String txnTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	
	// 商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
	private static String merId = "898440379930020";
			
	// orderId 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
	private static String orderId = null;
	
	// txnAmt商品价格
	private static String txnAmt = null;
			
	
	public UnionPayUtils(Context context ,Activity act ,String orderId,String txnAmt){
		this.context = context ;
		this.act = act ;
		this.orderId = orderId;
		this.txnAmt = txnAmt;
	}
	
	public static UnionPayUtils getInstances(Context context , Activity act,String orderId,String txnAmt){
		if(unionPayUtils == null){
			unionPayUtils = new UnionPayUtils(context,act,orderId,txnAmt);
		}
		return unionPayUtils;
	}
	
	
	/**
	 * 执行银联支付
	 */
	public void unionpay(){
		
		final String TN_URL_01 = PayConstants.SERVER_UNIONSDK
				+ "?txnTime="
				+ txnTime
				+ "&merId=" + merId + "&orderId=" + orderId + "&txnAmt=" + txnAmt;
		
	//	Log.e(LOG_TAG, " " + v.getTag());
	//	mGoodsIdx = (Integer) v.getTag();  ???
		
		mHandler = new Handler(this);
		/*************************************************
		 * 步骤1：从网络开始,获取交易流水号即TN
		 ************************************************/

		
		
			mLoadingDialog = ProgressDialog.show(context, // context
					"", // title
					"正在努力的获取tn中,请稍候...", // message
					true); // 进度是否是不确定的，这只和创建进度条有关

			/*************************************************
			 * 步骤1：从网络开始,获取交易流水号即TN
			 ************************************************/

			new Thread(new Runnable() {

				@Override
				public void run() {

					String tn = null;
					InputStream is;
					try {
						
						String url = TN_URL_01;

						URL myURL = new URL(url);
						URLConnection ucon = myURL.openConnection();
						ucon.setConnectTimeout(120000);
						is = ucon.getInputStream();
						int i = -1;
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						while ((i = is.read()) != -1) {
							baos.write(i);
						}
						tn = baos.toString();
						is.close();
						baos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

					Message msg = mHandler.obtainMessage();
					msg.obj = tn;
					mHandler.sendMessage(msg);

				}
			}).start();
		}
	
	

		 
		@Override
		public boolean handleMessage(Message msg) {
			Log.e(LOG_TAG, " " + "" + msg.obj);
			if (mLoadingDialog.isShowing()) {
				mLoadingDialog.dismiss();
			}

			String tn = "";
			if (msg.obj == null || ((String) msg.obj).length() == 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("错误提示");
				builder.setMessage("网络连接失败,请重试!");
				builder.setNegativeButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			} else {
				tn = (String) msg.obj;
				/*************************************************
				 * 步骤2：通过银联工具类启动支付插件
				 ************************************************/
				// doStartUnionPayPlugin(context, tn, mMode);
				UPPayAssistEx.startPay(context, null, null, tn, mMode);
			}

			return false;
		}
		
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			/*************************************************
			 * 步骤3：处理银联手机支付控件返回的支付结果
			 ************************************************/
			if (data == null) {
				return;
			}

			String msg = "";
			/*
			 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
			 */
			String str = data.getExtras().getString("pay_result");
			if (str.equalsIgnoreCase("success")) {
				// 支付成功后，extra中如果存在result_data，取出校验
				// result_data结构见c）result_data参数说明
					// 未收到签名信息
					// 建议通过商户后台查询支付结果
					msg = "支付成功！";
				
			} else if (str.equalsIgnoreCase("fail")) {
				msg = "支付失败！";
			} else if (str.equalsIgnoreCase("cancel")) {
				msg = "用户取消了支付";
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("支付结果通知");
			builder.setMessage(msg);
			builder.setInverseBackgroundForced(true);
			// builder.setCustomTitle();
			builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			
			
			
		}

		int startpay(Activity act, String tn, int serverIdentifier) {
			return 0;
		
		}

		

}
