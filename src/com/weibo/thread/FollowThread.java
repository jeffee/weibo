/**
 * 根据用户的关注或粉丝列表抓取关注用户/粉丝信息
 */
package com.weibo.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.FileProcess;

/**
 * @author Jeffee
 * 
 */
public class FollowThread extends PageThread {
	private String rFile;
	private int isFirst;
	
	/***uFile: 用户数据存放目录
	 * rFile:关系数据存放目录
	 * ***/
	public FollowThread(HttpClient client, HttpGet get, String uFile, int isFirst, String rFile) {
		super(client, get, uFile);
		//System.out.println(get.getURI());
		this.rFile = rFile;
		this.isFirst = isFirst;
	}

	protected void analyse(BufferedReader br) {
		// line = new JSONTokener(line).nextValue().toString();
		String line;
		//System.out.println(rFile);
		try {
			line = br.readLine();
			while (line != null) {
				if(line.startsWith("<script>FM.view({\"ns\":\"pl.header.head.index")&&isFirst==1)
					FileProcess.write(dFile, line);
				else if (line.startsWith("<script>FM.view({\"ns\":\"pl.content.followTab.index")) {
					List<String> list = new LinkedList<String>();
					Pattern pattern = Pattern
							.compile("class=\\\\\"connect.*?href=\\\\\"\\\\/(\\d*?)\\\\.*?(\\d*?)<.*?>(\\d*?)<");
					Matcher matcher = pattern.matcher(line);		
					while (matcher.find()) {
						String info = matcher.group(1)+","+matcher.group(2)+","+matcher.group(3);
						list.add(info);
					}
					FileProcess.write(rFile, list);
					
					break;
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
