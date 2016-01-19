package com.adwalker.wall.platform.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Environment;

import com.adwalker.wall.platform.AdConstants;
import com.adwalker.wall.platform.network.GuHttpNetwork;

public class ImageLoadUtil {
	/**
	 * 检查是否有SD卡 参数
	 */
	public static Boolean hasSD() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return true;
		} else
			return false;
	}

	/**
	 * 获取下载的目录
	 */
	public static String getDownloadDir(Context context) {
		Boolean isSD = hasSD();
		if (isSD) {
			return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
					+ AdConstants.DOWNLOAD_PATH;
		} else{
			return Environment.getDataDirectory().getAbsolutePath()
					+ File.separator + "data" + File.separator
					+ context.getPackageName() + AdConstants.DOWNLOAD_PATH;
		}
	}
	
	
	/**
	 * 获取下载的目录
	 */
	public static String getFileDir(Context context) {
		Boolean isSD = hasSD();
		if (isSD) {
			return Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator
					+ "js";
		} else{
			return Environment.getDataDirectory().getAbsolutePath()
					+ File.separator + "data" + File.separator
					+ context.getPackageName();
		}
	}

	public static Drawable getDrawable(Context context, String imgUrl,
			String imgName) {
		try{
			Bitmap bitmap =null;
			if (checkIconUrlValidity(imgUrl)) {
				bitmap = getLocalImg(context, imgUrl);
				if (bitmap == null) {
					if (saveImg(context, imgUrl)) {
						bitmap = getLocalImg(context, imgUrl);
						return new BitmapDrawable(bitmap);
					} else {
						return null;
					}
				} else {
					return new BitmapDrawable(bitmap);
				}
			} else {
				return null;
			}
		}catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 加载图片 srcName包括扩展名的文件名，如A.png
	 */
	public static final GradientDrawable WALL_TOP_BACKGROUND = new GradientDrawable(Orientation.TOP_BOTTOM,new int[]{Color.parseColor("#303030"),Color.parseColor("#404040"),Color.parseColor("#303030")});
	private static Bitmap pointDown;
	private static Bitmap pointNormal;
	public static Bitmap getPointDownBitmap(Context context){
		if (pointDown==null) {
			Bitmap bitmap = Bitmap.createBitmap(MobileUtil.dip2px(context, 10), MobileUtil.dip2px(context,10), Bitmap.Config.ARGB_4444);
			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			paint.setStyle(Style.FILL_AND_STROKE);
			paint.setAntiAlias(true);
			paint.setColor(Color.GRAY);
			RectF rectf = new RectF(0F, 0F, MobileUtil.dip2px(context, 10), MobileUtil.dip2px(context, 10));
			canvas.drawOval(rectf, paint);
			pointDown = bitmap;
		}else {
			return pointDown;
		}
		return pointDown;
	}
	public static Bitmap getPointNormalBitmap(Context context){
		if (pointNormal==null) {
			Bitmap bitmap = Bitmap.createBitmap(MobileUtil.dip2px(context, 10), MobileUtil.dip2px(context,10), Bitmap.Config.ARGB_4444);
			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			paint.setStyle(Style.FILL_AND_STROKE);
			paint.setAntiAlias(true);
			paint.setColor(Color.RED);
			RectF rectf = new RectF(0F, 0F, MobileUtil.dip2px(context, 10), MobileUtil.dip2px(context, 10));
			canvas.drawOval(rectf, paint);
			pointNormal = bitmap;
		}else {
			return pointNormal;
		}
		return pointNormal;
	}
	
	/**
	 * 检测图标URL
	 */
	private static boolean checkIconUrlValidity(String url) {
		if (url != null && !url.equals("null") && !url.contains("/null")
				&& !url.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * 从本地获取图片
	 */
	private static Bitmap getLocalImg(Context context, String url) {
		try {
			Bitmap bitmap =null;
			File file = new File(getDownloadDir(context) + AdConstants.DOWNLOAD_DIR
					+ File.separator + URLEncoder.encode(url.substring(url.lastIndexOf("/")+1)));
			if (file.exists() && file.isFile()) {
				bitmap = BitmapFactory.decodeFile(file.getPath());
				return bitmap;
			} else {
				return null;
			}
		} catch (OutOfMemoryError e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "getLocalImg: " + e);
			return null;
		}
	}

	//读文件在./data/data/com.tt/files/下面
	 
	   public String readFileData(String fileName,Context context){ 
	 
	        String res=""; 
	 
	        try{ 
	 
	         FileInputStream fin = context.openFileInput(fileName); 
	 
	         int length = fin.available(); 
	 
	         byte [] buffer = new byte[length]; 
	 
	         fin.read(buffer);     
	 
	         res = EncodingUtils.getString(buffer, "UTF-8"); 
	 
	         fin.close();     
	 
	        } 
	 
	        catch(Exception e){ 
	 
	         e.printStackTrace(); 
	 
	        } 
	 
	        return res; 
	 
	    }   

	
	/**
	 * 保存网络图片到本地
	 */
	private static boolean saveImg(Context context, String url) {
		try {
			String dir = getDownloadDir(context) + AdConstants.DOWNLOAD_DIR
					+ File.separator;
			File file = new File(dir + URLEncoder.encode(url.substring(url.lastIndexOf("/")+1)));
			if (file.exists()) {
				return true;
			} else {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
			}
			HttpGet request = new HttpGet(url);
			byte[] dataFromServer = GuHttpNetwork.querryResByHttp(context,request);
			if (dataFromServer != null && dataFromServer.length > 0) {
				file.createNewFile();
				FileOutputStream fos = null;
				BufferedOutputStream Buff = null;
				try {
					fos = new FileOutputStream(file);
					Buff = new BufferedOutputStream(fos);// 缓冲
					Buff.write(dataFromServer);
					return true;
				} catch (Exception e) {
					GuLogUtil.e(AdConstants.LOG_ERR,
							"image: " + e);
				}
				catch (OutOfMemoryError e) {
					GuLogUtil.e(AdConstants.LOG_ERR,
							"image: " + e);
				}finally {
					if (Buff != null) {
						try {
							Buff.close();
						} catch (Exception e) {
							GuLogUtil.e(AdConstants.LOG_ERR,
									"image: " + e);
						}
						catch (OutOfMemoryError e) {
							GuLogUtil.e(AdConstants.LOG_ERR,
									"image: " + e);
						}
					}
					if (fos != null) {
						try {
							fos.close();
						} catch (Exception e) {
							GuLogUtil.e(AdConstants.LOG_ERR,
									"image: " + e);
						}
						catch (OutOfMemoryError e) {
							GuLogUtil.e(AdConstants.LOG_ERR,
									"image: " + e);
						}

					}
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR,
					"image: " + e);
			return false;
		}
		catch (OutOfMemoryError e) {
			GuLogUtil.e(AdConstants.LOG_ERR,
					"image: " + e);
			return false;
		}
	}
	
	
	/**
	 * 保存网络图片到本地
	 */
	public static  InputStream saveGifImg(Context context, String url) {

		try {
			String dir = getDownloadDir(context) + AdConstants.DOWNLOAD_DIR
					+ File.separator;
			File file = new File(dir + URLEncoder.encode(url.substring(url.lastIndexOf("/")+1)));
			if (file.exists() && file.isFile()) {
				  FileInputStream f = null;
				  	try {
				  		f = new FileInputStream(dir + URLEncoder.encode(url.substring(url.lastIndexOf("/")+1)));
//				  		f = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/aaa.gif");
				  	} catch (FileNotFoundException e) {
				  		// TODO Auto-generated catch block
				  		e.printStackTrace();
				  	}
				         byte[] b = null;
				  	try {
				  		b = new byte[f.available()];
				  	} catch (IOException e) {
				  		// TODO Auto-generated catch block
				  		e.printStackTrace();
				  	}
				         try {
				  		f.read(b);
				  	} catch (IOException e) {
				  		// TODO Auto-generated catch block
				  		e.printStackTrace();
				  	}
				
				return FormatTools.Byte2InputStream(b);
//				return new FileInputStream(file.getPath());
			} else {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
			}
			HttpGet request = new HttpGet(url);
			byte[] dataFromServer = GuHttpNetwork.querryResByHttp(context,request);
			if (dataFromServer != null && dataFromServer.length > 0) {
				file.createNewFile();
				FileOutputStream fos = null;
				BufferedOutputStream Buff = null;
				try {
					fos = new FileOutputStream(file);
					Buff = new BufferedOutputStream(fos);// 缓冲
					Buff.write(dataFromServer);
					return FormatTools.Byte2InputStream(dataFromServer);
				} catch (Exception e) {
					GuLogUtil.e(AdConstants.LOG_ERR,
							"image: " + e);
				}
				catch (OutOfMemoryError e) {
					GuLogUtil.e(AdConstants.LOG_ERR,
							"image: " + e);
				}finally {
					if (Buff != null) {
						try {
							Buff.close();
						} catch (Exception e) {
							GuLogUtil.e(AdConstants.LOG_ERR,
									"image: " + e);
						}
						catch (OutOfMemoryError e) {
							GuLogUtil.e(AdConstants.LOG_ERR,
									"image: " + e);
						}
					}
					if (fos != null) {
						try {
							fos.close();
						} catch (Exception e) {
							GuLogUtil.e(AdConstants.LOG_ERR,
									"image: " + e);
						}
						catch (OutOfMemoryError e) {
							GuLogUtil.e(AdConstants.LOG_ERR,
									"image: " + e);
						}
					}
				}
				return FormatTools.Byte2InputStream(dataFromServer);
			} else {
				return null;
			}
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR,
					"image: " + e);
			return null;
		}
		catch (OutOfMemoryError e) {
			GuLogUtil.e(AdConstants.LOG_ERR,
					"image: " + e);
			return null;
		}
	
	}
}
