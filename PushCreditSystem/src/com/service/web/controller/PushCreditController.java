package com.service.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

/**
 * 中转访销系统的两个请求到积分系统
 * @author lifusen
 *
 */
public class PushCreditController extends Controller {

	private final Logger log = LoggerFactory.getLogger(PushCreditController.class);

	@Before(POST.class)
	public void convertWillScore() throws MalformedURLException {

		// 获取来自访销系统的json
		StringBuffer json = new StringBuffer();
		try {
			BufferedReader reader = this.getRequest().getReader();
			String line = null;
			while ((line = reader.readLine()) != null) 
			{
				json.append(line);
			}
			reader.close();
		} catch (IOException e) 
		{
			log.error("____________________转换积分中转收到参数异常:" + json.toString());
		}
		// 把json字符串推送到积分系统
		if (json.toString() != null)
		{
			HttpURLConnection conn = null;
			InputStream in = null;
			StringBuffer sb = new StringBuffer();
			sb.append("{\"success\": false, \"data\": { \"code\": 0, \"message\": \"operation failed\", \"object\": {");
			sb.append("\"code\":0,\"message\": \"operation failed\", \"object\": [ { \"score\": 0, \"productID\": 0");
			sb.append("},{\"score\":0,\"productID\":0 } ] } } }");
			try {
				URL url = new URL("http://172.16.100.11:80/wlyclub/oapi/store/convertWillScore");

				conn = (HttpURLConnection) url.openConnection();
				
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);

				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Charset", "UTF-8");

				byte[] data = (json.toString()).getBytes("UTF-8");
				conn.setRequestProperty("Content-Length", String.valueOf(data.length));
				conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
				conn.connect();
				
				OutputStream out = conn.getOutputStream();
				// 写入请求的字符串
				out.write((json.toString()).getBytes("UTF-8"));
				out.flush();
				out.close();
				log.error("**此条为info消息____________________转换积分中转收到积分系统状态码" + conn.getResponseCode());
				// 请求返回的状态
				if (conn.getResponseCode() == 200) 
				{
					// 请求返回的数据
					in = conn.getInputStream();
					
					String returnStr = null;

					byte[] returnData = new byte[in.available()];
					in.read(returnData);
					// 转成字符串
					returnStr = new String(returnData,"UTF-8");
					renderJson(returnStr);
				} else 
				{
					log.error("____________________转换积分调用积分系统===反馈状态码异常：" + conn.getResponseCode());
					log.error("____________________转换积分调用积分系统===反馈异常参数：" + json.toString());					
					//如果中转收到非200额状态码，返回给访销系统模拟json数据
					renderJson(sb.toString());
				}
			} catch (Exception e) 
			{
				log.error("____________________转换积分推送积分系统===发生异常");
				renderJson(sb.toString());
			}finally
			{
				try {
					if(conn!=null)
					{
						conn.disconnect();
					}
					if(in !=null)
					{
						in.close();
					}
				}catch(Exception e)
				{
					log.error("____________________转换积分推送积分系统===关闭链接发生异常" + e.getMessage());
				}
				
			}
		}
	}

	@Before(GET.class)
	public void getWillScore() {
		if(getPara("storeCode")!=null&&getPara("apiAppId")!=null&getPara("apiAppKey")!=null){
			HttpURLConnection httpUrlConn = null;
			InputStream in = null;
			StringBuffer sb = new StringBuffer();
			sb.append("{\"success\": false, \"data\": { \"code\": 0, \"message\": \"operation failed\", \"scoreList\": [");
			sb.append("{\"id\": 0, \"name\":\"\",  \"degree\":0,  \"volume\":0,\"spec\":0,  \"willScore\":0}]}}");
			try {
				URL url = new URL("http://172.16.100.11:80/wlyclub/oapi/store/getWillScore?storeCode="
				+ getPara("storeCode") + "&apiAppId=" + getPara("apiAppId") + "&apiAppKey=" 
				+ getPara("apiAppKey"));
				
				httpUrlConn = (HttpURLConnection) url.openConnection();

				httpUrlConn.setDoOutput(false);
				httpUrlConn.setDoInput(true);
				httpUrlConn.setUseCaches(false);

				httpUrlConn.setRequestMethod("GET");
				httpUrlConn.connect();
				
				log.error("**此条为info消息____________________查询预积分中转收到积分系统状态码" + httpUrlConn.getResponseCode());
				if (httpUrlConn.getResponseCode() == 200) 
				{
					// 请求返回的数据
					in = httpUrlConn.getInputStream();
					String returnStr = null;

					byte[] data = new byte[in.available()];
					in.read(data);
					// 转成字符串
					returnStr = new String(data,"UTF-8");
					renderJson(returnStr);
				} else 
				{
					log.error("____________________查询预积分调用积分系统===反馈状态码异常：" + httpUrlConn.getResponseCode());
					log.error("____________________查询预积分调用积分系统===反馈异常参数：" 
					+ "?storeCode=" + getPara("storeCode") + "&apiAppId=" + getPara("apiAppId") 
					+ "&apiAppKey=" + getPara("apiAppKey"));
					//如果中转收到非200额状态码，返回给访销系统模拟json数据
					renderJson(sb.toString());
				}
			} catch (Exception e) 
			{
				log.error("____________________查询预积分推送积分系统===发生异常" + e.getMessage());
				renderJson(sb.toString());
			}finally
			{
				try {
					if(httpUrlConn!=null)
					{
						httpUrlConn.disconnect();
					}
					if(in!=null)
					{
						in.close();
					}
					}catch(Exception e)
				{
						log.error("____________________查询预积分推送积分系统===关闭链接发生异常");
				}
			}
		}else
		{
			log.error("____________________查询预积分中转收到参数异常:"+getPara("storeCode")+"_"+getPara("apiAppId")+"_"+getPara("apiAppKey"));
		}
		
	}
}
