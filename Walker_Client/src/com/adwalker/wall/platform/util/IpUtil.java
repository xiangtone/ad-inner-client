package com.adwalker.wall.platform.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;



public class IpUtil {
	
	public static String getMobileIpAddress(Context context){
		//判断手机网络类型
		String type = ""; 
		String ipAddress="";
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    NetworkInfo info = cm.getActiveNetworkInfo(); 
	    if (info == null) { 
	        type = "null"; 
	    } else if (info.getType() == ConnectivityManager.TYPE_WIFI) { 
	        type = "wifi"; 
	        //当手机为wifi网络
	        ipAddress= getLocalIpAddress(context);
	    } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) { 
	        int subType = info.getSubtype(); 
	        if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS 
	                || subType == TelephonyManager.NETWORK_TYPE_EDGE) { 
	            type = "2g"; 
	        } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA 
	                || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0 
	                || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) { 
	            type = "3g"; 
	        } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准 
	            type = "4g"; 
	        } 
	        //当手机为非wifi网络
	        ipAddress= get3GAddress();
	    } 
		return ipAddress ;
	}
	
	
	//获取本机WIFI
	public static String getLocalIpAddress(Context context) {
	        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
	        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	        // 获取32位整型IP地址
	        int ipAddress = wifiInfo.getIpAddress();
	        
	        //返回整型地址转换成“*.*.*.*”地址
	        return String.format("%d.%d.%d.%d",
	                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
	                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
	    }
	
//	3G网络IP
	public static String get3GAddress() {
	try {
	            for (Enumeration<NetworkInterface> en = NetworkInterface
	                    .getNetworkInterfaces(); en.hasMoreElements();) {
	                NetworkInterface intf = en.nextElement();
	                for (Enumeration<InetAddress> enumIpAddr = intf
	                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                    InetAddress inetAddress = enumIpAddr.nextElement();
	                    if (!inetAddress.isLoopbackAddress()
	                            && inetAddress instanceof Inet4Address) {
	                        // if (!inetAddress.isLoopbackAddress() && inetAddress
	                        // instanceof Inet6Address) {
	                        return inetAddress.getHostAddress().toString();
	                    }
	                }
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
}