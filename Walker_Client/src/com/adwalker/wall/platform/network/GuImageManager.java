package com.adwalker.wall.platform.network;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.client.methods.HttpGet;

import com.adwalker.wall.platform.AdConstants;
import com.adwalker.wall.platform.util.GuLogUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public enum GuImageManager {  
     INSTANCE;  
     private final Map<String, SoftReference<Bitmap>> cache;  
     private final ExecutorService pool;  
     private Map<ImageView, String> imageViews = Collections  
             .synchronizedMap(new WeakHashMap<ImageView, String>());  
     private Bitmap placeholder;  
     	GuImageManager() { 
         cache = new HashMap<String, SoftReference<Bitmap>>();  
         pool = Executors.newFixedThreadPool(3);  
     }  
   
     public void setPlaceholder(Bitmap bmp) {  
         placeholder = bmp;  
     }  
   
     public Bitmap getBitmapFromCache(String url) {  
         if (cache.containsKey(url)) {  
             return cache.get(url).get();  
         }  
   
         return null;  
     }  
   
     
     
 	/**
 	 * 获取下载的目录
 	 */
 	private  String getDownloadDir(Context context) {
 		Boolean isSD = hasSD();
 		if (isSD) {
 			return Environment.getExternalStorageDirectory().getAbsolutePath()
 					+ AdConstants.DOWNLOAD_PATH;
 		} else
 			return Environment.getDataDirectory().getAbsolutePath()
 					+ File.separator + "data" + File.separator
 					+ context.getPackageName() + AdConstants.DOWNLOAD_PATH;
 	}
 	
	/**
	 * 检查是否有SD卡 参数
	 */
 	private  Boolean hasSD() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return true;
		} else
			return false;
	}
 	
     
     public void queueJob(final Context context,final String url, final ImageView imageView,  
             final int width, final int height) {  
         /* Create handler in UI thread. */  
         final Handler handler = new Handler() {  
             @Override  
             public void handleMessage(Message msg) {  
                 String tag = imageViews.get(imageView);  
                 if (tag != null && tag.equals(url)) {  
                     if (msg.obj != null) {  
                         imageView.setImageBitmap((Bitmap) msg.obj);  
                     } else {  
                         imageView.setImageBitmap(placeholder);  
//                         Log.d(null, "fail " + url);  
                     }  
                 }  
             }  
         };  
   
         pool.submit(new Runnable() {  
             @Override  
             public void run() {  
                 final Bitmap bmp = downloadBitmap(context,url, width, height);  
                 Message message = Message.obtain();  
                 message.obj = bmp;  
//                 Log.d(null, "Item downloaded: " + url);  
   
                 handler.sendMessage(message);  
             }  
         });  
     }  
   
     public void loadBitmap(Context context, final String url, final ImageView imageView,  
             final int width, final int height) {  
         imageViews.put(imageView, url);  
         Bitmap bitmap = getBitmapFromCache(url);  
         if(bitmap ==null){
        	 bitmap = getLocalImg(context, url);
         }
         // check in UI thread, so no concurrency issues  
         if (bitmap != null) {  
//             Log.d(null, "Item loaded from cache: " + url);  
             imageView.setImageBitmap(bitmap);  
         } else {  
             imageView.setImageBitmap(placeholder);  
             queueJob(context,url, imageView, width, height);  
         }  
     }  
   
     private Bitmap downloadBitmap(Context context,String url, int width, int height) {  
         try { 
        	 HttpGet request = new HttpGet(url);
 			byte[] dataFromServer = GuHttpNetwork.querryResByHttp(context,request);
 			saveImg(context, dataFromServer,url);
            Bitmap bitmap = BitmapFactory.decodeByteArray(dataFromServer, 0,dataFromServer.length );  
//          Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(  
//            		 url).getContent());  
//          bitmap = Bitmap.createBitmap(bitmap);  
            if(width>0){
            	 bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);  
            }
            if(width<100){
            	 cache.put(url, new SoftReference<Bitmap>(bitmap));  
            }
             return bitmap;  
         } catch (Exception e) {
//        	  e.printStackTrace();  
		}
//      catch (MalformedURLException e) {  
//             e.printStackTrace();  
//         } catch (IOException e) {  
//             e.printStackTrace();  
//         }  
         return null;  
     }  
     
 	/**
 	 * 从本地获取图片
 	 */
 	private  Bitmap getLocalImg(Context context, String url) {
 		try {
 			Bitmap bitmap =null;
 			File file = new File(getDownloadDir(context) + AdConstants.DOWNLOAD_DIR
 					+ File.separator + URLEncoder.encode(url.substring(url.lastIndexOf("/")+1)));
 			if (file.exists() && file.isFile()) {
 				Options options = new Options();
 				options.inJustDecodeBounds = true;
 				BitmapFactory.decodeFileDescriptor(new FileInputStream(file).getFD(), null, options);
 				 int inSampleSize = 1;
 				 int width = options.outWidth;
 				 int height = options.outHeight;
 				 
 			    if (width>390  ||  height > 390) {
 			        if (width > height) {
 			            inSampleSize = Math.round((float)height / (float)390);
 			        } else {
 			            inSampleSize = Math.round((float)width / (float)390);
 			        }
 			    }
 				options.inJustDecodeBounds = false;
 				options.inSampleSize = inSampleSize;
 				bitmap = BitmapFactory.decodeFileDescriptor(new FileInputStream(file).getFD(), null, options);
 				return bitmap;
 			} else {
 				return null;
 			}
 		} catch (OutOfMemoryError e) {
 			GuLogUtil.e(AdConstants.LOG_ERR, "image: " + e);
 			return null;
 		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; 
 	}
     /**
 	 * 保存网络图片到本地
     * @param url 
 	 */
 	private  boolean saveImg(Context context, byte[] dataFromServer, String url) {
 		try {
 			String dir = getDownloadDir(context) + AdConstants.DOWNLOAD_DIR
 					+ File.separator;
 			File file = new File(dir + URLEncoder.encode(url.substring(url.lastIndexOf("/")+1)));
// 			System.out.println(".===="+file.getName());
 			if (file.exists()) {
 				return true;
 			} else {
 				if (!file.getParentFile().exists()) {
 					file.getParentFile().mkdirs();
 				}
 			}
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
 							"saveImgErr: " + e);
 				}
 				catch (OutOfMemoryError e) {
 					GuLogUtil.e(AdConstants.LOG_ERR,
 							"saveImgErr: " + e);
 				}finally {
 					if (Buff != null) {
 						try {
 							Buff.close();
 						} catch (Exception e) {
 							GuLogUtil.e(AdConstants.LOG_ERR,
 									"saveImgErr: " + e);
 						}
 						catch (OutOfMemoryError e) {
 							GuLogUtil.e(AdConstants.LOG_ERR,
 									"saveImgErr: " + e);
 						}
 					}
 					if (fos != null) {
 						try {
 							fos.close();
 						} catch (Exception e) {
 							GuLogUtil.e(AdConstants.LOG_ERR,
 									"saveImgErr: " + e);
 						}
 						catch (OutOfMemoryError e) {
 							GuLogUtil.e(AdConstants.LOG_ERR,
 									"saveImgErr: " + e);
 						}

 					}
 				}
 				return true;
 			} else {
 				return false;
 			}
 		} catch (Exception e) {
 			GuLogUtil.e(AdConstants.LOG_ERR,
 					"saveImgErr: " + e);
 			return false;
 		}
 		catch (OutOfMemoryError e) {
 			GuLogUtil.e(AdConstants.LOG_ERR,
 					"saveImgErr: " + e);
 			return false;
 		}
 	}
     

    
    public void recycleCache() {
    	if(cache!=null){
    		cache.clear();
    	}
    	if(imageViews!=null){
    		imageViews.clear();
    	}
    	if(placeholder!=null&&!placeholder.isRecycled()){
    		placeholder.recycle();
    	}
    }  

 }