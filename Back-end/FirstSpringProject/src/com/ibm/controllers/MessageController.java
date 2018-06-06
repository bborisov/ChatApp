package com.ibm.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.dto.MessageCreateDto;
import com.ibm.dto.MessageEditDto;
import com.ibm.entities.wrappers.Message;
import com.ibm.services.MessageService;

@RestController
public class MessageController {

	@Autowired
	private MessageService messageService;

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@MessageMapping(value = "/sendMessage")
	public void sendMessage(MessageCreateDto messageCreateDto, Principal principal) throws IOException {
		boolean flag = false;
		Message message = null;
		try {
			message = messageService.sendMessage(messageCreateDto);
			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(),
					"/queue/chats/" + messageCreateDto.getChatId() + "/messages/sendMessage", flag);
			messagingTemplate.convertAndSend(
					"/topic/chats/" + messageCreateDto.getChatId() + "/messages/newMessageInChat",
					(message == null) ? false : message);
		}
	}

	@MessageMapping(value = "/editMessage")
	public void editMessage(MessageEditDto messageEditDto, Principal principal) throws IOException {
		boolean flag = false;
		Message message = null;
		try {
			message = messageService.editMessage(messageEditDto);
			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/chats/" + messageEditDto.getChatId()
					+ "/messages/" + messageEditDto.getMessageId() + "/editMessage", flag);
			messagingTemplate.convertAndSend("/topic/chats/" + messageEditDto.getChatId() + "/messages/editedMessageInChat",
					(message == null) ? false : message);
		}
	}

	@MessageMapping(value = "/getAllMessagesFromChat")
	public void getAllMessagesFromChat(int chatId, Principal principal) throws IOException {
		List<Message> messages = messageService.getAllMessagesFromChat(chatId);
		Map<Integer, Message> prettyMessages = messages.parallelStream()
				.collect(Collectors.toMap(Message::getId, Function.identity()));
		messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/getAllMessagesFromChat", prettyMessages);
	}

}
