/**
 * 获取关注用户
 * 自定义baseDir的值
 * 1. 在baseDir下建立origin文件夹
 * 2. 在origin文件夹下建立userid.txt,每一行为一个用户的ID+空格+用户关注好友数量
 * 3. 调用getFriends()
 * 
 */
package com.weibo.relation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.FileProcess;
import com.weibo.common.PageGet;
import com.weibo.login.Login;
import com.weibo.thread.FollowerThread;

/**
 * @author Jeffee
 *
 */
public class Friends {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HttpClient client = Login.login();
		Friends friend = new Friends(client);
		//friend.loop();
		/*try {
			friend.getFriends("1266321801", 589);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		friend.getFriends();
	}
	
	private HttpClient client;
	private String baseDir;
	public Friends(HttpClient client){
		baseDir = "E:\\Sina\\users";
		this.client = client;
	}
	
	/***根据用户ID获得用户的pageID，关注列表的地址需要pageID**/
	private void getPageID(){
		File sFile = new File(baseDir + "\\origin\\userid.txt");
		File dFile = new File(baseDir+"\\origin\\pageid.txt");
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dFile)));
			List<String> list = FileProcess.read(sFile);
			for(int i=0;i<list.size();i++){
				String info = list.get(i).trim();
				String userID = info.split(" ")[0].trim();
				String url = "http://weibo.com/"+userID;
				System.out.println(url);
				String line = PageGet.getStartLine(client, url, "$CONFIG['page_id");
				if(line==null)
					continue;
				line = line.trim();
				int index = line.indexOf("='")+2;
				String pageID = line.substring(index, line.length()-2);
				System.out.println(pageID);
				writer.write(pageID + " "+info);
				writer.newLine();
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/***根据origin目录下的userid.txt文件批量获取关注列表
	 * pageID.txt为程序生成，格式为
	 * pageID+userID+关注数量
	 * 以空格隔开
	 * **/
	public void getFriends(){
		getPageID();
		File sFile = new File(baseDir+"\\origin\\pageid.txt");
		List<String> list = FileProcess.read(sFile);
		for(int i=0;i<list.size();i++){
			String[] infos = list.get(i).trim().split(" ");
			try {
				getFriends(infos[0],infos[1],Integer.parseInt(infos[2]));
			} catch (NumberFormatException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**根据用户的pageID和关注数量获取关注列表*/
	public void getFriends(String pageID, String userID, int count) throws InterruptedException{
		System.out.println(pageID+"   "+count);
		int pageCount = count/20;
		if(count%20!=0)
			pageCount++;
		pageCount = (pageCount<5)?pageCount:5;		//设定抓取上限
		//pageCount = 2;
		Thread[] threads = new Thread[pageCount];
		for(int i=1;i<=pageCount;i++){
			String url = "http://weibo.com/p/"+pageID+"/follow?page="+i;
			HttpGet get = new HttpGet(url);
			String dFile = baseDir+"\\pages\\"+userID+"\\"+i+".txt";	//存放未经提取的页面文件，每个用户一个文件夹
			String uFile = baseDir+"\\friends\\"+userID+"\\"+i+".txt";		//存放提取后的好友文件，每个用户多个文件
			FollowerThread thread = new FollowerThread(client,get,dFile, uFile);
			threads[i-1] = thread;
		}
		for(int i=0;i<pageCount;i++){
	            threads[i].run();
	        }

		for(int i=0;i<pageCount;i++){
	        	threads[i].join();
	        }
	}

}
