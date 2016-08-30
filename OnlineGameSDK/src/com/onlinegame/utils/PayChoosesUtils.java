package com.onlinegame.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.onlinegame.R;
import com.onlinegame.dao.PayConstants;
import com.onlinegame.dao.ProductBean;
import com.xqt.now.paysdk.XqtPay;

public class PayChoosesUtils {
	private Context context;
	private Activity act;
	private static PayChoosesUtils payChoosesUtils = null;
	private String actRequstcode; // 判断支付回调
	private ProductBean productBean = null ;
	
	public PayChoosesUtils(Context context, Activity act) {
		this.context = context;
		this.act = act;
	}

	public static PayChoosesUtils getInstances(Context context, Activity act) {
		
		if (payChoosesUtils == null) {
			payChoosesUtils = new PayChoosesUtils(context, act);
		}
		return payChoosesUtils;
	}
	
	
	public void CreatePayDialog() {

		View view = LayoutInflater.from(context).inflate(
				R.layout.pay_dialog_check01, null);

		RelativeLayout zfb_app = (RelativeLayout) view
				.findViewById(R.id.zfb_app);
		RelativeLayout yl_app = (RelativeLayout) view.findViewById(R.id.yl_app);
		RelativeLayout wx_app = (RelativeLayout) view.findViewById(R.id.wx_app);

		ImageView zfb_imageView = (ImageView) view
				.findViewById(R.id.zfb_imageView);
		ImageView zfb_imageView_arrow = (ImageView) view
				.findViewById(R.id.zfb_imageView_arrow);
		View zfb_line = view.findViewById(R.id.zfb_line);

		ImageView yl_imageView = (ImageView) view
				.findViewById(R.id.yl_imageView);
		ImageView yl_imageView_arrow = (ImageView) view
				.findViewById(R.id.yl_imageView_arrow);
		View yl_line = view.findViewById(R.id.yl_line);

		ImageView wx_imageView = (ImageView) view
				.findViewById(R.id.wx_imageView);
		ImageView wx_imageView_arrow = (ImageView) view
				.findViewById(R.id.wx_imageView_arrow);
		View wx_line = view.findViewById(R.id.wx_line);

		LinearLayout dialog_pay_bg = (LinearLayout) view
				.findViewById(R.id.dialog_pay_bg);
		View title_line = view.findViewById(R.id.title_line);

		dialog_pay_bg.setBackgroundDrawable(FormatTools.getNineDrawable(
				context, "button_rromp_bg.9.png"));

		zfb_imageView.setImageDrawable(FormatTools.getImageFromAssetsFile2(
				"zfb.png", context));

		yl_imageView.setImageDrawable(FormatTools.getImageFromAssetsFile2(
				"yl.png", context));
		wx_imageView.setImageDrawable(FormatTools.getImageFromAssetsFile2(
				"wx.png", context));
		zfb_imageView_arrow.setImageDrawable(FormatTools
				.getImageFromAssetsFile2("arrow.png", context));
		yl_imageView_arrow.setImageDrawable(FormatTools
				.getImageFromAssetsFile2("arrow.png", context));
		wx_imageView_arrow.setImageDrawable(FormatTools
				.getImageFromAssetsFile2("arrow.png", context));
				
		zfb_line.setBackgroundDrawable(FormatTools.getImageFromAssetsFile2(
				"line_rromp.9.png", context));
		yl_line.setBackgroundDrawable(FormatTools.getImageFromAssetsFile2(
				"line_rromp.9.png", context));
		wx_line.setBackgroundDrawable(FormatTools.getImageFromAssetsFile2(
				"line_rromp.9.png", context));
		title_line.setBackgroundDrawable(FormatTools.getImageFromAssetsFile2(
				"line_rromp.9.png", context));
				
		zfb_app.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				// 点击支付宝
				AlipayUtils.getInstances(context, act,productBean).check(); //检查是否正常
				AlipayUtils.getInstances(context, act,productBean).pay(); //支付
			}
		});

		yl_app.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击银联支付
				
				UnionPayUtils.getInstances(context, act).unionpay();
				
				actRequstcode = "unionRequest";
				
			}
			
		});

		// 点击微信支付
		wx_app.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WechatPayUtils.getInstances(context, act).prePayMessage();
				
				XqtPay.mhtOrderNo = new SimpleDateFormat("yyyyMMddHHmmss",
						Locale.CHINA).format(new Date());
				XqtPay.payChannelType = "13";

				XqtPay.mhtOrderAmt = ProductBean.getInstance().getProduct_price(); // 商品价格 (暂不支持修改商品名称)

				XqtPay.sign = WechatPayUtils.getInstances(context, act).Sign();

				WechatPayUtils.getInstances(context, act).goToPay(); //执行支付

				actRequstcode = "wechatRequest";
				
			}
		});

		Dialog dialog_xml = new Dialog(context);
		dialog_xml.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog_xml.setContentView(view);
		dialog_xml.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		dialog_xml.show();

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (actRequstcode.equals("unionRequest")) {
			UnionPayUtils.onActivityResult(
					requestCode, resultCode, data);
		} else if (actRequstcode.equals("wechatRequest")) {
			WechatPayUtils.onActivityResult(
					requestCode, resultCode, data);
		}
	}
}
