package com.onlinegame.dao;

public class PayConstants {
	// public final static String SERVER_SDK = "http://192.168.1.133:8080/";
	// //ip地址

	// public final static String SERVER_UNIONSDK = SERVER_SDK //银联地址
	// + "ACPSample_KongjianServer/form05_6_2_Consume";
	// public final static String SERVER_UNIONSDK
	// ="http://unionpay-server.n8wan.com:29141/form05_6_2_Consume"; //外网服务地址

//	 public final static String SERVER_UNIONSDK="http://192.168.0.101:8080/ACPSample_KongjianServer/form05_6_2_Consume";//内网测试地址
	public final static String SERVER_UNIONSDK = "http://unionpay-server.n8wan.com:29141/form05_6_2_Consume";// 外网地址
	
	public final static String NOTIFY_URL = "http://thirdpay-webhook.n8wan.com:29141/thirdpayCountServlet"
			; // 支付回调地址
	

	public final static String LOGIN_URL = "http://192.168.0.101:8080/"
			+ "x-account-server/jsp/login.jsp"; // 登陆界面
	public final static String MER_ID = "898440379930020"; // 银联商户号

}
