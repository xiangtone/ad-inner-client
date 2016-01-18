package com.adcount.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.adcount.contents.ADContents;
import com.adcount.domain.CountBean;
import com.adcount.impl.CountDaoImpl;

/**
 * Servlet implementation class AdCountServlet
 */
@WebServlet("/AdCountServlet")
public class AdCountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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

		String date = request.getParameter("date");
		String adClickWeb = request.getParameter("AdClickWeb");
		String adShow = request.getParameter("AdShow");
		String clickToUrl = request.getParameter("ClickToUrl");
		String uid = request.getParameter("uid");
		String isAdShow = request.getParameter("isAdShow");

		CountBean countBean = new CountBean();
		countBean.setDate(date);
		countBean.setAdClickWeb(Integer.parseInt(adClickWeb));
		countBean.setAdShow(Integer.parseInt(adShow));
		countBean.setClickToUrl(clickToUrl); //待使用
		countBean.setUid(uid); //待使用
		countBean.setIsAdShow(isAdShow);

		//判断加载统计
		if (countBean.getIsAdShow() == "adshow" || countBean.getIsAdShow().equals("adshow")) {
			AdShowCount(countBean);
		} else {
			WebClickCount(countBean);
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
