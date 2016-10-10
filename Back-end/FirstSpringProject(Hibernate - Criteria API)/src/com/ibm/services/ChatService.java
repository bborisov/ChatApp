package com.ibm.services;

import java.util.List;

import com.ibm.other.Chat;
import com.ibm.other.ChatDto;
import com.ibm.other.ChatJoinDto;
import com.ibm.other.ChatKickPromoteDto;
import com.ibm.other.MemberDto;

public interface ChatService {
	
	public Chat createChat(ChatDto chatDto);
	
	public MemberDto joinChat(ChatJoinDto chatJoinDto);
	
	public void kickFromChat(ChatKickPromoteDto chatKickPromoteDto);
	
	public void makeAdmin(ChatKickPromoteDto chatKickPromoteDto);
	
	public List<MemberDto> getAllMembersFromChats(int chatId);
	
	public List<Chat> getAllPermittedChats(int userId);

}
