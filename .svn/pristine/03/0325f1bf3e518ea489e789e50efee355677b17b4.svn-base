/**
 * 抓取一页中的以特征字符串起始的行
 */
package com.weibo.thread;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.FileProcess;

/**
 * @author CHEN Kan
 * @date 2013年10月5日
 ***/

public class PageLineThread extends PageThread{

	private String str;
	public PageLineThread(HttpClient client, HttpGet get, String dFile, String startStr) {
		super(client, get, dFile);
		this.str = startStr;
	}

	protected void analyse(BufferedReader br) {
		String line;
		try {
			line = br.readLine();
			while (line != null) {
				if (line.startsWith(str)) {
					FileProcess.write(dFile, line);
					break;
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
