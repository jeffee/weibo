/**
 * 
 */
package com.weibo.parse;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CHEN Kan
 * @date 2013年12月7日
 ***/

public class FanParse {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static List<String> parse(String line){
		List<String> list = new LinkedList<String>();
		Pattern pattern = Pattern
				.compile("class=\\\\\"connect.*?href=\\\\\"\\\\/(\\d*?)\\\\.*?(\\d*?)<.*?>(\\d*?)<");
		Matcher matcher = pattern.matcher(line);
	
		while (matcher.find()) {
			String info = matcher.group(1)+","+matcher.group(2)+","+matcher.group(3);
			list.add(info);
		}
		return list;
	}
}
