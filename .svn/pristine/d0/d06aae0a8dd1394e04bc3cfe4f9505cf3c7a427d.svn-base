/**
 * 提供常用的抓取功能
 */
package com.weibo.crawl;

import java.util.List;

import org.apache.http.client.HttpClient;

import com.weibo.common.PageGet;
import com.weibo.login.Login;

/**
 * @author CHEN Kan
 * @date 2013年11月29日
 ***/

public class CommonCrawl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HttpClient client = Login.login();
		System.out.println(CommonCrawl.getPageID(client, "3533838331"));

	}

	/**根据用户的uid获取pageid****/
	public static String getPageID(HttpClient client, String uid){
		String url = "http://weibo.com/"+uid;
		List<String> list = PageGet.getPage(client, url);
		for(String info:list){
			if(info.startsWith("$CONFIG['page_id']")){
				int sIndex = info.indexOf("=")+2;
				int eIndex = info.lastIndexOf("'");
				return info.substring(sIndex, eIndex);
				
			}
		}
		return "";
	}
}
