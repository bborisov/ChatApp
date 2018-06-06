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

import com.ibm.dao.ChatDao;
import com.ibm.dto.ChatCreateDto;
import com.ibm.dto.ChatJoinDto;
import com.ibm.dto.ChatKickPromoteDto;
import com.ibm.dto.MemberDto;
import com.ibm.entities.wrappers.Chat;
import com.ibm.services.ChatService;

@RestController
public class ChatController {

	@Autowired
	private ChatService chatService;

	@Autowired
	private ChatDao chatDao;

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@MessageMapping(value = "/createChat")
	public void createChat(ChatCreateDto chatCreateDto, Principal principal) throws IOException {
		boolean flag = false;
		Chat chat = null;
		try {
			chat = chatService.createChat(chatCreateDto);

			int userId = chatCreateDto.getCreatorId();
			int chatId = chat.getId();

			chatDao.joinChat(userId, chatId);
			chatDao.makeAdmin(userId, chatId);

			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/chats/createChat", flag);
			messagingTemplate.convertAndSend("/topic/chats/newChat", (chat == null) ? false : chat);
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
			messagingTemplate.convertAndSendToUser(principal.getName(),
					"/queue/chats/" + chatJoinDto.getChatId() + "/joinChat", flag);
			messagingTemplate.convertAndSend("/topic/chats/" + chatJoinDto.getChatId() + "/newUserInChat",
					(memberDto == null) ? false : memberDto);
		}
	}

	@MessageMapping(value = "/kickFromChat")
	public void kickFromChat(ChatKickPromoteDto chatKickPromoteDto, Principal principal) throws IOException {
		boolean flag = false;
		try {
			chatService.kickFromChat(chatKickPromoteDto);
			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(),
					"/queue/chats/" + chatKickPromoteDto.getChatId() + "/kickFromChat", flag);
			messagingTemplate.convertAndSend("/topic/chats/" + chatKickPromoteDto.getChatId() + "/userKicked",
					chatKickPromoteDto.getUserId());
		}
	}

	@MessageMapping(value = "/makeAdmin")
	public void makeAdmin(ChatKickPromoteDto chatKickPromoteDto, Principal principal) throws IOException {
		boolean flag = false;
		try {
			chatService.makeAdmin(chatKickPromoteDto);
			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(),
					"/queue/chats/" + chatKickPromoteDto.getChatId() + "/makeAdmin", flag);
			messagingTemplate.convertAndSend("/topic/chats/" + chatKickPromoteDto.getChatId() + "/newAdmin",
					chatKickPromoteDto.getUserId());
		}
	}

	@MessageMapping(value = "/getAllMembersFromChat")
	public void getAllMembersFromChats(int chatId, Principal principal) throws IOException {
		List<MemberDto> members = chatService.getAllMembersFromChat(chatId);
		Map<Integer, MemberDto> prettyMembers = members.parallelStream()
				.collect(Collectors.toMap(MemberDto::getId, Function.identity()));
		messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/getAllMembersFromChat", prettyMembers);
	}

	@MessageMapping(value = "/getAllPermittedChats")
	public void getAllPermittedChats(int userId, Principal principal) throws IOException {
		List<Chat> chats = chatService.getAllPermittedChats(userId);
		Map<Integer, Chat> prettyChats = chats.parallelStream()
				.collect(Collectors.toMap(Chat::getId, Function.identity()));
		messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/getAllPermittedChats", prettyChats);
	}

}