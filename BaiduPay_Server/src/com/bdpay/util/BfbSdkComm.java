package com.bdpay.util;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class BfbSdkComm {
	/**
	 * 计算数组的签名，传入参数为数组，算法如下：
	 * 1. 对数组按KEY进行升序排序
	 * 2. 在排序后的数组中添加商户密钥，键名为key，键值为商户密钥
	 * 3. 将数组拼接成字符串，以key=value&key=value的形式进行拼接，注意这里不能直接调用
	 *    http_build_query方法，因为该方法会对参数进行URL编码
	 * 4. 要所传入数组中的$params ['sign_method']定义的加密算法，对拼接好的字符串进行加密，生成的便是签名。
	 * $params ['sign_method']等于1使用md5加密，等于2使用sha-1加密
	 * @param array $params 生成签名的数组
	 * @return string | boolean 成功返回生成签名，失败返回false
	 */
	public String   make_sign(String[] ary)
	{
		Arrays.sort(ary,String.CASE_INSENSITIVE_ORDER);   
		//对参数数组进行按key升序排列,然后拼接，最后调用5签名方法
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < ary.length; i++)
		{  
				sb. append(ary[i]+"&");	
		}
		String newStrTemp = sb.toString()+"key="+GetKey().trim();
		//获取sign_method
		String signmethod= GetMethodSign(newStrTemp);
		//根据sign_method选择使用MD5签名1，还是哈希签名2
		String sign=null;
		if(signmethod.equals("1")){
		  sign =new MD5().md5Digest(newStrTemp); 
		}
		else if(signmethod.equals("2")){
	      sign =new SHA1().Digest(newStrTemp,"gbk").toLowerCase();
		}
		System.out.println("str待签名串: " + newStrTemp + ";签名串 sign=" + sign);
		return sign;
	}
	public String   make_sign_by_map(HashMap<String,String> map)
	{
		String sign=null;
		StringBuffer sb = new StringBuffer();
		//排序
		List<Map.Entry<String, String>> infoIds =
		    new ArrayList<Map.Entry<String, String>>(map.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {   
		    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
		        return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		}); 
		//对参数数组进行按key升序排列,然后拼接，最后调用5签名方法
		int size  = infoIds.size();
		for(int i = 0; i < size; i++)
		{  
			if(!"".equals(infoIds.get(i).getValue()))
				sb. append(infoIds.get(i).getKey()+"="+infoIds.get(i).getValue()+"&");	
		}
		String newStrTemp = sb.toString()+"key="+GetKey().trim();
		//获取sign_method
		String signmethod= GetMethodSign(newStrTemp);
		//根据sign_method选择使用MD5签名1，还是哈希签名2
		if(signmethod.equals("1")){
		  sign =new MD5().md5Digest(newStrTemp); 
		}
		else if(signmethod.equals("2")){
	      sign =new SHA1().Digest(newStrTemp,"gbk").toLowerCase();
		}
		System.out.println("str待签名串: " + newStrTemp + ";签名串 sign=" + sign);
		return sign;
	}
	/**
	 * 日志打印，可以自行设置日志的存放位置
	 */
	public static Logger  printLog(String strName)
	{
		Logger logger = Logger.getLogger(strName);
		FileHandler fileHandler = null;
		try {
		       fileHandler = new FileHandler("d:/BaifubaoLog.txt", 0, 1, true);
		       /**
		        * 参数1：指定日志输出文件路径
		        * 参数2：表示文件最大字节数 0表示不限制
		        * 参数3：日志文件数可以多个 
		        * 参数4：是否在原来日志后追加日志,true表示追加日志文件而不是重新修改
		        */
		} catch (Exception ex) {
		       ex.printStackTrace();
		}
		fileHandler.setLevel(Level.FINER);//设置日志文件中输出级别
		fileHandler.setFormatter(new SimpleFormatter());//设置输出信息格式为普通格式 默认为XML）
		logger.addHandler(fileHandler);
		logger.setLevel(Level.FINER);//设置日志级别
		return logger;
	}
	/**
	 *获取商户密钥 
	 */
	private  String GetKey(){  
	    try{  
				String key = this.getClass().getResource("").getPath().replaceAll("%20", " ");  
				String path = key.substring(0, 
				key.indexOf("WEB-INF")) + "WEB-INF/key.properties";  
				Properties config = new Properties();  
				config.load(new FileInputStream(path));  
				return config.getProperty("key");  
		    }  catch(Exception e){  
		       e.printStackTrace();  
		    }  
	        return null;  
    }  
	/**
	 *选择签名方法 
	 */
	private String GetMethodSign(String  sb){	
		int aa=sb.indexOf("sign_method=");
	    String signmethod= sb.substring(aa+12,aa+13);
		return signmethod;
	}
   
}
