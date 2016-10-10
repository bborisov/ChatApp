package com.ibm.dao;

import java.util.List;

import com.ibm.other.Message;

public interface MessageDao {
	
	public Message sendMessage(int senderId, int chatId, String content);
	
	public Message editMessage(int messageId, String content);
	
	public Message getMessageById(int messageId);

	public List<Message> getAllMessagesFromChat(int chatId);
	
	public boolean isMessageStored (int messageId);
	
}
