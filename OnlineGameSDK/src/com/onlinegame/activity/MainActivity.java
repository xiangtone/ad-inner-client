package com.onlinegame.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.onlinegame.R;
import com.onlinegame.dao.PayConstants;
import com.onlinegame.dao.ProductBean;
import com.onlinegame.utils.LoginUtils;
import com.onlinegame.utils.PayChoosesUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Context context;
	private Button btn_pay; // 支付
	private Button btn_login; // 网页登陆
	private TextView tx_islogin;
	private final String NAME_SPASE = "webjs"; // webView交互
	private String url = PayConstants.LOGIN_URL; // 登陆界面
	private WebView webpobView;
	private TextView tx_uuid;
	private String getuid; // 登陆成功后得到的uid值
	private Activity act;
	private ProductBean productBean = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = this;
		act = this;
		btn_pay = (Button) findViewById(R.id.btn_pay);
		btn_login = (Button) findViewById(R.id.btn_login);
		tx_uuid = (TextView) findViewById(R.id.txuuid);
		tx_islogin = (TextView) findViewById(R.id.islogin);
		// btn_pay.setVisibility(View.INVISIBLE); //隐藏按钮(正式启动)

		// 登陆按钮点击事件
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webpobView = LoginUtils.getInstances().showLoginDialog(context,
						url); // 返回一個webview
				
				webpobView.addJavascriptInterface(new JavascriptInterface(
						context), NAME_SPASE); // 设置webview的javascript

			}
		});

		// 支付按钮点击事件
		btn_pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String orderId = new SimpleDateFormat("yyyyMMddHHmmss")
						.format(new Date());

				// orderId 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
				ProductBean.getInstance().setProduct_describe(orderId);
				// 设置价格 单位:分
				ProductBean.getInstance().setProduct_price("1");
				// 商品名字
				ProductBean.getInstance().setProduct_subject("火焰剑");

				PayChoosesUtils.getInstances(context, act).CreatePayDialog(); // 弹出支付窗口
				
				
			}
		});

	}

	/**
	 * webView使用JavaScript获得网页传的数据
	 * 
	 * @author 28518
	 * 
	 */
	public class JavascriptInterface {
		private Context context;

		public JavascriptInterface(Context context) {
			this.context = context;
		}

		@android.webkit.JavascriptInterface
		public void getUid(final String uid) {
			getuid = uid;
			runOnUiThread(new Runnable() {
				public void run() {

					LoginUtils.getInstances().login_dialog.cancel();

					tx_islogin.setText("已登录");
					tx_uuid.setText("uid = " + uid);
					Toast.makeText(context, "已登录\n" + uid, Toast.LENGTH_SHORT)
							.show();
					btn_pay.setVisibility(View.VISIBLE); // 修改为显示的btn

				}
			});

		}
	}

	/**
	 * 支付完成回调客户端
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		PayChoosesUtils.getInstances(context, act).onActivityResult(
				requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);

	}

}
