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
	 * ���������ǩ�����������Ϊ���飬�㷨���£�
	 * 1. �����鰴KEY������������
	 * 2. ������������������̻���Կ������Ϊkey����ֵΪ�̻���Կ
	 * 3. ������ƴ�ӳ��ַ�������key=value&key=value����ʽ����ƴ�ӣ�ע�����ﲻ��ֱ�ӵ���
	 *    http_build_query��������Ϊ�÷�����Բ�������URL����
	 * 4. Ҫ�����������е�$params ['sign_method']����ļ����㷨����ƴ�Ӻõ��ַ������м��ܣ����ɵı���ǩ����
	 * $params ['sign_method']����1ʹ��md5���ܣ�����2ʹ��sha-1����
	 * @param array $params ����ǩ��������
	 * @return string | boolean �ɹ���������ǩ����ʧ�ܷ���false
	 */
	public String   make_sign(String[] ary)
	{
		Arrays.sort(ary,String.CASE_INSENSITIVE_ORDER);   
		//�Բ���������а�key��������,Ȼ��ƴ�ӣ�������5ǩ������
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < ary.length; i++)
		{  
				sb. append(ary[i]+"&");	
		}
		String newStrTemp = sb.toString()+"key="+GetKey().trim();
		//��ȡsign_method
		String signmethod= GetMethodSign(newStrTemp);
		//����sign_methodѡ��ʹ��MD5ǩ��1�����ǹ�ϣǩ��2
		String sign=null;
		if(signmethod.equals("1")){
		  sign =new MD5().md5Digest(newStrTemp); 
		}
		else if(signmethod.equals("2")){
	      sign =new SHA1().Digest(newStrTemp,"gbk").toLowerCase();
		}
		System.out.println("str��ǩ����: " + newStrTemp + ";ǩ���� sign=" + sign);
		return sign;
	}
	public String   make_sign_by_map(HashMap<String,String> map)
	{
		String sign=null;
		StringBuffer sb = new StringBuffer();
		//����
		List<Map.Entry<String, String>> infoIds =
		    new ArrayList<Map.Entry<String, String>>(map.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {   
		    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
		        return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		}); 
		//�Բ���������а�key��������,Ȼ��ƴ�ӣ�������5ǩ������
		int size  = infoIds.size();
		for(int i = 0; i < size; i++)
		{  
			if(!"".equals(infoIds.get(i).getValue()))
				sb. append(infoIds.get(i).getKey()+"="+infoIds.get(i).getValue()+"&");	
		}
		String newStrTemp = sb.toString()+"key="+GetKey().trim();
		//��ȡsign_method
		String signmethod= GetMethodSign(newStrTemp);
		//����sign_methodѡ��ʹ��MD5ǩ��1�����ǹ�ϣǩ��2
		if(signmethod.equals("1")){
		  sign =new MD5().md5Digest(newStrTemp); 
		}
		else if(signmethod.equals("2")){
	      sign =new SHA1().Digest(newStrTemp,"gbk").toLowerCase();
		}
		System.out.println("str��ǩ����: " + newStrTemp + ";ǩ���� sign=" + sign);
		return sign;
	}
	/**
	 * ��־��ӡ����������������־�Ĵ��λ��
	 */
	public static Logger  printLog(String strName)
	{
		Logger logger = Logger.getLogger(strName);
		FileHandler fileHandler = null;
		try {
		       fileHandler = new FileHandler("d:/BaifubaoLog.txt", 0, 1, true);
		       /**
		        * ����1��ָ����־����ļ�·��
		        * ����2����ʾ�ļ�����ֽ��� 0��ʾ������
		        * ����3����־�ļ������Զ�� 
		        * ����4���Ƿ���ԭ����־��׷����־,true��ʾ׷����־�ļ������������޸�
		        */
		} catch (Exception ex) {
		       ex.printStackTrace();
		}
		fileHandler.setLevel(Level.FINER);//������־�ļ����������
		fileHandler.setFormatter(new SimpleFormatter());//���������Ϣ��ʽΪ��ͨ��ʽ Ĭ��ΪXML��
		logger.addHandler(fileHandler);
		logger.setLevel(Level.FINER);//������־����
		return logger;
	}
	/**
	 *��ȡ�̻���Կ 
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
	 *ѡ��ǩ������ 
	 */
	private String GetMethodSign(String  sb){	
		int aa=sb.indexOf("sign_method=");
	    String signmethod= sb.substring(aa+12,aa+13);
		return signmethod;
	}
   
}
