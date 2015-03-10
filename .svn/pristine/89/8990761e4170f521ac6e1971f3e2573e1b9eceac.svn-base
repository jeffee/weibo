/**
 * 微博类
 */
package com.weibo.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author CHEN Kan
 * @date 2013年9月10日
 ***/

public class Post {
	private String mid;
	private String content;
	private String date;
	private int repostCount;
	private int commentCount;
	private int favorCount;
	private String uid;
	private String userName;
	private int isOri;
	private String rootMid;
	private String from;
	private String href;
	
	public Post(){
	}

	public void show(){
		System.out.println(mid+";"+uid+";"+userName+";"+isOri+";"+rootMid+";"+date+";"+href+";"+repostCount+";"+favorCount+";"+commentCount+";"+from+";"+content);
	}
	public String toString(){
		return mid+";"+uid+";"+userName+";"+isOri+";"+rootMid+";"+date+";"+href+";"+repostCount+";"+favorCount+";"+commentCount+";"+from+";"+content;
	}
	public String getHref() {
		return href;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setHref(String href) {
		this.href = href;
	}

	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content.replaceAll(";", ",");
	}

	public String getDateStr() {
		return date;
	}

	public Date getDate(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日 HH:mm");
		Date cDate;
		try {
			cDate = format.parse(date);
		} catch (ParseException e) {
			return null;
		}
		return cDate;
	}
	public void setDate(String pDate) {
		this.date = pDate;
	}

	public int getRepostCount() {
		return repostCount;
	}

	public void setRepostCount(int repostCount) {
		this.repostCount = repostCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getFavorCount() {
		return favorCount;
	}

	public void setFavorCount(int favorCount) {
		this.favorCount = favorCount;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}


	public String getMid() {
		return mid;
	}

	public String getRootMid() {
		return rootMid;
	}

	public void setRootMid(String rootMid) {
		this.rootMid = rootMid;
	}

	public int isOri() {
		return isOri;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public void setOri(int isOri) {
		this.isOri = isOri;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	
}
