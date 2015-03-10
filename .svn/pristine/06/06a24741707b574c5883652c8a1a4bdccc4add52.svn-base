/**
 * 解析转发内容
 * Jeffee2013-9-132013
 */
package com.weibo.parse;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.weibo.common.FileProcess;
import com.weibo.common.Post;

/**
 * @author Jeffee
 *
 */
public class RepostParse {

	public static void main(String[] args) {
		long sTime = System.currentTimeMillis();
		String info = FileProcess.readLine(new File("E:\\Sina\\spams\\m7cP1m\\1"));
		List<Post> list = RepostParse.parse(info);
		long eTime = System.currentTimeMillis();
		System.out.println(eTime-sTime);
		System.out.println(list.size());
	}

	public static List<Post> parse(String info){
		List<Post> list = new LinkedList<Post>();
		info = info.replaceAll("\\\\n", "").replaceAll("\\\\t", "").replaceAll("\\\\", "");
		Pattern pattern = Pattern
				.compile("comment_list .*?mid=\"(\\d*)\".*?<dd><a.*?nick-name=\"(.*?)\" usercard=\"" +
						"id=(\\d*)\">.*?<em>(.*?)</em>.*?[S_txt2|W_textb]\">\\((.*?)\\)<.*?rootmid=(\\d*)&.*?&url=(.*?)&");
		
		Matcher matcher = pattern.matcher(info);
		while (matcher.find()) {
			Post post = new Post();
			post.setMid(matcher.group(1));
			post.setUserName(matcher.group(2));
			post.setUid(matcher.group(3));
			post.setContent(getContent(matcher.group(4)));
			post.setDate(matcher.group(5));
			post.setRootMid(matcher.group(6));
			post.setHref(matcher.group(7));
			post.setOri(0);
			list.add(post);
		}
		return list;
	}
	
	private static String getContent(String info){
		int sIndex = info.indexOf("<");
		int eIndex = info.indexOf(">");
		while(sIndex!=-1 && eIndex!=-1){
			info = info.substring(0,sIndex) + info.substring(eIndex+1);
			sIndex = info.indexOf("<");
			eIndex = info.indexOf(">");
		}
		return info;
	}
}
