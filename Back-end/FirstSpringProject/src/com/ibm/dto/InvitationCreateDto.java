package com.ibm.dto;

public class InvitationCreateDto {

	private String email;
	private int chatId;
	private int invitorId;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public int getInvitorId() {
		return invitorId;
	}

	public void setInvitorId(int invitorId) {
		this.invitorId = invitorId;
	}

}
