package com.ibm.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.constants.MemberRole;
import com.ibm.constants.MemberStatus;
import com.ibm.dao.ChatDao;
import com.ibm.dao.UserDao;
import com.ibm.dto.ChatCreateDto;
import com.ibm.dto.ChatJoinDto;
import com.ibm.dto.ChatKickPromoteDto;
import com.ibm.dto.MemberDto;
import com.ibm.entities.wrappers.Chat;
import com.ibm.entities.wrappers.User;
import com.ibm.services.ChatService;
import com.ibm.validators.StringValidator;

@Service
public class ChatServiceImpl implements ChatService {

	@Autowired
	private ChatDao chatDao;

	@Autowired
	private UserDao userDao;

	@Override
	public Chat createChat(ChatCreateDto chatCreateDto) {
		String chatName = chatCreateDto.getName();
		StringValidator chatNameValidator = new StringValidator(chatName, 50);

		String summary = chatCreateDto.getSummary();
		StringValidator summaryValidator = new StringValidator(summary, 255);

		if (!chatNameValidator.isValid() || !summaryValidator.isValid()) {
			throw new IllegalArgumentException("Incorrect data - chatName or summary!");
		}

		return chatDao.createChat(chatName, summary, chatCreateDto.getTypeId(), chatCreateDto.getCreatorId());
	}

	@Override
	public MemberDto joinChat(ChatJoinDto chatJoinDto) {
		int userId = chatJoinDto.getUserId();
		int chatId = chatJoinDto.getChatId();

		if (isMember(userId, chatId)) {
			throw new IllegalArgumentException("Cannot join this chat - you are already a member!");
		}

		if (isInvited(userId, chatId) || isDeclined(userId, chatId)) {
			chatDao.updateMemberStatus(userId, chatId, MemberStatus.SELF_JOINED);
		} else {
			chatDao.joinChat(userId, chatId);
		}

		User user = userDao.getEntityById(userId);
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
		if (!isKickPromoteValid(chatKickPromoteDto)) {
			throw new RuntimeException("You are not an admin or there is no member with such an id!");
		}

		chatDao.kickFromChat(chatKickPromoteDto.getUserId(), chatKickPromoteDto.getChatId());
	}

	@Override
	public void makeAdmin(ChatKickPromoteDto chatKickPromoteDto) {
		if (!isKickPromoteValid(chatKickPromoteDto)) {
			throw new RuntimeException("You are not an admin or there is no member with such an id!");
		}

		chatDao.makeAdmin(chatKickPromoteDto.getUserId(), chatKickPromoteDto.getChatId());
	}

	@Override
	public List<MemberDto> getAllMembersFromChat(int chatId) {
		return chatDao.getAllMembersFromChat(chatId);
	}

	@Override
	public List<Chat> getAllPermittedChats(int userId) {
		return chatDao.getAllPermittedChats(userId);
	}

	@Override
	public boolean isKickPromoteValid(ChatKickPromoteDto chatKickPromoteDto) {
		if (!isAdmin(chatKickPromoteDto.getDoerRoleId())
				|| !isMember(chatKickPromoteDto.getUserId(), chatKickPromoteDto.getChatId())) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isMember(int userId, int chatId) {
		int statusId = chatDao.getStatusId(userId, chatId);
		if (statusId == MemberStatus.SELF_JOINED || statusId == MemberStatus.JOINED_BY_INVITATION) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isInvited(int userId, int chatId) {
		int statusId = chatDao.getStatusId(userId, chatId);
		if (statusId == MemberStatus.INVITED) {
			return true;
		}

		return false;
	}
	
	@Override
	public boolean isDeclined(int userId, int chatId) {
		int statusId = chatDao.getStatusId(userId, chatId);
		if (statusId == MemberStatus.DECLINED) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isAdmin(int roleId) {
		if (roleId == MemberRole.ADMIN) {
			return true;
		}

		return false;
	}

}
