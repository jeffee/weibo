/**
 * 
 */
package com.weibo.repost;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import java.util.regex.Matcher;
import com.weibo.common.FileProcess;
import com.weibo.login.Login;

/**
 * @author CHEN Kan
 * @date 2013年8月30日
 ***/

public class GetFromFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HttpClient client = Login.login();
	
		String dir =  "E:\\Sina\\all\\normal\\comment pages";
		File sFile = new File("E:\\Sina\\all\\normal\\reposturl.sn");
		GetFromFile get = new GetFromFile(client, dir);
		get.getByUrl(sFile);
	}

	private HttpClient client;
	private String baseDir;
	
	public GetFromFile(HttpClient client, String baseDir){
		this.client = client;
		this.baseDir = baseDir;
	}
	public void getByUrl(File sFile){
		List<String> list = FileProcess.read(sFile);
		for(int i=0;i<list.size();i++){
			String url = list.get(i);
			Pattern pattern = Pattern.compile("com/(\\d*)/");
			Matcher matcher = pattern.matcher(url);
			if(matcher.find()){
				String dir = baseDir+"\\" +matcher.group(1)+"\\"
						+ url.substring(url.length() - 6);
				if (FileProcess.checkDir(dir) == false) {
					RepostPageCrawl re = new RepostPageCrawl(client, dir);
					re.getAll(url);
				}	
			}
		}
	}
	
	public void getByInfos(File sFile){
		List<String> list = FileProcess.read(sFile);
		for(int i=0;i<list.size();i++){
			String str = list.get(i);
			String[] infos = str.split(" ");
			if(Integer.parseInt(infos[2])>100){
				String url = infos[5];
				String dir = baseDir +"\\"
						+ url.substring(url.length() - 6);
				if (FileProcess.checkDir(dir) == false) {
					CommentPageCrawl re = new CommentPageCrawl(client, dir);
					re.getAll(url);
				}
			}
		}
	}
}
