/**
 * 
 */
package com.weibo.crawl;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.thread.FollowThread;

/**
 * @author CHEN Kan
 * @date 2013年12月7日
 ***/

public class BasicUserInfo {

	/**
	 * http://weibo.com/aj/user/newcard?type=1&mark=&id=1671526850&_t=1&callback=STK_138640577959944
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private String baseDir;
	/***存放用户信息的根目录**/
	private String userDir;

	/***存放用户关系信息的根目录**/
	private String relationDir;
	
	public BasicUserInfo(){
		this.userDir = "";
		this.relationDir = "";
	}
	public void getUserInfo(HttpClient client, String uid){
		String uFile = userDir+"\\"+uid;
		String rFile = relationDir+"\\"+uid;
		String url = "http://weibo.com/aj/user/newcard?type=1&mark=&id="+uid;
		
		ArrayList<FollowThread> uThreadList = new ArrayList<FollowThread>();
		//for(){
			HttpGet get = new HttpGet(url);
			uThreadList.add(new FollowThread(client, get, uFile, 1,rFile));
		//}
		for(FollowThread uThread:uThreadList){
			uThread.run();
		}
		for(FollowThread uThread:uThreadList){
			try {
				uThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

}
