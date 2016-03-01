package com.thirdpay.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.common.util.ThreadPool;

import com.thirdpay.domain.LogInsert;


/**
 * Servlet implementation class thirdpayCountServlet
 */
@WebServlet("/thirdpayCountServlet")
public class thirdpayCountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(thirdpayCountServlet.class);  
//	private String clickToUrl;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public thirdpayCountServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		//	doRedirct(request, response);
		String xx_notifyData = request.getParameter("xx_notifyData");
		
		logger.info(xx_notifyData); //打印自定义的传值 如appkey
		
		response.getWriter().append("success");
		
	//	logger.info("第三方支付测试");  
		requestPostData(request);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
	}

	private void doRedirct(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    String targetUrl = "http://m.baidu.com";
//	    String targetUrl = clickToUrl;
	    String nochannel = request.getParameter("nochannel");
	    String money = request.getParameter("money");
	    String commodity = request.getParameter("commodity");
	    String orderid = request.getParameter("nochannel");
	    if (request.getHeader("user-agent") != null && (request.getHeader("user-agent").matches("(.*)iPhone(.*)") || request.getHeader("user-agent").matches(
	            "(.*)iPod(.*)"))) {
	      targetUrl = "http://r.n8wan.com/";
	    }
	    
	    
	    ThreadPool.mThreadPool.execute(new LogInsert(request.getParameter("f"), request.getHeader("user-agent"), targetUrl, "ip地址","","","",""));response.sendRedirect(targetUrl);
		
		
	  }
	
	public static String requestPostData(HttpServletRequest request)
	{
		
		Map<String, String[]> map = request.getParameterMap();
		
		Iterator<Entry<String, String[]>> iterator =  map.entrySet().iterator();		
		while (iterator.hasNext()) {
			Map.Entry<java.lang.String, java.lang.String[]> entry = (Map.Entry<java.lang.String, java.lang.String[]>) iterator
					.next();
			
			String key = entry.getKey();
			String []value = map.get(key);
			
//			System.out.println(key);
			logger.info(key);
			
			for (int i = 0; i < value.length; i++) {
//				System.out.println(value[i]);
				logger.info(value[i]);
				
			}
			
			
		}
		
		
		
		return "";
	}
}
