/**
 * 抓取用户的所有的微博
 * 并将微博地址存在文件中
 * 微博的具体转发需要使用repost包进行抓取
 * 如果用户的pageID的文件夹已存在，则不会重复抓取
 * @update time 2013.8.29
 * 
 */
package com.weibo.crawl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.Common;
import com.weibo.common.FileProcess;
import com.weibo.common.PageGet;
import com.weibo.common.Post;
import com.weibo.login.Login;
import com.weibo.parse.PostParse;

/**
 * @author Jeffee
 * 
 */
public class PostCrawl {

	public static void main(String[] args) {
		HttpClient client = Login.login("social_ck@sina.com", "jeffee");
		String pageID = "1006063304867791";
		String baseDir = "E:\\weibo\\spam\\userpages\\" + pageID;
		int pageCount = 5;
		PostCrawl crawl = new PostCrawl(client,pageID,baseDir,0);
		crawl.getAllPosts(pageCount);
/*		List<String> list = FileProcess.read(new File(
				"E:\\Sina\\influence\\users.txt"));
		for (String line : list) {
			String[] infos = line.split(",");
			String pageID = infos[0].trim();
			String baseDir = "E:\\Sina\\influence\\normal\\" + pageID;
			PostCrawl all = new PostCrawl(client, pageID,baseDir);
			if (all.checkDir())
				all.getOriginPosts(2);
			System.out.println("Finished!");
		}*/

	}

	private String pageID;
	private HttpClient client;
	private String baseDir;
	private String maxID = "";
	private String endID = "";
	private int userType = 0;	//0表示个人用户，1表示企业用户
	
	public PostCrawl(HttpClient client, String pageid, String baseDir,int type) {
		this.pageID = pageid;
		this.client = client;
		this.baseDir = baseDir;
		this.userType = type;
	}
	
	/**查看是否需要新建文件夹，需要则返回true
	 * 在抓取之前需要调用
	 * **/
	public boolean checkDir(){
		//System.out.println(baseDir);
		File dir = new File(baseDir);
		if (!dir.exists() || !dir.isDirectory()){
			dir.mkdirs();
			return true;
		}
		return false;
	}

	public void getOriginPosts(int end) {
		getPosts(1,end,1);
		//flush();
	}
	
	public void getOriginPosts(int start, int end) {
		getPosts(start,end,1);
		//flush();
	}
	
	public void getAllPosts(int end){
		getPosts(1,end, 0);
		//flush();
	}
	public void getAllPosts(int start, int end){
		getPosts(start,end, 0);
		//flush();
	}
	
	
	/**
	 * 抓取所有微博
	 * @param count 抓取的页数
	 * 所有微博的url：
	 * "http://weibo.com/p/1005051195403385/weibo?from=page_100505_home&wvr=5.1&mod=weibomore"
	 * 原创微博的url
	 * ："http://weibo.com/p/1005051195403385/weibo?profile_ftype=1&is_ori=1#_0"
	 * 
	 * ***/
	private void getPosts(int start, int end, int origin) {
		for (int i = start; i <= end; i++) {
			getFirstParagraph(i,origin);
			getRest(i,origin);
		}
	}


	/** 抓取一页中的第一部分微博，（最初显示的15条） **/
	private void getFirstParagraph(int pageNum, int origin) {
		long btime = System.currentTimeMillis();
		String url = "";
		if(userType==0)
			url = "http://weibo.com/p/"
				+ pageID
				+ "/weibo?profile_ftype=0&page="+pageNum+"&is_ori="+origin+"&from=page_100505_home&wvr=5.1&mod=originalweibo#place";
		else
			url = "http://weibo.com/p/"+pageID+"/feed?profile_ftype=1&is_ori=1#_0";
		
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		boolean hasFound = false;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent(), "utf8"));
			String line = br.readLine();
			while (line != null) {
				//System.out.println(line);
				if (line.startsWith("<script>FM.view({\"ns\":\"pl.header.head.index")) {
					String fName = "E:\\Sina\\user\\" + pageID;
					FileProcess.write(fName, line); // 个人信息
				} else if (line
						.startsWith("<script>FM.view({\"ns\":\"pl.content.homeFeed.index")) {
					setParams(line);
					String fileName = baseDir + "\\info-" + pageNum + "-1";
					FileProcess.write(fileName, line);
					hasFound = true;
					break;
				}
				line = br.readLine();
			}
			if(!hasFound)
				System.err.println(pageID+"  not found!");
			br.close();
			get.abort();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long etime = System.currentTimeMillis();
		System.out.println("all: "+(etime-btime));
	}

	/**
	 * 抓取一页中的第二三部分微博，（需要加载显示的30条） //默认只显示页面的1/3内容，15条微博，需要再循环两次抓取
	 * //抓取时生成地址需要用到max_id和endid，其中max_id为显示的最后一条微博的mid
	 * //endid为显示的第一条微博的ID，即从endid开始计数，第15条显示的为max_id //同一次抓取，微博的endid都相同
	 * //地址类似于
	 * "http://weibo.com/p/aj/mblog/mbloglist?domain=100505&pre_page=2&page=2&max_id=3475339705570090&end_id=3476099474253588&count=15&pagebar=0&pl_name=Pl_Official_LeftProfileFeed__11&id=1005051195403385&script_uri=/p/1005051195403385/weibo&feed_type=0&is_search=0&visible=0&is_ori=1&is_tag=0&profile_ftype=1"
	 * 
	 * **/
	private void getRest(int pageNum,int origin) {
		int i = 2;
		while (i <= 3) {
			String url = "http://weibo.com/p/aj/mblog/mbloglist?domain=100505&pre_page="
					+ pageNum
					+ "&page="
					+ pageNum
					+ "&max_id="
					+ maxID
					+ "&end_id="
					+ endID
					+ "&count=15&pagebar="
					+ (i - 2)
					+ "&pl_name=Pl_Official_LeftProfileFeed__11&id="
					+ pageID
					+ "&script_uri=/p/"
					+ pageID
					+ "/weibo&feed_type=0&is_search=0&visible=0&is_ori="+origin+"&is_tag=0&profile_ftype=1";

			String line = PageGet.getJasonPage(client, url);
			if(setParams(line)){
				String fileName = baseDir + "\\info-" + pageNum + "-" + i++;
				FileProcess.write(fileName, line);
			} else break;
		}
	}

	/***根据内容设置endID和maxID参数
	 * 返回false则说明没有找到对应参数，（页面不存在，直接退出）
	 * **/
	private boolean setParams(String line){
		//System.out.println(line);
		int index=-1;
		if(endID.equals("")){
			index = line.indexOf(" mid");
			endID = line.substring(index+7,index+23);
		}
		index = line.lastIndexOf(" mid");
		if(index==-1)
			return false;
		maxID = line.substring(index+7);
		index = maxID.indexOf("\\");
		maxID = maxID.substring(0, index-1);
		return true;
	}
	/***
	 * 对微博内容进行分析，抽取其中的endID和maxID，并将微博地址加入到urlList中
	 * 其中endID只在第一次进行分析时赋值，以后保持不变（显示的第一条微博的mid） maxID随着抓取不断改变（最后显示的一条微博的mid）
	 * **/
	/*private void extract(String line) {
		//System.out.println(line);
		Pattern pattern = Pattern
				.compile("allowForward=1&url=(.*?)&mid=(\\d*)&.*?(?:\\\\u8f6c\\\\u53d1|转发)\\(?(.*?)\\)?<.*?(?:\\\\u6536\\\\u85cf|收藏)\\(?(.*?)\\)?<.*?(?:\\\\u8bc4\\\\u8bba|评论)\\(?(.*?)\\)?<");
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			int repostCount = Common.getNum(matcher.group(3));
			int favoCount = Common.getNum(matcher.group(4));
			int commentCount = Common.getNum(matcher.group(5));
			String url = matcher.group(1).replace("\\", "");
			urlList.add(url);
			if (endID.equals(""))
				endID = matcher.group(2);
			maxID = matcher.group(2);
			String info = maxID+" "+pageID+" "+repostCount+" "+favoCount+" "+commentCount+" "+url;
			//System.out.println(info);
			infoList.add(info);
		}
	}*/
	

}
