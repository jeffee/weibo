/**
 * 抓取用户信息的线程，直接将用户信息存储到文件
 */
package com.weibo.thread;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.FileProcess;

/**
 * @author CHEN Kan
 * @date 2013年9月24日
 ***/

public class UserInfoThread extends PageThread{

	public UserInfoThread(HttpClient client, HttpGet get, String dFile) {
		super(client, get, dFile);
	}

	protected void analyse(BufferedReader br) {
		String line;
		try {
			line = br.readLine();
			while (line != null) {
				if (line.startsWith("<script>FM.view({\"ns\":\"pl.header.head.index")) {
					FileProcess.write(dFile, line);

					break;
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
