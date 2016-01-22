package com.unionpay.acp.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.unionpay.acp.sdk.SDKConfig;
import com.unionpay.acp.sdk.SDKUtil;

/**
 * 重要：联调测试时请仔细阅读注释！
 * 
 * 产品：手机控件产品<br>
 * 交易：交易状态查询交易：只有同步应答 <br>
 * 日期： 2015-09<br>
 * 版本： 1.0.0 
 * 版权： 中国银联<br>
 * 说明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码性能及规范性等方面的保障<br>
 * 交易说明：代付同步返回00，如果未收到后台通知建议3分钟后发起查询交易，可查询N次（不超过6次），每次时间间隔2N秒发起,即间隔1，2，4，8，16，32S查询（查询到03 04 05 01 12 34 60继续查询，否则终止查询）。【如果最终尚未确定交易是否成功请以对账文件为准】
 *        代付同步返03 04 05 01 12 34 60响应码及未得到银联响应（读超时）建议3分钟后发起查询交易，可查询N次（不超过6次），每次时间间隔2N秒发起,即间隔1，2，4，8，16，32S查询（查询到03 04 05 01 12 34 60继续查询，否则终止查询）。【如果最终尚未确定交易是否成功请以对账文件为准】
 */
public class Form05_6_3_Query extends HttpServlet {

	@Override
	public void init(ServletConfig config) throws ServletException {
		/**
		 * 请求银联接入地址，获取证书文件，证书路径等相关参数初始化到SDKConfig类中
		 * 在java main 方式运行时必须每次都执行加载
		 * 如果是在web应用开发里,这个方法可使用监听的方式写入缓存,无须在这出现
		 */
		//这里已经将加载属性文件的方法挪到了web/AutoLoadServlet.java中
		//SDKConfig.getConfig().loadPropertiesFromSrc(); //从classpath加载acp_sdk.properties文件
		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String merId = req.getParameter("merId");
		String orderId = req.getParameter("orderId");
		String txnTime = req.getParameter("txnTime");
		
		Map<String, String> data = new HashMap<String, String>();
		
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		data.put("version", SDKUtil.version);                 //版本号
		data.put("encoding", SDKUtil.encoding_UTF8);               //字符集编码 可以使用UTF-8,GBK两种方式
		data.put("signMethod", "01");                          //签名方法 目前只支持01-RSA方式证书加密
		data.put("txnType", "00");                             //交易类型 00-默认
		data.put("txnSubType", "00");                          //交易子类型  默认00
		data.put("bizType", "000401");                         //业务类型 代付
		
		/***商户接入参数***/
		data.put("merId", merId);                  			   //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		data.put("accessType", "0");                           //接入类型，商户接入固定填0，不需修改
		
		/***要调通交易以下字段必须修改***/
		data.put("orderId", orderId);                 //****商户订单号，每次发交易测试需修改为被查询的交易的订单号
		data.put("txnTime", txnTime);                 //****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间

		/**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
		
		Map<String, String> submitFromData = SDKUtil.signData(data,SDKUtil.encoding_UTF8);			//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String url = SDKConfig.getConfig().getSingleQueryUrl();										//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
		Map<String, String> resmap = SDKUtil.submitUrl(submitFromData, url,SDKUtil.encoding_UTF8); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		
		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		
		if(("00").equals(resmap.get("respCode"))){//如果查询交易成功
			String origRespCode = resmap.get("origRespCode");
			//处理被查询交易的应答码逻辑
			if(("00").equals(origRespCode) ||("A6").equals(origRespCode)){
				//A6代付交易返回，参与清算，商户应该算成功交易，根据成功的逻辑处理
				//交易成功，更新商户订单状态
				//TODO
			}else if(("03").equals(origRespCode)||
					 ("04").equals(origRespCode)||
					 ("05").equals(origRespCode)||
					 ("01").equals(origRespCode)||
					 ("12").equals(origRespCode)||
					 ("34").equals(origRespCode)||
					 ("60").equals(origRespCode)){
				//订单处理中或交易状态未明，需稍后发起交易状态查询交易 【如果最终尚未确定交易是否成功请以对账文件为准】
				//TODO
			}else{
				//其他应答码为交易失败
				//TODO
			}
		}else if(("34").equals(resmap.get("respCode"))){
			//订单不存在，可认为交易状态未明，需要稍后发起交易状态查询，或依据对账结果为准
			
		}else{//查询交易本身失败，如应答码10/11检查查询报文是否正确
			//TODO
		}
		
		String reqMessage = DemoBase.genHtmlResult(submitFromData);
		String rspMessage = DemoBase.genHtmlResult(resmap);
		resp.getWriter().write("交易状态查询交易</br>请求报文:<br/>"+reqMessage+"<br/>" + "应答报文:</br>"+rspMessage+"");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
}