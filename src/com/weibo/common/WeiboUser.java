package com.weibo.common;

import java.util.LinkedList;
import java.util.List;

public class WeiboUser {

	private String uid;
	private String userName;
	private String userPass;
	private String nickName;
	private long fanCount;
	private long followCount;
	private long postCount;
	
	public static List<WeiboUser> userList = new LinkedList<WeiboUser>(); 
	
	public WeiboUser(){
	}
	
	public String getUid() {
		return uid;
	}

	public WeiboUser(String uid, String fowCount, String fanCount, String pCount){
		this.uid = uid.trim();
		this.followCount = getNum(fowCount);
		this.fanCount = getNum(fanCount);
		this.postCount = getNum(pCount);
	}
	
	private long getNum(String nStr){
		int index = nStr.indexOf("Íò");
		if(index!=-1){
			nStr = nStr.substring(0, index).trim();
			return 10000*Long.parseLong(nStr);
		}
		return Long.parseLong(nStr);
	}
	public String getDisplayName() {
		return nickName;
	}
	public void setDisplayName(String displayName) {
		this.nickName = displayName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPass() {
		return userPass;
	}
	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public long getFanCount() {
		return fanCount;
	}
	public void setFanCount(long fanCount) {
		this.fanCount = fanCount;
	}
	public long getFollowCount() {
		return followCount;
	}
	public void setFollowCount(long followCount) {
		this.followCount = followCount;
	}
	public long getPostCount() {
		return postCount;
	}
	public void setPostCount(long postCount) {
		this.postCount = postCount;
	}
	
	
	
}
