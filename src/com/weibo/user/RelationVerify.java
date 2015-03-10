/**
 * 
 */
package com.weibo.user;

import java.io.File;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.FileProcess;
import com.weibo.common.PageGet;
import com.weibo.login.Login;
import com.weibo.thread.PageLineThread;

/**
 * @author CHEN Kan
 * @date 2013年12月10日
 ***/

public class RelationVerify {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HttpClient client = Login.login();
		File dir = new File("E:\\Sina\\users\\relations");
		File[] files = dir.listFiles();
		RelationVerify re = new RelationVerify(client);
		for(File sFile:files){
			re.verify(sFile);
		}

	}

	private HttpClient client;
	private String userDir = "E:\\Sina\\users\\userInfo";
	private String newDir = "E:\\Sina\\users\\rusers";
	
	public RelationVerify(HttpClient client){
		this.client = client;
	}
	public void verify(File sFile){
		List<String> list = FileProcess.read(sFile);
		int count = 0;
		UThread[] threads = new UThread[list.size()];
		for(String info:list){
			String uid = info.substring(0, info.indexOf(","));
			if(new File(userDir+"\\"+uid+".sn").exists()||new File(newDir+"\\"+uid).exists()){
				continue;
			}
			String url = "http://weibo.com/aj/user/newcard?type=1&mark=&id="+uid;
			HttpGet get = new HttpGet(url);
			String dFile = newDir+"\\"+uid;
			threads[count++] = new UThread(client,get,dFile);
		}
		for(int i=0;i<count;i++)
			threads[i].run();
		for(int i=0;i<count;i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
