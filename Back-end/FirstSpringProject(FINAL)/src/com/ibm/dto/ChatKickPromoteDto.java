package com.ibm.dto;

public class ChatKickPromoteDto {

	private int userId;
	private int chatId;
	private int doerRoleId;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public int getDoerRoleId() {
		return doerRoleId;
	}

	public void setDoerRoleId(int doerRoleId) {
		this.doerRoleId = doerRoleId;
	}

}
