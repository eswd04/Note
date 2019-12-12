package com.mynotead.md;

public class Note{
	private String content,title,createTime,imgPath;
	private int id,time;
	private long endTime,lastEdtTime;
	private boolean isChecked,isRemind;

	
	public void setLastEdtTime(long lastEdtTime){
		this.lastEdtTime=lastEdtTime;
	}

	public long getLastEdtTime(){
		return lastEdtTime;
	}
	public void setCreateTime(String createTime){
		this.createTime=createTime;
	}
	
	public String getCreateTime(){
		return createTime;
	}

	public void setRemind(boolean isRemind) {
		this.isRemind = isRemind;
	}

	public boolean isRemind() {
		return isRemind;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public boolean isChecked() {
		return isChecked;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setImgPath(String imgPath){
		this.imgPath=imgPath;
	}
	public String getImgPath(){
		return imgPath;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getTime() {
		return time;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}

	
