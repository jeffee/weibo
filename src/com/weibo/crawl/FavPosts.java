/**
 * 
 */
package com.weibo.crawl;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;

import com.weibo.common.PageGet;
import com.weibo.login.Login;

/**
 * @author CHEN Kan
 * @date 2013年12月1日
 ***/

public class FavPosts {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HttpClient client = Login.login();
		List<String> hrefList = new LinkedList<String>();
		for(int i=1;i<=5;i++){
			String url = "http://weibo.com/fav?page="+i;
			FavPosts.getHrefs(client, url);
		}
	}

	public static void getHrefs(HttpClient client, String url){
		String page = PageGet.getStartLine(client, url, "<script>STK && STK.pageletM && STK.pageletM.view({\"pid\":\"pl_content_myFavoritesList");
		Pattern pattern = Pattern.compile("allowForward=1&url=(.*?)&");
		Matcher matcher = pattern.matcher(page);
		while(matcher.find()){
			String href = matcher.group(1).replaceAll("\\\\", "");
			System.out.println(href);
		}
	}
}
