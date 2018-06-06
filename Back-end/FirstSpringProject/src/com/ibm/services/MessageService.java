package com.ibm.services;

import java.util.List;

import com.ibm.dto.MessageCreateDto;
import com.ibm.dto.MessageEditDto;
import com.ibm.entities.wrappers.Message;

public interface MessageService {

	public Message sendMessage(MessageCreateDto messageCreateDto);

	public Message editMessage(MessageEditDto messageEditDto);

	public List<Message> getAllMessagesFromChat(int chatId);

	public boolean isMessageStored(int messageId);

}
