package com.adwalker.wall.init;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.adwalker.wall.platform.AdConstants;
import com.adwalker.wall.platform.bean.AppPackageBean;
import com.adwalker.wall.platform.network.GuHttpNetwork;
import com.adwalker.wall.platform.util.AdDbBase;
import com.adwalker.wall.platform.util.GuLogUtil;
import com.adwalker.wall.platform.util.MobileUtil;
import com.adwalker.wall.platform.util.GuUtil;

@SuppressLint("SimpleDateFormat")
public class AdInitModel {
	
	private static AdInitModel instance = null;
	private AdWalkerListener mListener = null;
	
	public void registerListener (AdWalkerListener listener) {
	        this.mListener = listener;
	    }
	
	/**
	 * 不允许应用创建引擎实例
	 */
	private AdInitModel() {
		super();
	}

	public static AdInitModel getInstance() {
		if (instance == null) {
			instance = new AdInitModel();
		}
		return instance;
	}
	/**
	* <p>Title: initializeFromServer</p>
	* <p>Description:初始化</p>
	* @param context
	* @return
	* @author 
	* @date 2013-1-23
	* @return(true:成功,false:失败)
	* @version 1.0
	 */
	public  boolean initializeFromServer(Context context) {
		if (!MobileUtil.checkNetWork(context)) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(AdConstants.SHARED, Context.MODE_PRIVATE);
		SimpleDateFormat formatter = new  SimpleDateFormat("yyyy-MM-dd");      
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间      
		String newDate = formatter.format(curDate); 
		String date = sp.getString("XYDATA","");
		String uuidTag = sp.getString("uuid","");
		//一天初始化一次
		if(!uuidTag.equals("")){
			 if(!date.equals(newDate)){
				 sp.edit().putString("XYDATA",newDate).commit();
			}else{
				if(mListener!=null){
					mListener.initSucess();
				}
				return true;
			}
		}else{
			 sp.edit().putString("XYDATA",newDate).commit();
		}
		String codeStr = MobileUtil.getRegisteredCode(context)+ "&mac=" + MobileUtil.getMac(context);
		String serverUrl = AdConstants.SERVER_INITIALIZE;
		byte[] bytes = GuHttpNetwork.dataFromServer(context, serverUrl,codeStr);
		if (bytes == null) {
			return false;
		}
		try {
			JSONObject returnJson = GuUtil.Bytes2Json(bytes);
			String status = returnJson.getString("status");
			JSONObject data = returnJson.getJSONObject("data");
			if (data != null) {
				if (status.equalsIgnoreCase("ok")) {
					String uuid = data.getString("uuid");
					if(MobileUtil.getMobileId(context)==null||MobileUtil.getMobileId(context).equals("")){
						sp.edit().putString("uuid", uuid).commit();
					}
					if(mListener!=null){
						mListener.initSucess();
					}
					return true;
				}
			}
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "initServierErr: ",e.fillInStackTrace());
		}
		return false;
	}
	
	public  void softListFromServer(Context context,List<AppPackageBean> softList, String softString) {
		if (!MobileUtil.checkNetWork(context)) {
			return;
		}
		String codeStr = "uuid=" +MobileUtil.getMobileId(context) + "&softList=" + softString;
		String serverUrl = AdConstants.SERVER_SOFTLIST;
		byte[] bytes = GuHttpNetwork.statisticsFromServer(context, serverUrl,codeStr);
		if (bytes == null) {
			return;
		}
		try {
			JSONObject returnJson = GuUtil.Bytes2Json(bytes);
			String status = returnJson.getString("status");
			if (status.equalsIgnoreCase("ok")) {
				AdDbBase dbHelper = new AdDbBase(context);
				dbHelper.mdb = dbHelper.getWritableDatabase();
				for (int i = 0; i < softList.size(); i++) {
					AppPackageBean info = softList.get(i);
					AdDbBase.Insert(dbHelper, info.appName,
							info.packageName);
				}
				dbHelper.mdb.close();
				dbHelper.mdb= null;
				dbHelper.close();
			}
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "softListFromServer: " + e);
		}
	}
}
