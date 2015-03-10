/**
 * 通过多线程的方式抓取用户转发数据
 */
package com.weibo.repost;

import java.io.BufferedReader;
import java.io.File;
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

import com.weibo.common.FileProcess;
import com.weibo.login.Login;

/**
 * @author Jeffee
 * 
 */
public class RepostPageCrawl {
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		HttpClient client = Login.login("social_ck@sina.com", "jeffee");
		
		String dir = "E:\\Sina\\test";
		RepostPageCrawl re = new RepostPageCrawl(client, dir);
		String url = "http://weibo.com/3639517235/A3GBJmVm7";
				
		re.getAll(url);
		long endTime = System.currentTimeMillis();
		System.out.println("all:" +(endTime-startTime));
	}

	
	private String baseDir;
	private HttpClient httpClient;
	
	public RepostPageCrawl(HttpClient client, String baseDir){
		httpClient = client;
		this.baseDir = baseDir;
		File dir = new File(baseDir);
		if(!dir.exists()||!dir.isDirectory()){
			dir.mkdirs();
		}
	}
	
	
	
	public void getAll(String oUrl){
		String url = oUrl.replaceAll("\\\\", "/")+"?type=repost";
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), "utf8"));
			String line = br.readLine();
			
			while(line!=null){
				if(line.startsWith("<script>FM.view({\"ns\":\"pl.content.weiboDetail") || line.startsWith("<script>STK && STK.pageletM && STK.pageletM.view({\"pid\":\"pl_content_weiboDetail")){
					get.abort();

					line = line.substring(line.indexOf("{"), line.lastIndexOf("}")+1);
					line = new JSONTokener(line).nextValue().toString();
					int pageNum=1;
					FileProcess.write(baseDir+"\\"+pageNum, line);
					int index = line.lastIndexOf("allowForward");
					if(index==-1)
						break;
					String str = line.substring(index);

					Pattern pattern = Pattern
							.compile("action-data=\\\\\"(id=\\d*?&max_id=.*?&page=)(\\d*)\\\\");

					Matcher matcher = pattern.matcher(str);
					String info = "";
					int pageCount = 0;
					while (matcher.find()){
						int count = Integer.parseInt(matcher.group(2));
						if(count>pageCount)
							pageCount = count;
						else
							info = matcher.group(1);
					}
					System.out.println(pageCount);
					while(pageNum<pageCount){
						pageNum +=1;
						url = "http://e.weibo.com/aj/mblog/info/big?"+info+pageNum;
						getJasonPage(url,pageNum);
					}
					break;
				}
				line = br.readLine();	
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void getJasonPage(String url, int pageNum){
		if(pageNum%20==0)
			System.out.println("GET 20 PAges ^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), "utf8"));		
			String line = br.readLine();
			get.abort();
			br.close();
			
			line = new JSONTokener(line).nextValue().toString();
		//	getUsers(line); 
			FileProcess.write(baseDir+"\\"+pageNum, line);
		} catch (IOException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/*public void flushResult()
 {
		FileWriter fWriter;
		try {
			String fileName = baseDir + "\\userlist.txt";
		
			fWriter = new FileWriter(new File(fileName));
			for (int i = 0; i < WeiboUser.userList.size(); i++) {
				WeiboUser user = WeiboUser.userList.get(i);
				fWriter.write(user.getUid() + ";" + user.getFollowCount() + ";"
						+ user.getFanCount() + ";" + user.getPostCount());
				fWriter.write("\r\n");
			}
			fWriter.close();
			WeiboUser.userList.clear();
			System.out.println(fileName + " finished");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/
}
