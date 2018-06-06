package com.ibm.services;

import java.util.List;

import com.ibm.dto.ChatCreateDto;
import com.ibm.dto.ChatJoinDto;
import com.ibm.dto.ChatKickPromoteDto;
import com.ibm.dto.MemberDto;
import com.ibm.entities.wrappers.Chat;

public interface ChatService {

	public Chat createChat(ChatCreateDto chatCreateDto);

	public MemberDto joinChat(ChatJoinDto chatJoinDto);

	public void kickFromChat(ChatKickPromoteDto chatKickPromoteDto);

	public void makeAdmin(ChatKickPromoteDto chatKickPromoteDto);

	public List<MemberDto> getAllMembersFromChat(int chatId);

	public List<Chat> getAllPermittedChats(int userId);

	public boolean isKickPromoteValid(ChatKickPromoteDto chatKickPromoteDto);

	public boolean isMember(int userId, int chatId);

	public boolean isInvited(int userId, int chatId);
	
	public boolean isDeclined(int userId, int chatId);

	public boolean isAdmin(int roleId);

}
