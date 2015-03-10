package searchpage;

/***
 * 根据微博搜索获取所有的搜索结果
 * Jeffee
 * 2013-7-202013
 */


import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.FileProcess;
import com.weibo.login.Login;

/**
 * @author Jeffee
 * 
 */
public class SingleTSearch {

	public static void main(String[] args) {
		HttpClient client = Login.login();
		String url = "http://s.weibo.com/weibo/%25E5%258F%2598%25E5%25BE%2597%25E7%25B2%2589%25E5%25AB%25A9%25E5%25AB%25A9%25E3%2580%2581%25E6%25B0%25B4%25E5%2598%259F%25E5%2598%259F%25E7%259A%2584%25E5%2591%25A6&b=1&category=4&nodup=1&page=";
		int count = 50;
		SingleTSearch search = new SingleTSearch(client, url, count);
		//search.getMids();
		
		search.mergeMids();
	}

	private HttpClient client;
	private String baseUrl;
	private int pageCount;
	private String baseDir;

	public SingleTSearch(HttpClient client, String url, int count) {
		this.baseUrl = url;
		this.client = client;
		this.pageCount = count;
		baseDir = "E:\\Sina\\search\\fen";
		FileProcess.checkDir(baseDir);
		FileProcess.checkDir(baseDir + "\\mids"); 
	}

	/***获取所有微博的mid**/
	public void getMids() {
		//SearchPageT[] threads = new SearchPageT[pageCount];
		//pageCount = 1;
		for (int i = 41; i <= pageCount; i++) {
			String url = baseUrl + i;
			System.out.println(url);
			HttpGet get = new HttpGet(url);
			CopyOfSearchPageT search = new CopyOfSearchPageT(client, get, baseDir + "\\mids\\"
					+ i);
			search.run();
		}

	}

	public void mergeMids() {
		List<String> allList = new LinkedList<String>();
		for (int i = 1; i <= pageCount; i++) {
			File sFile = new File(baseDir + "\\mids\\"+i);
			List<String> list = FileProcess.read(sFile);
			allList.addAll(list);
		}
		FileProcess.write(baseDir+"\\allmids", allList);
	}

}
