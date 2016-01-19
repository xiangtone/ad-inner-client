package com.adwalker.wall.platform.bean;


import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.adwalker.wall.init.AdWalkerListener;
import com.adwalker.wall.platform.network.GuDownloadInfo;

public class WalkerAdBean implements  Serializable,Parcelable ,Cloneable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Integer id; // ID
	public int ownerId; // 所有者ID(广告主ID--废弃)
	public int resourceSize; // 资源大小
	public String title; // 名称
	public String resourceUrl; // 资源URL
	public String fileName; // 文件名
	public String packageName; // 包名
	public int page_type; // 墙类型
	public int interval; // 推送广告间隔
	public int state = 0; // 0-未下载 1-下载中 3-已安装

	public String adimage_url; // 推广条/插屏图片地址
	public int adimage_width; // 推广条/插屏图片宽
	public int adimage_height; // 推广条/插屏图片高

	public String ad_url; // 跳转地址
	public int ad_type; // 0-跳至详情页 1-跳至注册网页,2下载按钮
	
	public int isDownload;//0:广告未被下载，1激活,2一次签到，3二次签到,-1当天广告状态激活后状态
	
	public int delay_time = 30;//延时加载时间，默认30
	public String score_msg = "";//积分获取说明
	
	public String catagory;
	public AdItemBean generalInfo;
	public GuDownloadInfo downloadInfo = null; // 下载信息
	public int bannerTag;//1:广告包含banner,2广告不包含banner
//	public int sign_status;//签到开关,0关闭，1一次签到，2次签到。
	public AdWalkerListener adWalkerListener;
	
	@Override
	public  Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(ownerId);
		dest.writeInt(resourceSize);
		dest.writeString(title);
		dest.writeString(resourceUrl);
		dest.writeString(fileName);
		dest.writeString(packageName);
		dest.writeInt(page_type);
		
		dest.writeInt(interval);
		dest.writeSerializable(downloadInfo);
		dest.writeInt(state);
		dest.writeString(adimage_url);
		dest.writeInt(adimage_width);
		dest.writeInt(adimage_height);
		dest.writeString(ad_url);
		dest.writeInt(ad_type);
		dest.writeInt(isDownload);
		dest.writeInt(delay_time);
		dest.writeString(score_msg);
		dest.writeInt(bannerTag);
		
	}
	public static final Parcelable.Creator<WalkerAdBean> CREATOR = new Creator<WalkerAdBean>() {
		public WalkerAdBean createFromParcel(Parcel source) {
			WalkerAdBean wallInfo = new WalkerAdBean();
			wallInfo.id = source.readInt();
			wallInfo.ownerId = source.readInt();
			wallInfo.resourceSize = source.readInt();
			wallInfo.title = source.readString();
			wallInfo.resourceUrl = source.readString();
			wallInfo.fileName = source.readString();
			wallInfo.packageName = source.readString();
			wallInfo.page_type = source.readInt();
			
			wallInfo.interval = source.readInt();
			wallInfo.downloadInfo = (GuDownloadInfo) source.readSerializable();
			wallInfo.state = source.readInt();
			wallInfo.adimage_url = source.readString();
			wallInfo.adimage_width = source.readInt();
			wallInfo.adimage_height = source.readInt();
			
			wallInfo.ad_url = source.readString();
			wallInfo.ad_type = source.readInt();
			wallInfo.isDownload = source.readInt();
			wallInfo.delay_time = source.readInt();
			wallInfo.score_msg = source.readString();
			wallInfo.bannerTag = source.readInt();
			
			return wallInfo;
		}
		public WalkerAdBean[] newArray(int size) {
			return new WalkerAdBean[size];
		}
	};
	
	
}
	

