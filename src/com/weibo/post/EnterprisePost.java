/**
 * 企业用户所有的微博抓取
 * 企业用户的微博地址有两类，一类使用uid，如"http://e.weibo.com/u/3186802144?page=1"
 * 一类使用pageID，如"http://weibo.com/p/1006063304291130/"
 * 程序处理后一种
 */
package com.weibo.post;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
import com.weibo.login.Login;

/**
 * @author CHEN Kan
 * @date 2013年9月9日
 ***/

public class EnterprisePost {

	
	public static void main(String[] args) {
		HttpClient client = Login.login("social_ck@sina.com","jeffee");
		String pageID = "1006063693554182";
		String baseDir = "E:\\weibo\\spam\\userpages\\" + pageID;
		EnterprisePost all = new EnterprisePost(client, pageID, baseDir);
		all.getOriginalPost(5);

	}
	private String pageID;
	private HttpClient client;
	private String baseDir;
	private String maxID = "";
	private String endID = "";
	private List<String> urlList;
	private List<String> infoList;
	

	public EnterprisePost(HttpClient client, String pid, String baseDir) {
		this.pageID = pid;
		this.client = client;
		this.baseDir = baseDir;
		
		File dir = new File(baseDir);
		if (!dir.exists() || !dir.isDirectory())
			dir.mkdirs();
		urlList = new LinkedList<String>();
		infoList = new LinkedList<String>();
	}
	
	public void getOriginalPost(int pageCount){
		getPosts(1,pageCount,1);
	}
	public void getAllPost(int pageCount){
		getPosts(1,pageCount,0);
	}
	/**
	 * 抓取所有微博
	 * @param count 抓取的页数
	 * 所有微博的url：
	 * "http://weibo.com/p/1006063304291130/"
	 * 原创微博的url
	 * ："http://weibo.com/p/1006063304291130?is_ori=1"
	 * 
	 * ***/
	private void getPosts(int start, int end, int origin) {
		for (int i = start; i <= end; i++) {
			getFirstParagraph(i,origin);
			getRest(i);
			System.out.println(i);
		}
		FileProcess.write(baseDir+"\\allPosts", urlList);
		FileProcess.write(baseDir+"\\infos", infoList);
	}


	/** 抓取一页中的第一部分微博，（最初显示的15条）
	 * @param pageNum 需要抓取的页面的页码
	 * @param origin 是否只抓取原创内容
	 *  **/
	private void getFirstParagraph(int pageNum, int origin) {
		String url = "http://weibo.com/p/"+pageID+"/feed?is_ori="+origin+"&page="+pageNum;
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent(), "utf8"));
			String line = br.readLine();

			while (line != null) {
				if (line.startsWith("<script>FM.view({\"ns\":\"pl.header.head.index")) {
					String fName = "E:\\Sina\\user\\" + pageID;
					FileProcess.write(fName, line); // 个人信息
				} else if (line.startsWith("<script>STK && STK.pageletM && STK.pageletM.view({\"pid\":\"pl_content_hisFeed")
						|| line.startsWith("<script>FM.view({\"ns\":\"pl.content.homeFeed")) {
					extract(line);
					String fileName = baseDir + "\\info-" + pageNum + "-1";
					
					FileProcess.write(fileName, line);
					System.out.println(fileName+"write");
					break;
				}
				line = br.readLine();
			}
			br.close();
			get.abort();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 抓取一页中的第二三部分微博，（需要加载显示的30条） //默认只显示页面的1/3内容，15条微博，需要再循环两次抓取
	 * //抓取时生成地址需要用到max_id和endid，其中max_id为显示的最后一条微博的mid
	 * //endid为显示的第一条微博的ID，即从endid开始计数，第15条显示的为max_id //同一次抓取，微博的endid都相同
	 * //地址类似于
	 * "http://weibo.com/p/aj/mblog/mbloglist?domain=100505&pre_page=2&page=2&max_id=3475339705570090&end_id=3476099474253588&count=15&pagebar=0&pl_name=Pl_Official_LeftProfileFeed__11&id=1005051195403385&script_uri=/p/1005051195403385/weibo&feed_type=0&is_search=0&visible=0&is_ori=1&is_tag=0&profile_ftype=1"
	 * "http://e.weibo.com/aj/mblog/mbloglist?page=1&pre_page=1&count=15&max_id=3611517859699880&end_id=3615962626922784&pagebar=1&uid=3186802144&_t=0"
	 * **/
	private void getRest(int pageNum) {
		int i = 2;
		while (i <= 3) {
			String url = "http://e.weibo.com/aj/mblog/mbloglist?page="
					+ pageNum
					+ "&pre_page="
					+ pageNum
					+ "&count=15&max_id="
					+ maxID
					+ "&end_id="
					+ endID
					+ "&pagebar="
					+ (i - 2)
					+ "&uid="
					+ pageID
					+ "&_t=0";
					
			url = "http://weibo.com/p/aj/mblog/mbloglist?domain=100606&pre_page="+pageNum
					+"&page="+pageNum
					+"&max_id="+maxID
					+"&end_id="+endID
					+"&count=15&pagebar="+(i-2)
					+"&max_msign=&filtered_min_id=&pl_name=Pl_Official_LeftProfileFeed__28&id="+pageID;
			System.out.println(url);
			String line = PageGet.getJasonPage(client, url);
			extract(line);
			String fileName = baseDir + "\\info-" + pageNum + "-" + i++;
			FileProcess.write(fileName, line);
		}
	}

	/***
	 * 对微博内容进行分析，抽取其中的endID和maxID，并将微博地址加入到urlList中
	 * 其中endID只在第一次进行分析时赋值，以后保持不变（显示的第一条微博的mid） maxID随着抓取不断改变（最后显示的一条微博的mid）
	 * 对每页第一部分进行解析时没有把unicode转换为汉字，第二三部分都进行了转换
	 * **/
	private void extract(String line) {
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
	}
}
