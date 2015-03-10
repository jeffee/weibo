/**
 * 抓取用户关注列表的线程
 */
package com.weibo.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.FileProcess;

/**
 * @author Jeffee
 * 
 */
public class FollowerThread extends PageThread {
	private String uFile;
	public FollowerThread(HttpClient client, HttpGet get, String dFile, String uFile) {
		super(client, get, dFile);
		this.uFile = uFile;
	}

	protected void analyse(BufferedReader br) {
		// line = new JSONTokener(line).nextValue().toString();
		String line;
		try {
			line = br.readLine();
			while (line != null) {
				if (line.startsWith("<script>FM.view({\"ns\":\"pl.content.followTab.index")) {
					
					Pattern pattern = Pattern
							.compile("class=\\\\\"connect.*?href=\\\\\"\\\\/(\\d*?)\\\\.*?(\\d*?)<");
					Matcher matcher = pattern.matcher(line);
					File file = new File(uFile);
					FileProcess.checkDir(file.getParent());
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
					
					while (matcher.find()) {
						String uid = matcher.group(1);
						String count = matcher.group(2);
						writer.write(uid+" "+count);
						writer.newLine();
					}
					
					writer.close();
					FileProcess.write(dFile, line);
					break;
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
