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

import com.ibm.other.Message;
import com.ibm.other.MessageCreateDto;
import com.ibm.other.MessageEditDto;
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
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/chats/" + message.getChatId() + "/messages/sendMessage", flag);
			messagingTemplate.convertAndSend("/topic/chats/" + message.getChatId() + "/messages/newMessageInChat", message);
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
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/chats/" + message.getChatId() + "/messages/" + message.getId() + "/editMessage", flag);
			messagingTemplate.convertAndSend("/topic/chats/" + message.getChatId() + "/messages/editedMessageInChat", message);
		}
	}
	
	@MessageMapping(value = "/getAllMessagesFromChat")
	public void getAllMessagesFromChat(int chatId, Principal principal) throws IOException {
		List<Message> messages = messageService.getAllMessagesFromChat(chatId);
		Map<Integer, Message> prettyMessages = messages.parallelStream().collect(Collectors.toMap(Message::getId, Function.identity()));
		messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/getAllMessagesFromChat", prettyMessages);
	}
	
//	@RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
//	public void sendMessage(int senderId, int chatId, String content) throws IOException {
//		messageService.sendMessage(senderId, chatId, content);
//	}
//	
//	@RequestMapping(value = "/editMessage", method = RequestMethod.PATCH)
//	public void editMessage(int messageId, String content) throws IOException {
//		messageService.editMessage(messageId, content);
//	}
//	
//	@RequestMapping(value = "/getAllMessagesFromChat", method = RequestMethod.GET)
//	public List<Message> getAllMessagesFromChat(@RequestParam(value = "chatId") int chatId) throws IOException {
//		return messageService.getAllMessagesFromChat(chatId);
//	}
	
}
