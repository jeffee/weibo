/**
 * 
 */
package searchpage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.HttpClient;

import com.weibo.common.FileProcess;
import com.weibo.login.Login;
import com.weibo.repost.RepostPageCrawl;

/**
 * @author Jeffee
 * 
 */
public class Download {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Download down = new Download();
		//down.getUrls();
	
		HttpClient client = Login.login("jeffee@sina.cn", "ruoshui3");
		File file = new File("E:\\Sina\\search\\fen\\highmids.txt");
		down.downSearch(client, file);
		//down.down(client);

	}

	private List<String> urlList;

	public Download() {
		urlList = new LinkedList<String>();
	}

	public void getUrls() {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://127.0.0.1:3306/mydb";
		String user = "root";
		String password = "ruoshui3";
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, password);
			if (!conn.isClosed())
				System.out.println("Succeeded connecting to the Database!");
			Statement stmt = conn.createStatement();
			String sql = "SELECT href FROM mydb.post where rCount>100";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String href = rs.getString(1).trim();
				urlList.add(href);
			}
			System.out.println("get " + urlList.size());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void download(HttpClient client) {
		for (int i = 0; i < urlList.size(); i++) {
			String url = urlList.get(i);

			String baseDir = "E:\\Sina\\search\\spams\\"
					+ url.substring(url.length() - 6);
			if (FileProcess.checkDir(baseDir) == false) {
				RepostPageCrawl re = new RepostPageCrawl(client, baseDir);
				re.getAll(url);
			}
		}
	}
	public void down(HttpClient client) {
		File sFile = new File("E:\\Sina\\spams\\spams.txt");
		List<String> list = FileProcess.read(sFile);
		
		for (int i = 0; i < list.size(); i++) {
			String url = list.get(i);
			String dir = "E:\\Sina\\search\\spams\\"
					+ url.substring(url.length() - 6);
			if (FileProcess.checkDir(dir) == false) {
				RepostPageCrawl re = new RepostPageCrawl(client, dir);
				re.getAll(url);
			}
			System.out.println(url);
		
			
		}
	}
	
	public void downSearch(HttpClient client, File file){
		List<String> list = FileProcess.read(file);
		for(int i=0;i<list.size();i++){
			String str = list.get(i);
			String[] infos = str.split(" ");
			if(Integer.parseInt(infos[2])>100){
				String url = infos[5];
				String dir = "E:\\Sina\\search\\spams\\"
						+ url.substring(url.length() - 6);
				if (FileProcess.checkDir(dir) == false) {
					RepostPageCrawl re = new RepostPageCrawl(client, dir);
					re.getAll(url);
				}
			}
		}
	}
}
