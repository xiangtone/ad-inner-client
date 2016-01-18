package com.adwalker.wall.init;


public abstract class AdWalkerListener {
	
	/**
	 * 初始化成功
	 */
	public void initSucess(){};
	
	/**
	 * 初始化失败(作废)
	 */
	public  void initFailed(){};
	
	/**
	 * 插屏预加载成功
	 */
	public void pobLoadingSucess(){};
	
	/**
	 * 展示成功
	 */
	public  void AdShowSucess(){};
	
	/**
	 * 插屏关闭
	 */
	public  void AdClose(){};
	
	
	/**
	 * 查询积分
	 * score:积分
	 * unit: 单位
	 */
	public  void getSucess(int score, String unit){};
	
	
	/**
	 * 消耗积分
	 * consumeScore:已消耗积分
	 * balanceScore: 总余额
	 * unit: 单位
	 */
	public  void consumeSucess(int consumeScore,int balanceScore, String unit){};
	
	
	/**
	 * 失败回调
	 * code:状态码
	 * error: 错误信息
	 */
	public  void callFailed(int code, String error){};
	
	/**
	 * 激活回调
	 * earnScore:赚取的积分
	 * balanceScore: 总余额
	 * unit: 单位
	 */
	public void AdActivating(int earnScore,int balanceScore, String unit){};
	
}
