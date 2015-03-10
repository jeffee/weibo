/**
 * 通過多線程方法获取加载页面
 */
package com.weibo.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;


/**
 * @author Jeffee
 *
 */
public class PageThread extends Thread{
	protected final HttpClient httpClient;
	protected final HttpGet get;
	private final HttpContext context;
	protected String dFile = "";

	public PageThread(HttpClient httpClient, HttpGet get, String dFile) {
		this.httpClient = httpClient;
		this.get = get;
		this.dFile = dFile;
		this.context = new BasicHttpContext();
	}

	public void run() {
		//System.out.println("GET PAge^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		try {
			HttpResponse response = httpClient.execute(get, context);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), "utf8"));		
			analyse(br);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			// 释放连接
		get.releaseConnection();
		}
	}
	
	protected void analyse(BufferedReader br) {
		//line = new JSONTokener(line).nextValue().toString();
	}

}
