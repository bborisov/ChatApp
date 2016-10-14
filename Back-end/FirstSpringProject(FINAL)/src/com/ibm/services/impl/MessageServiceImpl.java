package com.ibm.services.impl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.dao.MessageDao;
import com.ibm.dto.MessageCreateDto;
import com.ibm.dto.MessageEditDto;
import com.ibm.entities.wrappers.Message;
import com.ibm.services.MessageService;
import com.ibm.validators.StringValidator;

@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	private MessageDao messageDao;

	@Override
	public Message sendMessage(MessageCreateDto messageCreateDto) {
		String content = messageCreateDto.getContent();
		StringValidator contentValidator = new StringValidator(content, 255);

		if (!contentValidator.isValid()) {
			throw new IllegalArgumentException("Invalid message content!");
		}

		return messageDao.sendMessage(messageCreateDto.getSenderId(), messageCreateDto.getChatId(), content);
	}

	@Override
	public Message editMessage(MessageEditDto messageEditDto) {
		int messageId = messageEditDto.getMessageId();

		if (!isMessageStored(messageId)) {
			throw new NoSuchElementException("There is no such message!");
		}

		String content = messageEditDto.getContent();
		StringValidator contentValidator = new StringValidator(content, 255);

		if (!contentValidator.isValid()) {
			throw new IllegalArgumentException("Invalid message content!");
		}

		return messageDao.editMessage(messageId, content);
	}

	@Override
	public List<Message> getAllMessagesFromChat(int chatId) {
		return messageDao.getAllMessagesByChat(chatId);
	}

	@Override
	public boolean isMessageStored(int messageId) {
		if (messageDao.getEntityById(messageId) == null) {
			return false;
		} else {
			return true;
		}
	}

}
