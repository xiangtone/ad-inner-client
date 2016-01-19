package com.adwalker.wall.platform.bean;

import java.io.Serializable;

public class AdItemBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int score =0;// 积分墙分数
	public String wall_icon_Url; // 图标URL
	public String wall_left_first; // (0-描述 1-描述 2-空)
	public String wall_left_second; // (0-名称 1-名称 2-名称)
	public String wall_left_third; // (0-版本号 1-版本号+大小 2-空)
	public String wall_right; // (0-大小 1-积分 2-空)
	public String wall_desc; // (0-大小 1-积分 2-空)
	public String scoreUnit="积分";// 积分墙单位
	
}
