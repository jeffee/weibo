/**
 * 将转发微博提取为Post类存储
 */
package com.weibo.post;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import com.weibo.common.FileProcess;
import com.weibo.common.Post;
import com.weibo.parse.RepostParse;

/**
 * @author CHEN Kan
 * @date 2013年9月23日
 ***/

public class Repost2Post {

	public static void main(String[] args) {
		Repost2Post re = new Repost2Post();
		File baseDir = new File("E:\\Sina\\spams\\pages");
		File[] dirs = baseDir.listFiles();
		List<Post> list = new LinkedList<Post>();
		for(File dir:dirs)
			list.addAll(re.parse(dir));
		
		File dFile = new File("E:\\Sina\\spams\\allPost.sn");
		re.flush(list, dFile);
	}

	public List<Post> parse(File dir){
		List<Post> postList = new LinkedList<Post>();
		if(!dir.exists()||dir.isFile())
			return postList;
		File[] files = dir.listFiles();
		for(int i=1;i<=files.length;i++){
			String info = FileProcess.readLine(new File(dir.getAbsolutePath()+"\\"+i));
			postList.addAll(RepostParse.parse(info));
		}
		return postList;
	}
	
	public void flush(List<Post> list, File dFile){
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dFile)));
			for(Post post:list){
				bw.write(post.toString());
				bw.newLine();
			}
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
