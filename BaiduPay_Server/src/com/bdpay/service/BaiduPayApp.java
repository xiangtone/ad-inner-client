package com.bdpay.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.bdpay.util.MD5;
import com.bdpay.util.SHA1;



public class BaiduPayApp {

	
	
	private String order_create_time;
	private String order_no;
	
	public BaiduPayApp() {
		order_create_time = getTime();
		order_no = getOrderNo();
	}
	
	
	public String  pay(String goods_name,String goods_desc,String total_amount,String return_url){
		
		HashMap<String, String> params = new HashMap<>();
		//params.put("extra", "xxx");
		params.put("service_code", "1");
		params.put("sp_no", "1000315977");
		params.put("order_create_time",  order_create_time);
		params.put("order_no", order_no);
		//params.put("goods_category", "1");
		params.put("goods_name", goods_name);
		params.put("goods_desc", goods_desc);
		
		//params.put("goods_url", URLEncoder.encode("http://www.baidu.com"));
		//params.put("unit_amount", "1");
		//params.put("unit_count", "1");
		//params.put("transport_amount", "0");
		
		params.put("total_amount", total_amount);
		params.put("currency", "1");
		//params.put("buyer_sp_username", "x");
		params.put("return_url", return_url);
		
		params.put("pay_type", "2");
		//params.put("sp_pass_through", "00");
		//params.put("expire_time", getExpireTime());
		params.put("input_charset", "1");
		params.put("version", "2"); 
		
		params.put("sign_method", "1");
		//params.put("extra", "");
		
		String sign = make_sign_by_map(params);
		//params.put("sign", make_sign_by_map(params));
		
		StringBuffer sb = new StringBuffer();
		//排序
		List<Map.Entry<String, String>> infoIds =
		    new ArrayList<Map.Entry<String, String>>(params.entrySet());
//		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {   
//		    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
//		        return (o1.getKey()).toString().compareTo(o2.getKey());
//		    }
//		}); 
		//对参数数组进行按key升序排列,然后拼接，最后调用5签名方法
		int size  = infoIds.size();
		for(int i = 0; i < size; i++)
		{  
			if(!"".equals(infoIds.get(i).getValue())){
				if("goods_name".equals(infoIds.get(i).getKey())||"goods_desc".equals(infoIds.get(i).getKey())){
					  
					String name = "";
					try {
						name = URLEncoder.encode(infoIds.get(i).getValue(),"GBK");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					
					sb. append(infoIds.get(i).getKey()+"="+name+"&");	
				}else {
					sb. append(infoIds.get(i).getKey()+"="+infoIds.get(i).getValue()+"&");	
				}
				
			}
		}
		String newStrTemp = sb.toString()+"sign="+sign.trim();
		String str = newStrTemp.toString();
		return str;
	}
	
	public static String   make_sign_by_map(HashMap<String,String> map)
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
		String signmethod= "1";//GetMethodSign(newStrTemp);
		//根据sign_method选择使用MD5签名1，还是哈希签名2
		if(signmethod.equals("1")){
			try {
		        sign =new MD5().md5Digest(newStrTemp); 
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}
		else if(signmethod.equals("2")){
	      sign =new SHA1().Digest(newStrTemp,"gbk").toLowerCase();
		}
		System.out.println("str待签名串: " + newStrTemp + ";签名串 sign=" + sign);
		return sign;
	}
	
	
	
	/**
	 *获取商户密钥 
	 */
	private static  String GetKey(){  
	  return "cjsyi95FceRMTBX6b837ipXEYiHhchRn";  
    }  

	
	
	public String getExpireTime(){
		Date	date=new   Date();//取时间 
	     Calendar   calendar   =   new   GregorianCalendar(); 
	     calendar.setTime(date); 
	     calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动 
	     SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");  
		 String mDateTime=formatter.format(calendar.getTime()); 
		 return mDateTime;
	}
	
	public String getTime(){
		Calendar   c   =   Calendar.getInstance(); 
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");  
		String mDateTime=formatter.format(c.getTime()); 
		return mDateTime;
	}
	
	
	
	
	public String getOrderNo(){
		Random random = new Random();
		String str = getTime()+random.nextInt(10);
		return str;
	}
	
}
