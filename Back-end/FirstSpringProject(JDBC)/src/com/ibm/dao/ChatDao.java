package com.ibm.dao;

import java.util.List;

import com.ibm.other.Chat;
import com.ibm.other.MemberDto;

public interface ChatDao {
	
	public Chat createChat(String chatName, String summary, int typeId, int creatorId);
	
	public void joinChat(int userId, int chatId);
	
	public void updateMemberStatus(int userId, int chatId, int statusId);
	
	public void kickFromChat(int userId, int chatId);
	
	public void makeAdmin(int userId, int chatId);
	
	public List<MemberDto> getAllMembersFromChats(int chatId);
	
	public List<Chat> getAllPermittedChats(int userId);
	
	public int getStatusId(int userId, int chatId);
	
	public boolean isMember(int userId, int chatId);
	
	public boolean isInvited(int userId, int chatId);
	
	public boolean isAdmin(int roleId);

}
