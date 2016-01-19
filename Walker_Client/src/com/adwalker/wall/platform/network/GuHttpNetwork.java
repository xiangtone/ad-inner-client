package com.adwalker.wall.platform.network;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.adwalker.wall.init.AdInitialization;
import com.adwalker.wall.platform.AdConstants;
import com.adwalker.wall.platform.util.GuDes3;
import com.adwalker.wall.platform.util.GuLogUtil;
import com.adwalker.wall.platform.util.MobileUtil;
import com.adwalker.wall.platform.util.GuUtil;

/**
 * 该类负责与服务器通信，包括(http,ftp协议)
 */
public class GuHttpNetwork {
	private static String CHARSET = HTTP.UTF_8;
	private static HttpClient customerHttpClient = null;

	/**
	 * http通信，获取返回结果byte[],适用于单次提交请求并返回结果
	 */
	public static byte[] querryResByHttp(Context context,HttpGet httpGet) {
		byte[] result = null;
		HttpEntity entity = null;
		try {
			HttpClient httpClient = getHttpClient();
			httpClient.getParams().getIntParameter(
					HttpConnectionParams.CONNECTION_TIMEOUT, 5000);
			httpClient.getParams().getIntParameter(
					HttpConnectionParams.SO_TIMEOUT, 6000);
			HttpResponse response = excuteHttpRequest(context,httpClient, httpGet,
					null, true);
			if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				entity = response.getEntity();
				if (entity != null) {
					result = EntityUtils.toByteArray(entity);
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "netERR: "+e.fillInStackTrace());
		}
		return result;
	}

	/**
	 * 获取服务器返回数据
	 */
	public  static byte[] dataFromServer(Context context,
			String url, String codeStr) {
		
		int level = AdConstants.REQUESE_LEVEL;
		byte[] bytesFromServer = null;
		try {
			
			codeStr = codeStr + "&appkey=" + MobileUtil.getAPP_KEY(context) + "&channel="
					+ MobileUtil.getAPP_CHANNEL(context)+"&imsi="+ MobileUtil.getImsi(context)+ "&version=" + AdConstants.WALKER_VERSION;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("m", GuDes3.encode(codeStr)));
			GuLogUtil.i(AdConstants.LOG_TAG, "code:"+ codeStr);
			//url = http://121.40.137.243:8020/DemoTest/receive.jsp
			
			HttpPost request = new HttpPost(url);
			
			Log.e("         ++ url ", url);
			Log.e("         ++ codeStr ", codeStr);
			
			
			do {
				bytesFromServer = requestByHttpPost(context,request, nameValuePairs);
				if (bytesFromServer == null) {
					SystemClock.sleep(AdConstants.REQUESE_INTERVAL);
					level--;
				} else {
					GuLogUtil.i(AdConstants.LOG_TAG, "json:"+ GuUtil.Bytes2Json(bytesFromServer));
					GuLogUtil.i(AdConstants.LOG_TAG, "--------------------------------------------- ");
					return bytesFromServer;
				}
			} while (level > 0);

		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "netERRT:  " + e.fillInStackTrace());
		}
		return bytesFromServer;
	}

	public  static byte[] statisticsFromServer(Context context,
			String url, String codeStr) {

		int level = AdConstants.REQUESE_LEVEL;
		byte[] bytesFromServer = null;
		try {
			codeStr = codeStr + "&version=" + AdConstants.WALKER_VERSION
					+ "&appkey=" + MobileUtil.getAPP_KEY(context) + "&channel="
					+ MobileUtil.getAPP_CHANNEL(context)+"&imsi="+ MobileUtil.getImsi(context);
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("m",  GuDes3.encode(codeStr)));
			GuLogUtil.i(AdConstants.LOG_TAG,"code2: "+ codeStr);
			
			HttpPost request = new HttpPost(url);
			do {
				bytesFromServer = requestByHttpPost(context,request, nameValuePairs);
				if (bytesFromServer == null) {
					SystemClock.sleep(AdConstants.REQUESE_INTERVAL);
					level--;
				} else {
					GuLogUtil.i(AdConstants.LOG_TAG,"json2: "+ GuUtil.Bytes2Json(bytesFromServer));
					GuLogUtil.i(AdConstants.LOG_TAG,"---------------------------------------------");
					return bytesFromServer;
				}
			} while (level > 0);

		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "network: "+e.fillInStackTrace());
		}
		return bytesFromServer;
	}

	/**
	 * http通信，获取返回结果byte[],适用于单次提交请求并返回结果
	 */
	private static byte[] requestByHttpPost(Context context,HttpPost httpPost,
			List<NameValuePair> nameValuePairs) {
		byte[] result = null;
		HttpEntity entity = null;
		try {
			HttpClient httpClient = getHttpClient();
			httpClient.getParams().getIntParameter(
					HttpConnectionParams.CONNECTION_TIMEOUT, 8000);
			httpClient.getParams().getIntParameter(
					HttpConnectionParams.SO_TIMEOUT, 9000);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = excuteHttpRequest(context,httpClient, null,
					httpPost, false);
			if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				entity = response.getEntity();
				if (entity != null) {
					result = EntityUtils.toByteArray(entity);
					entity.consumeContent();
				} else {
					return new byte[] {};
				}
			}
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "netPostERR: "+e.fillInStackTrace());
		}
		return result;
	}

	/**
	 * 提交request，并返回HttpEntity
	 */
	private static HttpResponse excuteHttpRequest(Context context,HttpClient httpClient,
			HttpGet httpGet, HttpPost httpPost, boolean isHttpGet) {
		HttpResponse response = null;
		try {
			// 如果用户用cmwap连接网络，修改url前缀
			if (MobileUtil.isUsingCmwap(context)) {
				String originalUrl = "";
				originalUrl = (isHttpGet == true ? httpGet.getURI().toURL()
						.toString() : httpPost.getURI().toURL().toString());
				String wapUrl = getWapUrl(originalUrl);
				String host = getHost(originalUrl);
				if (isHttpGet) {
					httpGet.setURI(URI.create(wapUrl));
					httpGet.setHeader("X-Online-Host", host);
				} else {
					httpPost.setURI(URI.create(wapUrl));
					httpPost.setHeader("X-Online-Host", host);
				}
			}
			if (isHttpGet) {
				response = httpClient.execute(httpGet);
			} else {
				response = httpClient.execute(httpPost);
			}
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() != HttpStatus.SC_OK
					&& status.getStatusCode() != HttpStatus.SC_PARTIAL_CONTENT) {
				try {
					new String(EntityUtils.toByteArray(response.getEntity()));
				} catch (Exception e) {
					GuLogUtil.e(AdConstants.LOG_ERR, "netERR1: "
							,e.fillInStackTrace());
				}
				return null;
			}
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "netERR2: " , e.fillInStackTrace());
			if (customerHttpClient != null && customerHttpClient.getConnectionManager() != null) {
				customerHttpClient.getConnectionManager().shutdown();
				customerHttpClient = null;
			}
		}
		return response;
	}

	public void finalize() {
		if (customerHttpClient != null && customerHttpClient.getConnectionManager() != null) {
			customerHttpClient.getConnectionManager().shutdown();
			customerHttpClient = null;
		}
	}

	private static String getHost(String url) {
		if (url == null || url.equals(""))
			return null;
		url = url.replaceFirst("http://", "");
		return url.substring(0, url.indexOf("/"));
	}
	
	private static String getWapUrl(String url) {
		if (url == null || url.equals(""))
			return null;
		url = url.replaceFirst("http://", "");
		url = url.substring(url.indexOf("/"), url.length());
		return "http://10.0.0.172" + url;
	}
	
	
	private synchronized static HttpClient getHttpClient() {
		if (null == customerHttpClient) {
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpClientParams.setRedirecting(params, true);
			ConnManagerParams.setTimeout(params, 2500);
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
					params, schReg);
			customerHttpClient = new DefaultHttpClient(conMgr, params);
		}
		return customerHttpClient;
	}
}
