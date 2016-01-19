package com.adwalker.wall.platform.bean;

public class AppPackageBean {
	public String packageName; // 包名
	public String appName; // APK名称
	public String appVersionName; // 版本名
	public int appVersionCode; // 版本号

	@Override
	public String toString() {
		return "AppPackageInfo [packageName=" + packageName + ", appName="
				+ appName + ", appVersionName=" + appVersionName
				+ ", appVersionCode=" + appVersionCode + "]";
	}
}
