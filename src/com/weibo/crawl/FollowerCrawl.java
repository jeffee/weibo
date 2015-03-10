/**
 * 抓取用户的关注列表
 */
package com.weibo.crawl;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.FileProcess;
import com.weibo.common.IDMap;
import com.weibo.common.PageGet;
import com.weibo.login.Login;
import com.weibo.thread.PageLineThread;

/**
 * @author CHEN Kan
 * @date 2013年9月24日
 ***/

public class FollowerCrawl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HttpClient client = Login.login();
		String baseDir = "E:\\Sina\\all\\spams\\fans";
		FollowerCrawl crawl = new FollowerCrawl(client,baseDir);
		List<String> list = FileProcess.read(new File("E:\\Sina\\spams\\repost.sn"));
		String line = list.toString();
		Pattern pattern = Pattern.compile("http://weibo.com/(\\d*)/");
		Matcher matcher = pattern.matcher(line);
		while(matcher.find()){
			String uid = matcher.group(1);
			System.out.println("Prepared to crawl from "+uid);
			String dir = crawl.baseDir+"\\"+uid;
			if(!FileProcess.checkDir(dir))
				crawl.crawl(uid);
		}
		//crawl.crawl("1005051708942053");
	}

	private HttpClient client;
	private String baseDir;

	
	public FollowerCrawl(HttpClient client,String dir){
		this.client = client;
		baseDir = dir;
	}
	/*private String getPageID(String uid) {
		String pageID = IDMap.getPageID(uid);
		if(pageID!=null)
			return pageID;
		String url = "http://weibo.com/" + uid;
		String info = PageGet.getStartLine(client, url, "$CONFIG['page_id']");
		if(info==null)
			return null;
		int sIndex = info.indexOf("=") + 2;
		int eIndex = info.lastIndexOf("'");
		pageID = info.substring(sIndex, eIndex);
		IDMap.put(uid, pageID);
		System.out.println(pageID);
		return pageID;
	}*/

	public int getPageCount(String pageID){
		String url = "http://weibo.com/p/"+pageID+"/follow";
		System.out.println("1");
		String line = PageGet.getStartLine(client, url, "<script>FM.view({\"ns\":\"pl.content.followTab.index");
		line = line.substring(0, line.lastIndexOf("Pl_Official_LeftHisRelation"));
		int index = line.lastIndexOf("page=");
		line = line.substring(index+5);
		System.out.println(line);
		return Integer.parseInt(line.substring(0,line.indexOf("#")));
	}
	
	public void crawl(String pageID) {
		if(pageID==null)
			return;
		int pageCount = getPageCount(pageID);
		System.out.println(pageCount);
		PageLineThread[] threads = new PageLineThread[pageCount];
		for(int i=1;i<=pageCount;i++){
			String url = "http://weibo.com/p/"+pageID+"/follow?page="+i;
			HttpGet get = new HttpGet(url);
			String dFile = baseDir +"\\"+pageID+"\\"+i+".sn";
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
