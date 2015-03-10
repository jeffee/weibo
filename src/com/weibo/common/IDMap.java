/**
 * 提供userID到pageID之间的转换
 */
package com.weibo.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author CHEN Kan
 * @date 2013年10月5日
 ***/

public class IDMap {


	public static void main(String[] args) {
		System.out.println(IDMap.idMap.get("2784194993"));
		IDMap.put("22", "4444");
		IDMap.put("33", "5555");

	}

	private static String idFile = "E:\\Sina\\spams\\id.sn";
	private static Map<String,String> idMap = new HashMap<String, String>();
	
	static {
		List<String> list = FileProcess.read(new File(idFile));
		for(String info:list){
			String[]strs = info.trim().split(",");
			idMap.put(strs[0], strs[1]);
		}
	}
	public static String getPageID(String uid){
		return idMap.get(uid);
	}
	public static void put(String uid, String pageID){
		   idMap.put(uid, pageID);
		try {
			FileWriter writer = new FileWriter(idFile, true);
			writer.write(uid + "," + pageID+"\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	     
	}

}
