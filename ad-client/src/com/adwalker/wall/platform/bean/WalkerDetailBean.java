package com.adwalker.wall.platform.bean;

import java.io.Serializable;
import java.util.List;

public class WalkerDetailBean implements Serializable {
	private static final long serialVersionUID = 1L;
	public String detail_icon_Url; // 图标URL
	public String detail_first; // 名称
	public String detail_second; // 更新时间
	public String detail_third; // 版本
	public String detail_fourth; // 大小
	public String detail_fifth; // (0-空 1-积分 2-空)
	public String detail_sixth; // 文字:软件介绍
	public String detail_seventh; // 介绍内容
	public int isDownload;//0:广告未被下载，1被下载
	public String catagoryName;
	public List<String> detail_picturesUrl; // 界面图片(多)
}
