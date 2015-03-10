/**
 * 抓取用户关注的用户
 */
package com.weibo.crawl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;

import com.weibo.common.PageGet;
import com.weibo.login.Login;

/**
 * @author CHEN Kan
 * @date 2013年12月4日
 ***/

public class Focuser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HttpClient client = Login.login();
		Focuser foc = new Focuser(client);
		String url = "http://weibo.com/2751730300/myfollow?t=1&gid=3651586851441361&page=2";
		foc.crawl(url);

	}
	public Focuser(HttpClient client){
		this.client = client;
	}
	private HttpClient client;
	
	public void crawl(String url){
		String line = PageGet.getStartLine(client, url, "<script>STK && STK.pageletM && STK.pageletM.view({\"pid\":\"pl_relation_myfollow");
		Pattern pattern = Pattern.compile("action-data=\\\\\"uid=(\\d*?)&");
		Matcher matcher = pattern.matcher(line);
		while(matcher.find()){
			String uid = matcher.group(1);
			System.out.println(uid);
		}
			
	}
}
