package com.ibm.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.dao.MessageDao;
import com.ibm.other.Message;
import com.ibm.other.MessageCreateDto;
import com.ibm.other.MessageEditDto;
import com.ibm.services.MessageService;

@Service
public class MessageServiceImpl implements MessageService {
	
	@Autowired
	private MessageDao messageDao;

	@Override
	public Message sendMessage(MessageCreateDto messageCreateDto) {
		String content = messageCreateDto.getContent();
		
		if (content == null || content.trim().equals("") || content.length() > 255) {
			throw new RuntimeException("Invalid content!");
		}
		
		return messageDao.sendMessage(messageCreateDto.getSenderId(), messageCreateDto.getChatId(), content);
	}
	
	@Override
	public Message editMessage(MessageEditDto messageEditDto) {
		int messageId = messageEditDto.getMessageId();
		
		if (!messageDao.isMessageStored(messageId)) {
			throw new RuntimeException("There is no such message!");
		}
		
		String content = messageEditDto.getContent();
		
		if (content == null || content.trim().equals("") || content.length() > 255) {
			throw new RuntimeException("Invalid content!");
		}
		
		return messageDao.editMessage(messageId, content);
	}

	@Override
	public List<Message> getAllMessagesFromChat(int chatId) {
		return messageDao.getAllMessagesFromChat(chatId);
	}
	
}
