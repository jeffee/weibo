/**
 * 抓取用户的粉丝列表
 */
package com.weibo.crawl;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.FileProcess;
import com.weibo.common.PageGet;
import com.weibo.login.Login;
import com.weibo.thread.PageLineThread;

/**
 * @author CHEN Kan
 * @date 2013年9月24日
 ***/

public class FanCrawl {

	public static void main(String[] args) {
		HttpClient client = Login.login();
		String baseDir = "E:\\Sina\\all\\normal\\fans";
		FanCrawl crawl = new FanCrawl(client,baseDir);
		List<String> list = FileProcess.read(new File("E:\\Sina\\all\\normal\\reposturl.sn"));
/*		for(String uid:list){
			System.out.println("Prepared to crawl from "+uid);
			crawl.crawl(uid);
		}*/
		String line = list.toString();
		Pattern pattern = Pattern.compile("http://weibo.com/(\\d*)/");
		Matcher matcher = pattern.matcher(line);
		while(matcher.find()){
			String uid = matcher.group(1);
			System.out.println("Prepared to crawl from "+uid);
			crawl.crawl(uid);
		}
	}
	
	private HttpClient client;
	private String baseDir;
	private String pageID;
	/**用户类型，1为企业用户，0为个人用户**/
	private int userType;
	public FanCrawl(HttpClient client, String baseDir){
		this.baseDir = baseDir;
		this.client = client;
	}
	
	private void init(String uid) {
		String url = "http://weibo.com/" + uid;
		List<String> list = PageGet.getPage(client, url);
		for (String info : list) {
			if (info.startsWith("$CONFIG['page_id']")) {
				int sIndex = info.indexOf("=") + 2;
				int eIndex = info.lastIndexOf("'");
				pageID = info.substring(sIndex, eIndex);
				return;
			} 
		}
	}

	public int getPageCount(String pageID){
		String url = "http://weibo.com/p/"+pageID+"/follow?relate=fans#place";
	
		String line = PageGet.getStartLine(client, url, "<script>FM.view({\"ns\":\"pl.content.followTab.index");
		
		line = line.substring(0, line.lastIndexOf("Pl_Official_LeftHisRelation"));
		int index = line.lastIndexOf("page=");
		if(index==-1)
			return 1;
		line = line.substring(index+5);
		return Integer.parseInt(line.substring(0,line.indexOf("#")));
	}
	
	public void crawl(String uid) {
		init(uid);
		
		if(pageID.equals("")||new File(baseDir +"\\"+uid).exists())   				//用户已经被删除或已存在
			return;
		
		int pageCount = getPageCount(pageID);
		if(pageCount>10)
			pageCount=10;
		System.out.println(pageCount);
		PageLineThread[] threads = new PageLineThread[pageCount];
		for(int i=1;i<=pageCount;i++){
			int page = 10+i;
			String url = "http://weibo.com/p/"+pageID+"/follow?relate=fans&page="+page+"#place";
			HttpGet get = new HttpGet(url);
			String dFile = baseDir +"\\"+uid+"\\"+page+".sn";
			String startStr = "<script>FM.view({\"ns\":\"pl.content.followTab.index";
			threads[i-1] = new PageLineThread(client,get,dFile,startStr);
		}
		for (int j = 0; j < pageCount; j++) {
			threads[j].run();
		}

		for (int j = 0; j < pageCount; j++) {
			try {
				threads[j].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
