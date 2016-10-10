package com.ibm.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.dao.ChatDao;
import com.ibm.dao.UserDao;
import com.ibm.other.Chat;
import com.ibm.other.ChatDto;
import com.ibm.other.ChatJoinDto;
import com.ibm.other.ChatKickPromoteDto;
import com.ibm.other.MemberDto;
import com.ibm.other.MemberRole;
import com.ibm.other.MemberStatus;
import com.ibm.other.User;
import com.ibm.services.ChatService;

@Service
public class ChatServiceImpl implements ChatService {
	
	@Autowired
	private ChatDao chatDao;
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public Chat createChat(ChatDto chatDto) {
		String chatName = chatDto.getName();
		String summary = chatDto.getSummary();
		
		if (chatName == null || chatName.trim().equals("") || chatName.length() > 50 ||
				summary == null || summary.trim().equals("") || summary.length() > 255) {
			throw new RuntimeException("Incorrect data - chatName or summary!");
		}
		
		return chatDao.createChat(chatName, summary, chatDto.getTypeId(), chatDto.getCreatorId());
	}
	
	@Override
	public MemberDto joinChat(ChatJoinDto chatJoinDto) {
	 	int userId = chatJoinDto.getUserId();
	 	int chatId = chatJoinDto.getChatId();
		
		if (chatDao.isMember(userId, chatId)) {
			throw new RuntimeException("Cannot join this chat - you are already a member!");
		}
		
		if (chatDao.isInvited(userId, chatId)) {
			chatDao.updateMemberStatus(userId, chatId, MemberStatus.SELF_JOINED);
		} else {
			chatDao.joinChat(userId, chatId);
		}
		
		User user = userDao.getUserById(userId);
		MemberDto memberDto = new MemberDto();
		
		memberDto.setId(user.getId());
		memberDto.setName(user.getName());
		memberDto.setEmail(user.getEmail());
		memberDto.setStatusId(user.getStatusId());
		memberDto.setRoleId(MemberRole.USER);
		
		return memberDto;
	}
	
	@Override
	public void kickFromChat(ChatKickPromoteDto chatKickPromoteDto) {
		
		if (!chatDao.isAdmin(chatKickPromoteDto.getDoerRoleId())) {
			throw new RuntimeException("You are not an Admin!");
		}
		
		int userId = chatKickPromoteDto.getUserId();
		int chatId = chatKickPromoteDto.getChatId();
		
		if (!chatDao.isMember(userId, chatId)) {
			throw new RuntimeException("There is no member with such an id!");
		}
		
		chatDao.kickFromChat(userId, chatId);
	}
	
	@Override
	public void makeAdmin(ChatKickPromoteDto chatKickPromoteDto) {
		
		if (!chatDao.isAdmin(chatKickPromoteDto.getDoerRoleId())) {
			throw new RuntimeException("You are not an Admin!");
		}
		
		int userId = chatKickPromoteDto.getUserId();
		int chatId = chatKickPromoteDto.getChatId();
		
		if (!chatDao.isMember(userId, chatId)) {
			throw new RuntimeException("There is no member with such an id!");
		}
		
		chatDao.makeAdmin(userId, chatId);
	}
	
	@Override
	public List<MemberDto> getAllMembersFromChats(int chatId) {
		return chatDao.getAllMembersFromChats(chatId);
	}
	
	@Override
	public List<Chat> getAllPermittedChats(int userId) {
		return chatDao.getAllPermittedChats(userId);
	}

}
