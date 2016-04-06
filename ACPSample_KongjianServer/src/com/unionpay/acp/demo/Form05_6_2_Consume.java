package com.unionpay.acp.demo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.unionpay.acp.sdk.CertUtil;
import com.unionpay.acp.sdk.SDKConfig;
import com.unionpay.acp.sdk.SDKUtil;

/**
 * 重要：联调测试时请仔细阅读注释！
 * 
 * 产品：手机控件支付产品<br>
 * 交易：消费交易：后台异步交易，有后台通知<br>
 * 功能：获取调起控件的tn号<br>
 * 日期： 2015-09<br>
 * 版权： 中国银联<br>
 * 说明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码性能规范性等方面的保障<br>
 * 确定交易成功机制：商户必须开发后台通知接口和交易状态查询接口（Form05_6_3_Query）确定交易是否成功，建议发起查询交易的机制：代付交易发生后且交易状态不明确或未收到后台通知，3分钟后发起查询交易，可查询N次（不超过6次），每次时间间隔2N秒发起,即间隔1，2，4，8，16，32S查询（查询到03，04，05，01，12,34，60 继续查询，否则终止查询）
 */

public class Form05_6_2_Consume extends HttpServlet {

	
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
		String txnAmt = req.getParameter("txnAmt");
		String orderId = req.getParameter("orderId");
		String txnTime = req.getParameter("txnTime");
		
		Map<String, String> contentData = new HashMap<String, String>();
		
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		contentData.put("version", SDKUtil.version);            //版本号 全渠道默认值
		contentData.put("encoding", SDKUtil.encoding_UTF8);     //字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("signMethod", "01");           		 	//签名方法 目前只支持01：RSA方式证书加密
		contentData.put("txnType", "01");              		 	//交易类型 01:消费
		contentData.put("txnSubType", "01");           		 	//交易子类 01：消费
		contentData.put("bizType", "000201");          		 	//填写000201
		contentData.put("channelType", "07");          		 	//渠道类型
		
		/***商户接入参数***/
		//contentData.put("merId", merId);   		 				//商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		contentData.put("merId", "898440379930020");   		 				//商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		contentData.put("accessType", "0");            		 	//接入类型，商户接入填0 ，不需修改（0：直连商户， 1： 收单机构 2：平台商户）
		contentData.put("orderId", orderId);        	 	    //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则	
		contentData.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));		 		    //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		contentData.put("accType", "01");					 	//账号类型 01：银行卡02：存折03：IC卡帐号类型(卡介质)
		
		
		//////////如果在控件回显卡号【需开通 接收商户共享信息】，商户号开通了商户对敏感信息加密的权限那么，需要对 卡号accNo加密使用：
		contentData.put("encryptCertId",CertUtil.getEncryptCertId());      //上送敏感信息加密域的加密证书序列号
	//	String accNo = SDKUtil.encryptPan("6216261000000000018", "UTF-8"); //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
	//	contentData.put("accNo", accNo);
		//////////
		
		/////////如果在控件回显卡号【需开通 接收商户共享信息】，商户未开通敏感信息加密的权限那么不对敏感信息加密使用：
		//contentData.put("accNo", "6216261000000000018");                  //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		////////

		//代收交易的上送的卡验证要素为：姓名或者证件类型+证件号码
//		Map<String,String> customerInfoMap = new HashMap<String,String>();
//		customerInfoMap.put("certifTp", "01");						    //证件类型
//		customerInfoMap.put("certifId", "341126197709218366");		    //证件号码
//		//customerInfoMap.put("customerNm", "全渠道");					//姓名
//		String customerInfoStr = SDKUtil.getCustomerInfoWithEncrypt(customerInfoMap,"6216261000000000018",SDKUtil.encoding_UTF8);				
		
//		contentData.put("customerInfo", customerInfoStr);
		contentData.put("txnAmt", txnAmt);						 	//交易金额 单位为分，不能带小数点
		contentData.put("currencyCode", "156");                     //境内商户固定 156 人民币
		contentData.put("reqReserved", "透传字段");                    //商户自定义保留域，交易应答时会原样返回
		
		//后台通知地址（需设置为外网能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，【支付失败的交易银联不会发送后台通知】
		//后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
		//注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码 
		//    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200或302，那么银联会间隔一段时间再次发送。总共发送5次，银联后续间隔1、2、4、5 分钟后会再次通知。
		//    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
		contentData.put("backUrl", DemoBase.backUrl);
		
		/**对请求参数进行签名并发送http post请求，接收同步应答报文**/
		Map<String, String> submitFromData = SDKUtil.signData(contentData,SDKUtil.encoding_UTF8);			 //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String requestAppUrl = SDKConfig.getConfig().getAppRequestUrl();								 //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
		//如果这里通讯读超时（30秒），需发起交易状态查询交易
		Map<String, String> resmap = SDKUtil.submitUrl(submitFromData,requestAppUrl,SDKUtil.encoding_UTF8);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		  
		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		String respCode = resmap.get("respCode");
		System.err.println(respCode);
		System.out.println(resmap.get("tn"));
		
		if(("00").equals(respCode)){
			//成功,获取tn号
			String tn = resmap.get("tn");
			
			resp.getWriter().write(tn);
			
		}else{
			//其他应答码为失败请排查原因
			//TODO
			
		}

		String reqMessage = DemoBase.genHtmlResult(submitFromData);
		String rspMessage = DemoBase.genHtmlResult(resmap);
		//resp.getWriter().write("获取tn交易</br>请求报文:<br/>"+reqMessage+"<br/>" + "应答报文:</br>"+rspMessage+"");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

}
