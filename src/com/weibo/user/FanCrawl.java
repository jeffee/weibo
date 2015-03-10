/**
 * 抓取用户的粉丝列表
 */
package com.weibo.user;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.FileProcess;
import com.weibo.common.PageGet;
import com.weibo.login.Login;
import com.weibo.thread.FollowThread;
import com.weibo.thread.PageLineThread;

/**
 * @author CHEN Kan
 * @date 2013年9月24日
 ***/

public class FanCrawl {

	public static void main(String[] args) {
		HttpClient client = Login.login();

		FanCrawl crawl = new FanCrawl(client);
		File rDir = new File("E:\\Sina\\users\\crawl");
		File[] sFiles = rDir.listFiles();
		for(File sFile:sFiles){
			List<String> list = FileProcess.read(sFile);
			for(String line:list){
				String uid = line.substring(0,line.indexOf(","));
				
				crawl.crawl(uid);
				System.out.println(uid+"  finished!");
			}
		}
/*		List<String> list = FileProcess.read(new File("E:\\Sina\\all\\normal\\reposturl.sn"));

		String line = list.toString();
		Pattern pattern = Pattern.compile("http://weibo.com/(\\d*)/");
		Matcher matcher = pattern.matcher(line);
		while(matcher.find()){
			String uid = matcher.group(1);
			System.out.println("Prepared to crawl from "+uid);
			crawl.crawl(uid);
		}*/
		//crawl.crawl("3691837237");
	}
	
	private HttpClient client;

	
	public FanCrawl(HttpClient client){
		this.client = client;
		
	}
	
	private String getPageID(String uid) {
		String pageID = "";
		String url = "http://weibo.com/" + uid;
		List<String> list = PageGet.getPage(client, url);
		for (String info : list) {
			if (info.startsWith("$CONFIG['page_id']")) {
				int sIndex = info.indexOf("=") + 2;
				int eIndex = info.lastIndexOf("'");
				pageID = info.substring(sIndex, eIndex);
				return pageID;
			} 
		}
		return pageID;
	}


	
	public void crawl(String uid) {
		String pageID = getPageID(uid);	
		if(pageID.equals("")||new File(CommDir.userInfoDir+"\\"+uid+".sn").exists())   				//用户已经被删除或已存在
			return;
		
		int pageCount = 5;	
		FollowThread[] threads = new FollowThread[10];
		int isFirst = 1;
		for(int i=1;i<=pageCount;i++){

			String fanUrl = "http://weibo.com/p/"+pageID+"/follow?relate=fans&page="+i+"#place";
			String followUrl = "http://weibo.com/p/"+pageID+"/follow?page="+i+"#place";
			HttpGet fanGet = new HttpGet(fanUrl);
			HttpGet followGet = new HttpGet(followUrl);
			String uFile = CommDir.userInfoDir+"\\"+uid+".sn";
			String rfanFile = CommDir.relationDir+"\\"+uid+"-fan-"+i;
			String rfolFile = CommDir.relationDir+"\\"+uid+"-follow-"+i;
			if(isFirst==1){
				threads[2*i-2] = new FollowThread(client,fanGet,uFile,1,rfanFile);
				isFirst = 0;
			}
			else threads[2*i-2] = new FollowThread(client,fanGet,uFile,0,rfanFile);
			threads[2*i-1] = new FollowThread(client,followGet,uFile,0,rfolFile);
		}
		
		for (int j = 0; j < pageCount*2; j++) {
			threads[j].run();
		}

		for (int j = 0; j < pageCount*2; j++) {
			try {
				threads[j].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
