/**
 * 抓取用户的所有微博
 */
package com.weibo.crawl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;

import com.weibo.common.FileProcess;
import com.weibo.common.PageGet;
import com.weibo.login.Login;

/**
 * @author CHEN Kan
 * @date 2013年9月23日
 ***/

public class AllPosts {

	public static void main(String[] args) {
		HttpClient client = Login.login();
		String baseDir = "E:\\Sina\\all\\normal\\userpages";
		AllPosts all = new AllPosts(client, baseDir);
		
		/*	
		File sFile = new File("E:\\Sina\\all\\normal\\uid.txt");
		List<String> list = FileProcess.read(sFile);
		for(String line:list){
			String[] infos = line.split(" ");
			if(!new File(baseDir+"\\"+infos[0]).exists()){
			
			all.crawlByUid(infos[0], 2);
			}
		}*/
		
		File sFile = new File("E:\\Sina\\all\\normal\\uid.txt");
		List<String> list = FileProcess.read(sFile);
		for(String pageID:list){
			int pageCount = 5;
			all.crawlByPageID(pageID,pageCount,0);
			System.out.println(pageID +" has finished!");
		}
	
	}

	private List<String> idMap;
	private String uid;
	private String pageID;
	private String baseDir;
	
	/**用户类型，1表示企业用户，0表示个人用户***/
	private int userType;
	//private int pageCount;
	private HttpClient client;
	
	public AllPosts(HttpClient client, String baseDir){
		this.client = client;
		this.baseDir = baseDir;
		idMap= new LinkedList<String>();
	}
	
	public void crawlByUid(String userID, int count){
		uid = userID;
		preSet();
		PostCrawl all = new PostCrawl(client, pageID, baseDir+"\\"+userID,userType);
		if(all.checkDir())
			all.getOriginPosts(count);
	}
	public void crawlByPageID(String pageID,int pageCount,int type){
		PostCrawl all = new PostCrawl(client, pageID,baseDir,type);
		if(all.checkDir())
			all.getAllPosts(pageCount);
	}

	/***根据用户id设置参数**/
	private void preSet(){
		String url = "http://weibo.com/"+uid;
		List<String> list = PageGet.getPage(client, url);
		for(String info:list){
			if(info.startsWith("$CONFIG['page_id']")){
				int sIndex = info.indexOf("=")+2;
				int eIndex = info.lastIndexOf("'");
				pageID = info.substring(sIndex, eIndex);
				System.out.println(pageID);
				idMap.add(uid+","+ pageID);
			}else if(info.startsWith("$CONFIG['domain']=")){
				if(info.indexOf("100606")!=-1)
					userType = 1;
				return;
			}
/*			else if(info.startsWith("<script>FM.view({\"ns\":\"pl.header.head.index")){
				info = info.replaceAll("\\\\", "");
				Pattern pattern = Pattern.compile("=\"weibo\">(\\d*)<");
				Matcher matcher = pattern.matcher(info);
				if(matcher.find()){
					int count = Integer.parseInt(matcher.group(1));
					pageCount = count/45;
					
					if(pageCount>3)
						pageCount=3;
					System.out.println(pageCount);
				}
			}*/
		}
	}
}
