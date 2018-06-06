package com.ibm.dao;

import java.util.List;

import com.ibm.entities.MessageEntity;
import com.ibm.entities.wrappers.Message;

public interface MessageDao extends BasicDao<MessageEntity> {

	public Message sendMessage(int senderId, int chatId, String content);

	public Message editMessage(int messageId, String content);

	public List<Message> getAllMessagesByChat(int chatId);

}
