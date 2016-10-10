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

import com.ibm.other.Chat;
import com.ibm.other.ChatDto;
import com.ibm.other.ChatJoinDto;
import com.ibm.other.ChatKickPromoteDto;
import com.ibm.other.MemberDto;
import com.ibm.services.ChatService;

@RestController
public class ChatController {

	@Autowired
	private ChatService chatService;
	
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;
	
	@MessageMapping(value = "/createChat")
	public void createChat(ChatDto chatDto, Principal principal) throws IOException {
		boolean flag = false;
		Chat chat = null;
		try {
			chat = chatService.createChat(chatDto);
			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/chats/createChat", flag);
			messagingTemplate.convertAndSend("/topic/chats/newChat", chat);
		}
	}
	
	@MessageMapping(value = "/joinChat")
	public void joinChat(ChatJoinDto chatJoinDto, Principal principal) throws IOException {
		boolean flag = false;
		MemberDto memberDto = null;
		try {
			memberDto = chatService.joinChat(chatJoinDto);
			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/chats/" + chatJoinDto.getChatId() + "/joinChat", flag);
			messagingTemplate.convertAndSend("/topic/chats/" + chatJoinDto.getChatId() + "/newUserInChat", memberDto);
		}
	}
	
	@MessageMapping(value = "/kickFromChat")
	public void kickFromChat(ChatKickPromoteDto chatKickPromoteDto, Principal principal) throws IOException {
		boolean flag = false;
		try {
			chatService.kickFromChat(chatKickPromoteDto);
			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/chats/" + chatKickPromoteDto.getChatId() + "/kickFromChat", flag);
			messagingTemplate.convertAndSend("/topic/chats/" + chatKickPromoteDto.getChatId() + "/userKicked", chatKickPromoteDto.getUserId());
		}
	}
	
	@MessageMapping(value = "/makeAdmin")
	public void makeAdmin(ChatKickPromoteDto chatKickPromoteDto, Principal principal) throws IOException {
		boolean flag = false;
		try {
			chatService.makeAdmin(chatKickPromoteDto);
			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/chats/" + chatKickPromoteDto.getChatId() + "/makeAdmin", flag);
			messagingTemplate.convertAndSend("/topic/chats/" + chatKickPromoteDto.getChatId() + "/newAdmin", chatKickPromoteDto.getUserId());
		}
	}
	
	@MessageMapping(value = "/getAllMembersFromChat")
	public void getAllMembersFromChats(int chatId, Principal principal) throws IOException {
		List<MemberDto> members = chatService.getAllMembersFromChats(chatId);
		Map<Integer, MemberDto> prettyMembers = members.parallelStream().collect(Collectors.toMap(MemberDto::getId, Function.identity()));
		messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/getAllMembersFromChat", prettyMembers);
	}
	
	@MessageMapping(value = "/getAllPermittedChats")
	public void getAllPermittedChats(int userId, Principal principal) throws IOException {
		List<Chat> chats = chatService.getAllPermittedChats(userId);
		Map<Integer, Chat> prettyChats = chats.parallelStream().collect(Collectors.toMap(Chat::getId, Function.identity()));
		messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/getAllPermittedChats", prettyChats);
	}
	
	
	
//	@RequestMapping(value = "/createChat", method = RequestMethod.POST)
//	public void createChat(String chatName, String summary, int typeId, int creatorId) throws IOException {
//		chatService.createChat(chatName, summary, typeId, creatorId);
//	}
//
//	@RequestMapping(value = "/joinChat", method = RequestMethod.POST)
//	public void joinChat(int userId, int chatId) throws IOException {
//		chatService.joinChat(userId, chatId);
//	}
//
//	@RequestMapping(value = "/inviteToChat", method = RequestMethod.POST)
//	public void inviteToChat(int userId, int chatId, int invitorId) throws IOException {
//		chatService.inviteToChat(userId, chatId, invitorId);
//	}
//
//	@RequestMapping(value = "/kickFromChat", method = RequestMethod.DELETE)
//	public void kickFromChat(int userId, int chatId, int kickerRoleId) throws IOException {
//		chatService.kickFromChat(userId, chatId, kickerRoleId);
//	}
//	
//	@RequestMapping(value = "/makeAdmin", method = RequestMethod.PATCH)
//	public void makeAdmin(int userId, int chatId, int promoterRoleId) throws IOException {
//		chatService.makeAdmin(userId, chatId, promoterRoleId);
//	}
//	
//	@RequestMapping(value = "/getAllPermittedChats", method = RequestMethod.GET)
//	public List<Chat> getAllPermittedChats(@RequestParam(value = "userId") int userId) throws IOException {
//		return chatService.getAllPermittedChats(userId);
//	}
	
	// controller - input / output
	// service - validations (input) / business validations / business logic
	// dao - persistence layer / CRUD -> DB + processing persistence data
	
}