/**
 * 抓取用户信息
 */
package com.weibo.crawl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.FileProcess;
import com.weibo.login.Login;
import com.weibo.repost.UserThread;
import com.weibo.thread.UserInfoThread;

/**
 * @author CHEN Kan
 * @date 2013年9月24日
 ***/

public class UserCrawl {

	public static void main(String[] args) {
		List<String> list = FileProcess.read(new File("E:\\Sina\\spams\\spammers.sn"));
		HttpClient client = Login.login(); 
		UserCrawl crawl = new UserCrawl(client);
		crawl.getByIDList(list);
	}

	private HttpClient client;
	private String baseDir;
	
	public UserCrawl(HttpClient client){
		this.client = client;
		baseDir = "E:\\Sina\\spams\\userinfos";
	}

	
	
	/***通过用户列表以线程形式抓取用户数据****/
	public void getByIDList(List<String> uList){
		HttpGet get = null;
		List<UserInfoThread> threadList = new LinkedList<UserInfoThread>();
		for(String uid:uList){
			String url = "http://weibo.com/u/"+uid;
			get = new HttpGet(url);
			String dFile = baseDir+"\\"+uid+".sn";
			threadList.add(new UserInfoThread(client, get, dFile));
		}

		  // start the threads
        for (int j = 0; j < threadList.size(); j++) {
            threadList.get(j).run();
        }

        // join the threads
        for (int j = 0; j < threadList.size(); j++) {
        	try {
				threadList.get(j).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	/**
	 * 获取用户信息
	 * @param httpClient
	 * @param startUrl
	 * @throws InterruptedException
	 */
	public void getUsers(String page)
			throws InterruptedException
	{
		long startTime = System.currentTimeMillis();
		HttpGet get = null;
		
		List<UserThread> threadList = new LinkedList<UserThread>();
		Pattern pattern = Pattern.compile("nick-name=.*?usercard=\\\\\"id=(\\d*)");
		Matcher matcher = pattern.matcher(page);
		while(matcher.find()){
			String uid = matcher.group(1);
			String url = "http://weibo.com/aj/user/cardv5?_wv=5&type=1&mark=&id="
					+ uid + "&_t=0";
			get = new HttpGet(url);
			threadList.add(new UserThread(client, get, uid));
		}

		  // start the threads
        for (int j = 0; j < threadList.size(); j++) {
            threadList.get(j).run();
        }

        // join the threads
        for (int j = 0; j < threadList.size(); j++) {
        	threadList.get(j).join();
        }
        
		long endTime = System.currentTimeMillis();
		System.out.println("Get all users in page:" + (endTime-startTime));
	}
}
