package com.adcount.domain;

public class CountBean {
	private String date;
	private int AdShow;
	private int AdClickWeb;
	private String ClickToUrl;
	private String uid;
	private String isAdShow;
	private String ipAddress;
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getIsAdShow() {
		return isAdShow;
	}

	public void setIsAdShow(String isAdShow) {
		this.isAdShow = isAdShow;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getClickToUrl() {
		return ClickToUrl;
	}

	public void setClickToUrl(String clickToUrl) {
		ClickToUrl = clickToUrl;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getAdShow() {
		return AdShow;
	}

	public void setAdShow(int adShow) {
		AdShow = adShow;
	}

	public int getAdClickWeb() {
		return AdClickWeb;
	}

	public void setAdClickWeb(int adClickWeb) {
		AdClickWeb = adClickWeb;
	}

	@Override
	public String toString() {
		return "CountBean [date=" + date + ", AdShow=" + AdShow + ", AdClickWeb=" + AdClickWeb + ", ClickToUrl="
				+ ClickToUrl + ", uid=" + uid + ", isAdShow=" + isAdShow + ", ipAddress=" + ipAddress + "]";
	}


}
