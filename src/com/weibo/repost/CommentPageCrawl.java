/**
 *通过多线程的方式抓取用户评论数据
 */
package com.weibo.repost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.util.JSONTokener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;

import com.weibo.common.FileProcess;
import com.weibo.common.WeiboUser;
import com.weibo.login.Login;


/**
 * @author Jeffee
 * @date 2013-12-2
 */
public class CommentPageCrawl {

	public static void main(String[] args) {
		HttpClient client = Login.login();
		String url = "http://weibo.com/3275897871/zuyfHDs4Q";
		String dir = "E:\\Sina\\test";
		CommentPageCrawl re = new CommentPageCrawl(client,dir);
				
		re.getAll(url);
	}
	
	private String baseDir;
	private HttpClient httpClient;
	
	public CommentPageCrawl(HttpClient client, String baseDir){
		httpClient = client;
		this.baseDir = baseDir;
		File dir = new File(baseDir);
		if(!dir.exists()||!dir.isDirectory()){
			dir.mkdirs();
		}
	}
	
	public void getAll(String url){
		HttpGet get = new HttpGet(url+"?type=comment");

		try {
			HttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), "utf8"));
			String line = br.readLine();
			
			while(line!=null){
				
				if(line.startsWith("<script>FM.view({\"ns\":\"pl.content.weiboDetail.index")){
					
					get.abort();
					int index = line.indexOf("\"html\":");
					line = line.substring(index+8, line.length()-14);
					
					index = line.lastIndexOf("commentFilter");	/**如果评论都是广告用户已经被清理掉，则index为-1**/
					if(index==-1)
						return;
					
					int pageNum=1;
					FileProcess.write(baseDir+"\\"+pageNum, line);
					
					String str = line.substring(index);
					Pattern pattern = Pattern
							.compile("action-data=\\\\\"(id=\\d*?&max_id=.*?&page=)(\\d*)");
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
						String newUrl = "http://weibo.com/aj/comment/big?_wv=5&"+info+pageNum;
						getJasonPage(newUrl,pageNum);
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
		System.out.println("GET PAge^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), "utf8"));		
			String line = br.readLine();
			get.abort();
			br.close();
			
			line = new JSONTokener(line).nextValue().toString();
 
			FileProcess.write(baseDir+"\\"+pageNum, line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
