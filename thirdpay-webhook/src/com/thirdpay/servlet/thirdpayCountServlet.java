package com.thirdpay.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.print.attribute.standard.RequestingUserName;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.common.util.ThreadPool;
import org.omg.PortableInterceptor.SUCCESSFUL;

import com.thirdpay.domain.LogInsert;


/**
 * Servlet implementation class thirdpayCountServlet
 */
@WebServlet("/thirdpayCountServlet")
public class thirdpayCountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
		response.getWriter().append("success");
		
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
	    
	    
	    ThreadPool.mThreadPool.execute(new LogInsert(request.getParameter("f"), request.getHeader("user-agent"), targetUrl, "ip��ַ","","","",""));
    
response.sendRedirect(targetUrl);
		
		
		
		
	  }
	
	public static String requestPostData(HttpServletRequest request)
	{
		BufferedReader br = null;
		InputStreamReader isr = null;
		InputStream is = null;
		StringBuffer result = null;
		
		try
		{
			is = request.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			result = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null)
			{
				result.append(line);
			}
			
			System.out.println("���"+result.toString().trim());
			
			return result.toString().trim();
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try{if(br!=null)br.close();}catch(Exception ex){}
			try{if(isr!=null)isr.close();}catch(Exception ex){}
			try{if(is!=null)is.close();}catch(Exception ex){}
		}
		return "";
	}
}
