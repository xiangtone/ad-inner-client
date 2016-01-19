package com.adwalker.wall.platform.network;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.adwalker.wall.platform.util.GuLogUtil;


public class GuDownLoadTask extends AsyncTask<Void, Void, Void> implements Observer{
	private static final String TAG = "GuDownLoadTask";
	public GuDownloadInfo downLoadInfo;
	public static int time;
	private Context context;
	private String indexValue;
	private String catagory;
	private Handler handler;
	
	public GuDownLoadTask(GuDownloadInfo downLoadInfo,Context context){
		if (downLoadInfo==null) {
			throw new NullPointerException("downloadInfo cannot be null");
		}
		GuDownLoadManager.getInstance().addObserver(this);
		this.downLoadInfo = downLoadInfo;
		this.context = context;
	}
	
	public GuDownLoadTask(GuDownloadInfo downLoadInfo,Context context,String indexValue,String catagory,Handler handler){
		this.indexValue = indexValue;
		this.catagory = catagory;
		this.handler = handler;
		if (downLoadInfo==null) {
			throw new NullPointerException("downloadInfo cannot be null");
		}
		GuDownLoadManager.getInstance().addObserver(this);
		this.downLoadInfo = downLoadInfo;
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected void onPostExecute(Void result) {
		if (GuDownLoadManager.getInstance().impl!=null) {
			if (downLoadInfo.exception == null) {
				GuDownLoadManager.getInstance().impl.notifyDownloadFinish(downLoadInfo); 
			}else {
				if (context!=null) {
					if(handler != null){
						Message msg = new Message();
						try{
							msg.arg1 = Integer.parseInt(indexValue);
							msg.arg2 = Integer.parseInt(catagory);
						}catch(Exception e){
							msg.arg1 = 0;
							msg.arg2 = 0;
						}
						msg.what = 11;
						handler.sendMessage(msg);
					}
					Toast.makeText(context, downLoadInfo.mName+" 下载异常", Toast.LENGTH_SHORT).show();
				}
				GuDownLoadManager.getInstance().impl.notifyDownloadError(downLoadInfo);
			}
		}
		GuDownLoadManager.getInstance().delDownTask(downLoadInfo);
		super.onPostExecute(result);
	}
	@Override
	protected Void doInBackground(Void... voids) {

		BufferedInputStream bis = null;
		RandomAccessFile out = null;
		HttpURLConnection conn = null;
		HttpURLConnection lenConn = null;//用于获取长度的HttpURLConnection
		long hasDown = 0;
		
		try {
			File file = new File(downLoadInfo.mDestination,downLoadInfo.mFileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			URL url = new URL(downLoadInfo.mUrl);
			String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();
            String path = url.getPath();
            //因为 +  符号在java是关键字符需要转义，不能直接用
            // %20  为空格的编码，这里替换掉，URL才不会报错
            String urlString =  (new StringBuilder(String.valueOf(protocol)))
            		.append("://").append(host).append(port == -1 ? "" : ((Object) (Integer.valueOf(port))))
            		.append("/").append(URLEncoder.encode(path,"UTF-8").replaceAll("\\+","%20")).toString(); 
        	hasDown = file.length();//filelength文件长度
	        lenConn = (HttpURLConnection) new URL(urlString).openConnection();  
	        if(lenConn.getContentLength() >= downLoadInfo.resourceSize){
	        	downLoadInfo.mTotalBytes = lenConn.getContentLength();
	        }else{
	        	downLoadInfo.mTotalBytes = downLoadInfo.resourceSize;
	        }
	    	lenConn.disconnect();
			if (hasDown!=0&&hasDown!=downLoadInfo.mTotalBytes) {//文件有问题
				file.delete();
				file.createNewFile();
				hasDown = 0;
			}
		    if (hasDown==downLoadInfo.mTotalBytes) {
		    	downLoadInfo.mCurrentBytes = hasDown;//设置下载进度
	    		downLoadInfo.mPercentage = hasDown/(double)downLoadInfo.mTotalBytes;//设置下载进度百分比
	    		//add by zgb
//	    		if(handler != null){
//	    			if(downLoadInfo.mPercentage > 1.0){
//	    				downLoadInfo.mPercentage = 1.0;
//	    			}
//		    		Message msg = new Message();
//		    		try{
//						msg.arg1 = Integer.parseInt(indexValue);
//						msg.arg2 = Integer.parseInt(catagory);
//					}catch(Exception e){
//						msg.arg1 = 0;
//						msg.arg2 = 0;
//					}
//					msg.what = 12;
//					handler.sendMessage(msg);
//	    		}
			}else{
				out = new RandomAccessFile(file, "rw");
				conn = GuNetworkUtil.getConnection(context, urlString, GuNetworkUtil.METHOD_GET);
//	            conn.setRequestProperty("RANGE", "bytes=" + hasDown + "-"); // 设置获取数据的范围  
	            InputStream in = conn.getInputStream();  
	        	bis = new BufferedInputStream(in);
	            byte[] buffer = new byte[1024];  
	            int len = 0;  
//	            out.seek(hasDown);  
				downLoadInfo.mCurrentBytes = hasDown;//设置下载进度
				downLoadInfo.mPercentage = hasDown/(double)downLoadInfo.mTotalBytes;//设置下载进度百分比
		          while ((len = bis.read(buffer)) != -1) {  
		            	out.write(buffer, 0, len);  
		            	hasDown += len;  
		            	downLoadInfo.mCurrentBytes = hasDown;//设置下载进度
						downLoadInfo.mPercentage = hasDown/(double)downLoadInfo.mTotalBytes;//设置下载进度百分比
						//add by zgb
//						if(handler != null){
//							if(downLoadInfo.mPercentage > 1.0){
//								downLoadInfo.mPercentage = 1.0;
//							}
//							Message msg = new Message();
//							try{
//								msg.arg1 = Integer.parseInt(indexValue);
//								msg.arg2 = Integer.parseInt(catagory);
//							}catch(Exception e){
//								msg.arg1 = 0;
//								msg.arg2 = 0;
//							}
//							msg.obj = String.valueOf(downLoadInfo.mPercentage);
//							msg.what = 13;
//							
//							handler.sendMessage(msg);
//						}
		            } 
			}
		} catch (MalformedURLException e) {
			GuLogUtil.e(TAG, e.getMessage(), e);
			downLoadInfo.exception = e;
		} catch (IOException e) {
			GuLogUtil.e(TAG, e.getMessage(), e);
			downLoadInfo.exception = e;
		}finally{
			if (bis!=null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (lenConn!=null) {
				lenConn.disconnect();
			}
			if (conn!=null) {
				conn.disconnect();
			}
		}
		return null;
	}
		
	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof GuDownloadInfo) {
			GuDownloadInfo downLoadInfo = (GuDownloadInfo) data;
			if (downLoadInfo==this.downLoadInfo) {
				this.execute();
			}
		}
	}


}
