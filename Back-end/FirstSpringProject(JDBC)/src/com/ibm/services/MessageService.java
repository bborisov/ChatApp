package com.ibm.services;

import java.util.List;

import com.ibm.other.Message;
import com.ibm.other.MessageCreateDto;
import com.ibm.other.MessageEditDto;

public interface MessageService {
	
	public Message sendMessage(MessageCreateDto messageCreateDto);
	
	public Message editMessage(MessageEditDto messageEditDto);
	
	public List<Message> getAllMessagesFromChat(int chatId);

}
