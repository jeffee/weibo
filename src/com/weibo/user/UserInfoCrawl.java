/**
 * 多线程抓取用户信息
 */
package com.weibo.user;

import java.io.File;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.FileProcess;
import com.weibo.login.Login;
import com.weibo.thread.UserInfoThread;

/**
 * @author CHEN Kan
 * @date 2014年1月14日
 ***/

public class UserInfoCrawl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File sDir = new File("E:\\Sina\\users\\crawl");
		HttpClient client = Login.login();
		UserInfoCrawl crawl = new UserInfoCrawl(client);
		crawl.crawlFromDir(sDir);
	}

	public UserInfoCrawl(HttpClient client) {
		this.client = client;
	}

	private HttpClient client;

	public void crawlFromDir(File sDir) {
		File[] sFiles = sDir.listFiles();
		for (File sFile : sFiles)
			crawl(sFile);

	}

	public void crawl(File sFile) {
		List<String> list = FileProcess.read(sFile);
		UserInfoThread[] threads = new UserInfoThread[20];
		int count = 0;
		for (String info : list) {
			String uid = info.substring(0, info.indexOf(","));

			String url = "http://weibo.com/" + uid;
			HttpGet get = new HttpGet(url);
			String uFile = CommDir.userInfoDir + "\\" + uid + ".sn";
			if (!(new File(uFile).exists()))
				threads[count++] = new UserInfoThread(client, get, uFile);
		}
		for (int i = 0; i < count; i++) {
			threads[i].run();
		}

		for (int i = 0; i < count; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(sFile.getName()+"  is  finished! Haha~~");
	}
}
