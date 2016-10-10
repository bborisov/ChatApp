package com.ibm.other;

import java.sql.Timestamp;

public class Message {
	
	private int id;
	private int senderId;
	private int chatId;
	private String content;
	private Timestamp createTms;
	private Timestamp updateTms;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSenderId() {
		return senderId;
	}
	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}
	public int getChatId() {
		return chatId;
	}
	public void setChatId(int chatId) {
		this.chatId = chatId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Timestamp getCreateTms() {
		return createTms;
	}
	public void setCreateTms(Timestamp createTms) {
		this.createTms = createTms;
	}
	public Timestamp getUpdateTms() {
		return updateTms;
	}
	public void setUpdateTms(Timestamp updateTms) {
		this.updateTms = updateTms;
	}

}
