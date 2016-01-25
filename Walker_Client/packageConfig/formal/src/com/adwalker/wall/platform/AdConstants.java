package com.adwalker.wall.platform;

import android.net.Uri;
import com.adwalker.wall.init.AdWalkerListener;
import com.adwalker.wall.platform.bean.WalkerAdBean;

public class AdConstants {
	
  public final static String SERVER_SDK = "http://121.40.134.145/AdService/";
	
	public final static String WALKER_VERSION = "androidV2.1.0"; // SDK版本
	
//	public final static String SERVER_SDK = "http://192.168.0.118:8081/AdService/";
//	public final static String SERVER_SDK = "http://192.168.0.182/AdService/";
//	public final static String SERVER_SDK = "http://192.168.0.114:8080/AdService/";
//	public final static String SERVER_SDK = "http://106.120.153.236/AdService/";
	public final static String SCORE_WALL_ADRESS = "html/adscorewall.html";
	public final static String RECOMMEND_WALL_ADRESS = "html/adhotwall.html";
	
	public final static String SERVER_INITIALIZE = SERVER_SDK
			+ "android/init.do"; // 初始化
	public final static String SERVER_SOFTLIST = SERVER_SDK
			+ "common/app_list.do"; // 已安装列表
	public final static String SERVER_GETSCORE = SERVER_SDK
			+ "android/get_score.do"; // 获取积分
	public final static String SERVER_CONSUMESCORE = SERVER_SDK
			+ "android/pay_score.do"; // 消耗积分
	public final static String SERVER_ADDSCORE = SERVER_SDK
			+ "android/activate.do"; // 增加积分
	public final static String SERVER_ADLIST = SERVER_SDK
			+ "android/ad_picker.do"; // 广告数据
	public final static String SERVER_DETAILS = SERVER_SDK
			+ "android/ad_detail.do"; // 详情信息
	public final static String SERVER_ACTIONLOG = SERVER_SDK
			+ "android/motion.do"; // 动作
	public static String userInfo = null; // 合作信息

	/**
	 * 广告类型
	 */
	public final static int PageTypeScore = 0; // 积分墙
	public final static int PageTypeHot = 1; // 推荐墙
	public final static int PAGE_TYPE_BANNER = 4; // 推广条
	public final static int PAGE_TYPE_PLAQUE = 5; // 插屏
	/**
	 * 图片大小
	 */
	public final static int BANNER_BANNER = 0; // 图片-推广条小(320*80)
	public final static int PLAQUE_CROSS = 2; // 图片-插屏横屏()
	public final static int PLAQUE_VERTICAL = 3; // 图片-插屏竖屏()

	/**
	 * 通知栏
	 */
	public final static String NOTIFY_DOWNLOADING_ID = "notify_dl_id"; // 广告下载ID
	public final static String NOTIFY_CLEAR_ACTION = "com.adwalker.wall.NOTIFY_CANCEL"; // 通知栏清除行为标识
	public final static String NOTIFY_CLEAR = "clear_notify"; // 通知栏清除单个标识
	public final static String NOTIFY_CLEAR_ID = "clear_notify_id"; // 通知栏清除ID
	public final static String NOTIFY_CLEAR_ALL = "clear_notify__all"; // 通知栏清除所有标识

	/**
	 * 下载
	 */
	public final static int DOWNLOAD_MAX = 3; // 最大同时下载数
	public final static String DOWNLOAD_PATH = "/XYDownloads/"; // 下载存放路经
	public final static String DOWNLOAD_DIR = ".res"; // 资源缓存图片存放路径
	// public static String IMAGE_PREFIX = ""; // 图片前缀

	/**
	 * 广告状态
	 */
	public final static int APP_UNDO = 0; // 未下载
	public final static int APP_DOWNLOADING = APP_UNDO + 1; // 下载中
	public final static int APP_DOWNLOADED = APP_UNDO + 2; // 下载完成
	public final static int APP_INSTALLED = APP_UNDO + 3; // 已安装

	/**
	 * 详情加载状态
	 */
	public final static int DETAIL_LOAD_SUCCESS = 0; // 详情加载成功
	public final static int DETAIL_LOAD_ERROR = 1; // 详情加载失败

	/**
	 * 错误信息
	 */
	public final static String NETWORK_NONE = "当前网络不可用, 请检查网络!";
	public final static int NETWORK_ERR = 404;
	public final static int NETWORK_INTERVAL = 1000;

	/**
	 * Log
	 */
	public final static String LOG_TAG = "[WalkerInfo]";
	public final static String LOG_ERR = "[WalkerErr]";
	public final static String LOG_PATH = "/WalkerDowns/.Log/";

	
	public final static String SHARED = "com.adwalker.wall.platform.SharedPreferences"; // SharedPreferences
	public final static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn"); // APN_URI

	/**
	 * 列表显示
	 */
	public static int ADLIST_TOTAL_NUM = 0; // 列表展示总数
	public static int ADLIST_TOTAL_PAGE = 0; // 列表展示总页数
	public final static int ADLIST_PAGE_SIZE = 10; // 列表展示每页个数
	public final static Integer GRID_LINE_NUMBER = 4; // grid每行显示个数

	/**
	 * 交互
	 */
	public final static int REQUESE_LEVEL = 2; // 执行次数
	public final static int REQUESE_INTERVAL = 3000; // 调度执行时间间隔

	/**
	 * 动作日志
	 */
	public final static int ACTION_DOWNLOADED = 0; // 动作-下载完成
	public final static int ACTION_OPEN = 1; // 动作-打开
	public final static int FORM_OPEN = 2; // 表单-打开
	public final static int BUTTON_OPEN = 4; // 4 CPC出下载按钮
	public final static int BUTTON_DOWM = 5; // 5点击下载，
	public final static int SHOW_SUCCESS = 6; // 展示成功，
	public final static int DETAIL_SHOW_SUCCESS = 7; //详情页展示成功，

	/**
	 * 跳转类型
	 */
	public final static int JUMP_TYPE_DETAIL = 0;// 跳转类型-详情
	public final static int JUMP_TYPE_WEB = 1;// 跳转类型-网页
	public final static int JUMP_TYPE_BUTTON = 2;// 跳转类型-按钮
	public final static int JUMP_TYPE_DOWN = 3;// 跳转类型-直接下载

	public final static int Download = 1;// 已经下载isDownload;//0:广告未被下载，1:被下载，2:一次签到，3:二次签到
	public final static int NOTDownload = 0;// 未下载

	public static WalkerAdBean DATA_WEB; // 进入网页的来源

	public static int sign_status; // 签到开关,0关闭，1一次签到，2次签到。
	public static int isSignIn = 0;// 0：获取积分墙，1获取签到列表
	
	public static AdWalkerListener adWalkerListener;// 0：获取积分墙，1获取签到列表

}
