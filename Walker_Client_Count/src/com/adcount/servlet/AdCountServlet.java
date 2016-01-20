package com.adcount.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.common.util.ThreadPool;

import com.adcount.contents.ADContents;
import com.adcount.domain.CountBean;
import com.adcount.domain.LogInsert;
import com.adcount.impl.CountDaoImpl;

/**
 * Servlet implementation class AdCountServlet
 */
@WebServlet("/AdCountServlet")
public class AdCountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String date;
	private String adClickWeb;
	private String adShow;
	private String clickToUrl;
	private String uid;
	private String isAdShow;
	private String ipAddress;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	
	public AdCountServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		date = request.getParameter("date");
		adClickWeb = request.getParameter("AdClickWeb");
		adShow = request.getParameter("AdShow");
		clickToUrl = request.getParameter("ClickToUrl");
		uid = request.getParameter("uid");
		isAdShow = request.getParameter("isAdShow");
		ipAddress = request.getParameter("ipAddress");
		
	//	System.out.println(clickToUrl);打印跳转的url
//			System.out.println("ip=  "+ipAddress);
		
		
		CountBean countBean = new CountBean();
		countBean.setDate(date);
		countBean.setAdClickWeb(Integer.parseInt(adClickWeb));
		countBean.setAdShow(Integer.parseInt(adShow));
		countBean.setClickToUrl(clickToUrl); //待使用
		countBean.setUid(uid); //待使用
		countBean.setIsAdShow(isAdShow);
		countBean.setIsAdShow(ipAddress);
		
		//判断加载统计
		if (countBean.getIsAdShow() == "adshow" || countBean.getIsAdShow().equals("adshow")) {
			AdShowCount(countBean); //提交统计
			doRedirct(request, response); //不带url
		} else {
			WebClickCount(countBean);//提交统计
			doRedirct(request, response); //带url
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private void doRedirct(HttpServletRequest request, HttpServletResponse response) throws IOException {
//	    String targetUrl = "http://m.baidu.com";
	    String targetUrl = clickToUrl;
	    
	    if (request.getHeader("user-agent") != null
	        && (request.getHeader("user-agent").matches("(.*)iPhone(.*)") || request.getHeader("user-agent").matches(
	            "(.*)iPod(.*)"))) {
	      targetUrl = "http://r.n8wan.com/";
	    }
	    ThreadPool.mThreadPool.execute(new LogInsert(request.getParameter("f"), request.getHeader("user-agent"), targetUrl, ipAddress));
	    
	    response.sendRedirect(targetUrl);
	  }
	
	/**
	 * 广告网页点击统计
	 * 
	 * @param countBean
	 */
	public void WebClickCount(CountBean countBean) {
		boolean results;
		CountDaoImpl countDao = new CountDaoImpl();
		// CountBean countBean = countDao.checkIsExist(date);
		// ADContents.date = countBean.getDate();
		// 更新数据加1
		results = countDao.updateWebclickCount(countBean.getDate());
		if (results) {
			System.out.println("广告网页点击成功+1");
		} else {
			// 插入新的行
			boolean insertResults = countDao.insertCount(countBean);
			if (insertResults == true) {
				System.out.println("插入新的天数成功");
				WebClickCount(countBean);
			}

		}
	}

	 @SuppressWarnings("rawtypes")
	  private void printHeader(HttpServletRequest request) {
	    Enumeration names = request.getHeaderNames();
	    StringBuilder sb = new StringBuilder("headerInfo---");
	    while (names.hasMoreElements()) {
	      String name = names.nextElement().toString();
	      Enumeration headers = request.getHeaders(name);
	      sb.append(name).append(":");
	      while (headers.hasMoreElements()) {
	        sb.append(headers.nextElement()).append(" ");
	      }
	      sb.append("\n");
	    }
	  //  LOG.debug(sb.toString());
	  }
	
	/**
	 * 广告弹出统计
	 * 
	 * @param countBean
	 */
	public void AdShowCount(CountBean countBean) {
		boolean results;
		CountDaoImpl countDao = new CountDaoImpl();
		// CountBean countBean = countDao.checkIsExist(date);
		// ADContents.date = countBean.getDate();
		// 更新数据加1
		results = countDao.updateAdShowCount(countBean.getDate());
		if (results) {
			System.out.println("广告弹窗展示成功+1");
		} else {
			// 插入新的行
			boolean insertResults = countDao.insertCount(countBean);
			if (insertResults == true) {
				System.out.println("插入新的天数成功");
				AdShowCount(countBean);
			}

		}
	}
}
