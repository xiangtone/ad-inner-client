package com.onlinegame.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.ipaynow.plugin.api.IpaynowPlugin;
import com.onlinegame.R;
import com.xqt.now.paysdk.XqtPay;
import com.xqt.now.paysdk.XqtPay.XqtPayListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


public class WechatPayUtils implements XqtPayListener{
	private static Context context ;
	private static WechatPayUtils wechatPayUtils = null;
	private static Activity act = null;
	private static ProgressDialog progressDialog = null;
	// 商户秘钥
	private static final String key = "88c1a59b8fa9d217c8c632c2921ef286";
	
	public WechatPayUtils(Context context ,Activity act){
		this.context = context;
		this.act = act ;
	}
	
	public static WechatPayUtils getInstances(Context context ,Activity act){
		if(wechatPayUtils ==null){
			wechatPayUtils = new WechatPayUtils(context, act);
		}
		return wechatPayUtils;
	}
	
	public void goToPay() {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			progressDialog = new ProgressDialog(act);
			progressDialog.setTitle("进度提示");
			progressDialog.setMessage("支付安全环境扫描");
			progressDialog.setCancelable(false);
			
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
			
			// 获取支付参数
			XqtPay.Transit(act, this);

		} else {
			Builder builder = new AlertDialog.Builder(act);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setTitle("网络状态");
			builder.setMessage("没有可用网络,是否进入设置面板");
			builder.setPositiveButton("是",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							context.startActivity(new Intent(
									android.provider.Settings.ACTION_WIRELESS_SETTINGS));
						}
					});
			builder.setNegativeButton("否",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(act, "联网失败", Toast.LENGTH_SHORT)
									.show();
						}
					});
			builder.create().show();
		}
	}

	public void prePayMessage() {
		
		
		XqtPay.consumerId = "154345"; //修改商户id
		XqtPay.mhtOrderName = "屠龙刀    ";
		
		XqtPay.mhtOrderDetail = "屠龙刀     16156";
		XqtPay.notifyUrl = "http://thirdpay-webhook.n8wan.com:29141/thirdpayCountServlet"; //回调地址
		XqtPay.superid = "100000";
		IpaynowPlugin.setShowConfirmDialog(false);
		
	}

	public static void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		String respCode = data.getExtras().getString("respCode");
		String respMsg = data.getExtras().getString("respMsg");
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("支付结果通知");
		StringBuilder temp = new StringBuilder();
		if (respCode.equals("00")) {
			temp.append("交易状态:成功");
		}

		if (respCode.equals("02")) {
			temp.append("交易状态:取消");
		}

		if (respCode.equals("01")) {
			temp.append("交易状态:失败").append("\n").append("原因:" + respMsg);
		}

		if (respCode.equals("03")) {
			temp.append("交易状态:未知").append("\n").append("原因:" + respMsg);
		}
		builder.setMessage(temp.toString());
		builder.setInverseBackgroundForced(true);
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	@Override
	public void success(String str) {
		progressDialog.dismiss();
		// 是否打开未支付返回再次支付提示
		IpaynowPlugin.setShowConfirmDialog(true);
		// 发起支付请求
		IpaynowPlugin.pay(act, str);
		
	}

	@Override
	public void error(String str) {
		progressDialog.dismiss();
//		Toast.makeText(context, str, 1).show();

	}

	public String Sign() {
		String str = "customerid=" + XqtPay.consumerId + "&sdcustomno="
				+ XqtPay.mhtOrderNo + "&orderAmount=" + XqtPay.mhtOrderAmt
				+ key;
		return getMD5(str).toUpperCase();
	}

	public static String getMD5(String content) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(content.getBytes());
			return getHashString(digest);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getHashString(MessageDigest digest) {
		StringBuilder builder = new StringBuilder();
		for (byte b : digest.digest()) {
			builder.append(Integer.toHexString((b >> 4) & 0xf));
			builder.append(Integer.toHexString(b & 0xf));
		}
		return builder.toString();
	}
	
	
			
}
