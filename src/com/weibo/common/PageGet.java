/**
 * 根据传入的client和页面地址读取页面
 */
package com.weibo.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.util.JSONTokener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

/**
 * @author Jeffee
 * 
 */
public class PageGet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


	}
	

	/***读取jason页，页面内容只有一行**/
	public static String getJasonPage(HttpClient client, String url) {
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		String line="";
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent(), "utf8"));
			line = br.readLine();
			if(line.startsWith("try")){
				int index = line.lastIndexOf("catch");
				line = line.substring(5, index);
			}
		} catch (IllegalStateException|IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		line = new JSONTokener(line).nextValue().toString();
		return line;
	}
	
	/***读取页面，返回以特征字符串开始的行**/
	public static String getStartLine(HttpClient client, String url, String startStr) {
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		String line="";
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent(), "utf8"));
			line = br.readLine();
			while(line!=null){
				if(line.startsWith(startStr))
					break;
				else
					line = br.readLine();
			}
			br.close();
		} catch (IllegalStateException|IOException e) {
			e.printStackTrace();
		} 
		
		return line;
	}
	
	/***读取页面，返回与特征字符串匹配的行
	 * 有多行匹配时，只返回第一行
	 * **/
	public static String getPatternLine(HttpClient client, String url, String patternStr) {
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		String info ="";
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent(), "utf8"));
			String	line = br.readLine();
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(line);
			
			if(matcher.find()){
				info = matcher.group(1);
			}
		} catch (IllegalStateException|IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return info;
	}
	
	public static List<String> getPage(HttpClient client, String url){
		List<String> list = new LinkedList<String>();
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), "utf8"));
			String	line = br.readLine();
			while(line!=null){
				list.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (IllegalStateException|IOException e) {
			e.printStackTrace();
		} 
		return list;
	}
}
