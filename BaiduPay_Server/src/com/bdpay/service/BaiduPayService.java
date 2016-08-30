package com.bdpay.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BaiduPayService
 */
@WebServlet("/BaiduPayService")
public class BaiduPayService extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**  
     * @see HttpServlet#HttpServlet()
     */
    public BaiduPayService() {
        super();
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=gbk");
        request.setCharacterEncoding("gbk");
        
        String goods_name = request.getParameter("goods_name");
        String goods_desc = request.getParameter("goods_desc");
        String total_amount = request.getParameter("total_amount");
        String return_url = request.getParameter("return_url");
          
    	BaiduPayApp pay = new BaiduPayApp();
		String str = pay.pay(goods_name, goods_desc, total_amount, return_url);
		response.getWriter().append(str);
	}

}
