package com.adwalker.wall.platform.network;

import org.json.JSONObject;
import android.content.Context;
import android.widget.Toast;
import com.adwalker.wall.init.AdWalkerListener;
import com.adwalker.wall.platform.AdConstants;
import com.adwalker.wall.platform.AdWalker;
import com.adwalker.wall.platform.bean.WalkerAdBean;
import com.adwalker.wall.platform.util.GuLogUtil;
import com.adwalker.wall.platform.util.MobileUtil;
import com.adwalker.wall.platform.util.GuUtil;

public class GuScoreManage {
	
	private  AdWalkerListener mListener = null;
	private  AdWalkerListener getListener = null;
	private  AdWalkerListener consumeListener = null;
	public static GuScoreManage instance = null;
	
	private GuScoreManage(){};
	/**
	 * 获取SDK实例
	 */
	public static GuScoreManage getInstance() {
		if (instance == null) {
			instance = new GuScoreManage();
		}
		return instance;
	}
	
	public  void registerListener (AdWalkerListener listener) {
	        this.mListener = listener;
	    }
	public  void getListener (AdWalkerListener listener) {
			this.getListener = listener;
    }
	public  void consumeListener (AdWalkerListener listener) {
			this.consumeListener = listener;
    }
	
	
	public  void getScore(final Context context,final AdWalkerListener adWalkerListener) {
		if (!MobileUtil.checkNetWork(context)) {
			Toast.makeText(context, "请检查您的网络...！", Toast.LENGTH_SHORT).show();
			return;
		}
		if(GuUtil.eqString(MobileUtil.getMobileId(context))){
			try {
				AdWalker.instance(new AdWalkerListener() {
					@Override
					public void initSucess() {
						new Thread(new MyRunnable(adWalkerListener, context, true, 0)).start();
					}
					@Override
					public void callFailed(int code, String error) {
						if(adWalkerListener!=null)
						adWalkerListener.callFailed(400, "查询失败!");
					}
				}).init(context,"","");
			} catch (Exception e) {
				if(adWalkerListener!=null)
				adWalkerListener.callFailed(400, "查询失败!");
			}
		
		}else {
			new Thread(new MyRunnable(adWalkerListener, context, true, 0)).start();
		}
	}
	
	public void consumeScore (final Context context,final AdWalkerListener adWalkerListener,final int consume_score){
		
		if (!MobileUtil.checkNetWork(context)) {
			Toast.makeText(context, "请检查您的网络...！", Toast.LENGTH_SHORT).show();
			return;
		}
		if(GuUtil.eqString(MobileUtil.getMobileId(context))){
			try {
				AdWalker.instance(new AdWalkerListener() {
					@Override
					public void initSucess() {
						new Thread(new MyRunnable(adWalkerListener, context, false, consume_score)).start();
					}
					@Override
					public void callFailed(int code, String error) {
						if(adWalkerListener!=null)
							adWalkerListener.callFailed(400, "消耗失败!");
					}
				}).init(context,"","");
			} catch (Exception e) {
				if(adWalkerListener!=null)
					adWalkerListener.callFailed(400, "消耗失败!");
			}
		} else {
			new Thread(new MyRunnable(adWalkerListener, context, false, consume_score)).start();
		}
		
	
		
	}
	
	/**
	 * 查询积分
	* <p>Title: getScoreFromServer</p>
	* <p>Description:TODO</p>
	* @param context
	* @author caiqiang
	* @date 2013-8-22
	* @return void
	* @version 1.0
	 */
	public  void getScoreFromServer(Context context) {
		String codeStr = "uuid=" + MobileUtil.getMobileId(context);
		String serverUrl = AdConstants.SERVER_GETSCORE;
		byte[] bytes = GuHttpNetwork.statisticsFromServer(context, serverUrl,
				codeStr);
		if (bytes == null) {
			return;
		}
		try {
			JSONObject returnJson = GuUtil.Bytes2Json(bytes);
			String status = returnJson.getString("status");
			JSONObject data = returnJson.getJSONObject("data");
			if (data != null) {
				if (status.equalsIgnoreCase("ok")) {
					int score = data.getInt("score");
					String unit = data.getString("unit");
					if(getListener!=null){
						getListener.getSucess(score, unit);
					}
				
				} else if (status.equalsIgnoreCase("error")) {
					int code = data.getInt("code");
					String message = data.getString("message");

					if (message != null && !message.trim().equals("")) {
						if(getListener!=null){
							getListener.callFailed(code, message);
						}
					}
				}
			}
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "getscoreErr: ",e.fillInStackTrace());
		}
	}

	public void consumeScoreFromServer(Context context, int consume_score) {
		if (!MobileUtil.checkNetWork(context)) {
			consumeListener.callFailed(AdConstants.NETWORK_ERR, AdConstants.NETWORK_NONE);
			return;
		}
		String codeStr = "uuid=" + MobileUtil.getMobileId(context) + "&consumeScore=" + consume_score;
		String serverUrl = AdConstants.SERVER_CONSUMESCORE;

		byte[] bytes = GuHttpNetwork.statisticsFromServer(context, serverUrl,
				codeStr);
		if (bytes == null) {
			return;
		}
		try {
			JSONObject returnJson = GuUtil.Bytes2Json(bytes);
			String status = returnJson.getString("status");
			JSONObject data = returnJson.getJSONObject("data");
			if (data != null) {
				if (status.equalsIgnoreCase("ok")) {
					int score = data.getInt("score");
					int updateScore = data.getInt("updateScore");
					String unit = data.getString("unit");
					if (consumeListener != null){
						consumeListener.consumeSucess( score, updateScore,unit);
					}
				} else if (status.equalsIgnoreCase("error")) {
					int code = data.getInt("code");
					String message = data.getString("message");

					if (message != null && !message.trim().equals("")) {
						if (consumeListener != null){
							consumeListener.callFailed( code, message);
						}
					}
				}
			}
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "consumeScoreErr: " + e);
		}
	}

	public  void addScoreFromServer(Context context, WalkerAdBean wallInfo) {
		String codeStr = "uuid=" + MobileUtil.getMobileId(context) + "&id=" + wallInfo.id + "&pageType="
				+ wallInfo.page_type + "&bannerTag=" + wallInfo.bannerTag+ "&devUserId=" + AdConstants.userInfo;;
		String serverUrl = AdConstants.SERVER_ADDSCORE;
		byte[] bytes = GuHttpNetwork.statisticsFromServer(context, serverUrl,codeStr);
		if (bytes == null) {
			return;
		}
		try {
			JSONObject returnJson = GuUtil.Bytes2Json(bytes);
			String status = returnJson.getString("status");
			JSONObject data = returnJson.getJSONObject("data");
			if (data != null) {
				if (status.equalsIgnoreCase("ok")) {
					int score = data.getInt("score");//总分
					int updateScore = data.getInt("updateScore");
					String unit = data.getString("unit");
					if (mListener != null){
						mListener.AdActivating( updateScore, score, unit);
					}
				} else if (status.equalsIgnoreCase("error")) {
					int code = data.getInt("code");
					String message = data.getString("message");

					if (message != null && !message.trim().equals("")) {
						if (mListener != null){
							mListener.callFailed(code, message);
						}
					}
				}
			}
		} catch (Exception e) {
			if (mListener != null && wallInfo.page_type == 0){
				mListener.callFailed(410, "重复激活.....");
			}
		}
	}
	
	
	class MyRunnable implements Runnable{
		private boolean scoreTag;
		private int consume_score;
		private AdWalkerListener madWalkerListener;
		private Context mcontext;
		public MyRunnable(AdWalkerListener adWalkerListener,Context context,boolean scoreTag,int consume_score){
			this.scoreTag = scoreTag;
			this.consume_score = consume_score;
			this.madWalkerListener = adWalkerListener;
			this.mcontext = context;
		}
		
		@Override
		public void run() {
			if(scoreTag){//查询积分
				getListener(madWalkerListener);
				getScoreFromServer(mcontext);
			}else{//消费积分
				consumeListener(madWalkerListener);
				consumeScoreFromServer(mcontext, consume_score);
			}
			
		}
		
	}
}
