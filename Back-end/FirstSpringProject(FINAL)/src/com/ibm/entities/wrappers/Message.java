package com.ibm.entities.wrappers;

import java.sql.Timestamp;

public abstract class Message {

	public abstract int getId();

	public abstract void setId(int id);

	public abstract int getSenderId();

	public abstract void setSenderId(int senderId);

	public abstract int getChatId();

	public abstract void setChatId(int chatId);

	public abstract String getContent();

	public abstract void setContent(String content);

	public abstract Timestamp getCreateTms();

	public abstract void setCreateTms(Timestamp createTms);

	public abstract Timestamp getUpdateTms();

	public abstract void setUpdateTms(Timestamp updateTms);

}
