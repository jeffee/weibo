/**
 * 通過多線程的方法抓取用戶數據
 */
package com.weibo.repost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.util.JSONTokener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.WeiboUser;


/**
 * @author Jeffee
 *
 */
public class UserThread extends  Thread{

	HttpClient httpClient = null;
	HttpGet getMethod = null;
	String uid = "";

	/*
	 * // 保存任务所需要的数据 private Object threadPoolTaskData;
	 * ThreadPoolTask(Object tasks) { this.threadPoolTaskData = tasks; }
	 */

	public UserThread(HttpGet get, int i) {
		// TODO Auto-generated constructor stub
	}

	public UserThread(HttpClient httpClient, HttpGet get, String uid) {
		// TODO Auto-generated constructor stub
		this.httpClient = httpClient;
		this.getMethod = get;
		this.uid = uid;
	}

	public void run() {
		WeiboUser user = null;
		
		
		try {
			long startTime = System.currentTimeMillis();
			//System.out.println("executing request " + getMethod.getURI());
			HttpResponse response = httpClient.execute(getMethod);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent(), "utf8"));
			String line = br.readLine();
			while (line != null) {
				line = new JSONTokener(line).nextValue().toString();
				line = line.substring(9, line.lastIndexOf("div") + 4);
				line = line.replace(" ", ""); // 去除所有空格干扰
				Pattern pattern = Pattern
						.compile("关注<\\\\/a>([\\d|万]*).*?粉丝<\\\\/a>([\\d|万]*).*微博<\\\\/a>([\\d|万]*)");

				Matcher matcher = pattern.matcher(line);
				if (matcher.find()){
					user = new WeiboUser(uid,matcher.group(1),matcher.group(2),matcher.group(3));
					WeiboUser.userList.add(user);
				}
				line = br.readLine();
			}
			br.close();
			long endTime = System.currentTimeMillis();
			//System.out.println("Get a user in thread:" +(endTime-startTime));
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			// 释放连接
		getMethod.releaseConnection();
		}
	}

}
