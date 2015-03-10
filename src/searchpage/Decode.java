/**
 * 对抓取到的网页文件进行解码，获取需要的数据
 */
package searchpage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jeffee
 * 
 */
public class Decode {
	private List<Integer> repostList;
	private List<Integer> favoList;
	private List<Integer> commList;

	public static void main(String[] args) {
		Decode decode = new Decode();
		File dFile = new File("E:\\Sina\\search\\info.txt");
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dFile)));
			for (int i = 1; i <= 21; i++) {
				File sFile = new File("E:\\Sina\\search\\pages\\"+i);
				decode.decode(sFile, writer);
				System.out.println("Finishe "+ i);
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

	public Decode() {
		repostList = new LinkedList<Integer>();
		favoList = new LinkedList<Integer>();
		commList = new LinkedList<Integer>();
	}

	public void decode(File file,BufferedWriter writer) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			String line = reader.readLine();
			while (line != null) {
				if (line.startsWith("<script>STK && STK.pageletM && STK.pageletM.view({\"pid\":\"pl_weibo_feedlist\"")) {
					Pattern pattern = Pattern
							.compile("allowForward=1&url=(.*?)&mid=(\\d*?)&.*?&uid=(\\d*?)&.*?\\\\u8f6c\\\\u53d1\\(?(.*?)\\)?<.*?\\\\u6536\\\\u85cf\\(?(.*?)\\)?<.*?\\\\u8bc4\\\\u8bba\\(?(.*?)\\)?<");
					Matcher matcher = pattern.matcher(line);
					while (matcher.find()) {
						// System.out.println(matcher.group());
						String url = matcher.group(1).trim()
								.replaceAll("/", "");
						String mid = matcher.group(2).trim();
						String uid = matcher.group(3);

						int repostCount = getNum(matcher.group(4));
						int favoCount = getNum(matcher.group(5));
						int commentCount = getNum(matcher.group(6));
						String info = (mid + " " + uid + "  " + repostCount
								+ " " + favoCount + " " + commentCount + " "
								+ url);
						//System.out.println(info);
						writer.write(info);
						writer.newLine();
					}
					break;
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 获取评论数，转发数等数字指标 **/
	public void getNums(String line) {
		Pattern pattern = Pattern
				.compile("allowForward=1&url=(.*?)&mid=(\\d*?)&.*?&uid=(\\d*?)&.*?\\\\u8f6c\\\\u53d1\\(?(.*?)\\)?<.*?\\\\u6536\\\\u85cf\\(?(.*?)\\)?<.*?\\\\u8bc4\\\\u8bba\\(?(.*?)\\)?<");
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			System.out.println(matcher.group());
			String url = matcher.group(1).trim().replaceAll("/", "");
			String mid = matcher.group(2).trim();
			String uid = matcher.group(3);

			int repostCount = getNum(matcher.group(4));
			int favoCount = getNum(matcher.group(5));
			int commentCount = getNum(matcher.group(6));
			System.out.println(url + " " + uid + "  " + mid + " " + repostCount
					+ " " + favoCount + " " + commentCount);

		}

	}

	private int getNum(String str) {
		int num = 0;
		if (str.trim().equals(""))
			str = "0";
		num = Integer.parseInt(str);
		return num;
	}
}
