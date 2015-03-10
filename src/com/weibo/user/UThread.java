package com.weibo.user;

import java.io.BufferedReader;
import java.io.IOException;

import net.sf.json.util.JSONTokener;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.weibo.common.FileProcess;
import com.weibo.thread.PageThread;

/**
 * @author CHEN Kan
 * @date 2013年12月10日
 ***/

public class UThread extends PageThread {
	public UThread(HttpClient client, HttpGet get, String dFile) {
		super(client, get, dFile);
	}

	protected void analyse(BufferedReader br) {
		String line;

		try {
			line = br.readLine();
			if (line.startsWith("try")) {
				int index = line.lastIndexOf("catch");
				line = line.substring(5, index);
			}
			line = new JSONTokener(line).nextValue().toString();
			FileProcess.write(dFile, line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}
