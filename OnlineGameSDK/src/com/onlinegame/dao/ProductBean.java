package com.onlinegame.dao;

public class ProductBean {
	private String product_subject;
	private String product_describe;
	private String product_price;

	private static ProductBean productBean = null;

	public static ProductBean getInstance() {
		if (productBean == null) {
			productBean = new ProductBean();
		}
		return productBean;
	}

	public String getProduct_subject() {
		return product_subject;
	}

	public void setProduct_subject(String product_subject) {

		this.product_subject = product_subject;

	}

	public String getProduct_describe() {

		return product_describe;

	}

	public void setProduct_describe(String product_describe) {

		this.product_describe = product_describe;

	}

	public String getProduct_price() {
		return product_price;
	}

	public void setProduct_price(String product_price) {
		this.product_price = product_price;
	}

}
