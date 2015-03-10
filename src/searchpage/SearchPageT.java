/**
 * 获取搜索页面的所有微博的mid，并存储在dFile文件中
 * Jeffee2013-7-202013
 */
package searchpage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.Common;
import com.weibo.thread.PageThread;

/**
 * @author Jeffee
 */
public class SearchPageT extends PageThread {
	public SearchPageT(HttpClient client, HttpGet get, String dFile){
		super(client, get, dFile);
	}
	
	protected void analyse(BufferedReader br) {
		try {
			String line = br.readLine();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dFile)));

			while(line!=null){
			//	System.out.println(line);
				if(line.startsWith("<script>STK && STK.pageletM && STK.pageletM.view({\"pid\":\"pl_weibo_feedlist\"")){
					Pattern pattern = Pattern.compile("allowForward=1&url=(.*?)&mid=(\\d*?)&.*?&uid=(\\d*?)&.*?\\\\u8f6c\\\\u53d1\\(?(.*?)\\)?<.*?\\\\u6536\\\\u85cf\\(?(.*?)\\)?<.*?\\\\u8bc4\\\\u8bba\\(?(.*?)\\)?<");
					Matcher matcher = pattern.matcher(line);
					while(matcher.find()){
						String url = matcher.group(1).trim().replaceAll("/", "");
						String mid = matcher.group(2).trim();
						String uid = matcher.group(3);
					
						int repostCount = Common.getNum(matcher.group(4));
						int favoCount = Common.getNum(matcher.group(5));
						int commentCount = Common.getNum(matcher.group(6));
						String info = mid+" " +uid+"  "+repostCount+" "+favoCount+" "+commentCount+" "+url;
						bw.write(info);
						bw.newLine();
					}
					break;
				} else line = br.readLine();
			}
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
