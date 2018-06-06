package com.ibm.dao;

import java.util.List;

import com.ibm.dto.MemberDto;
import com.ibm.entities.ChatEntity;
import com.ibm.entities.wrappers.Chat;

public interface ChatDao extends BasicDao<ChatEntity> {

	public Chat createChat(String chatName, String summary, int typeId, int creatorId);

	public void joinChat(int userId, int chatId);

	public void updateMemberStatus(int userId, int chatId, int statusId);

	public void kickFromChat(int userId, int chatId);

	public void makeAdmin(int userId, int chatId);

	public List<MemberDto> getAllMembersFromChat(int chatId);

	public List<Chat> getAllPermittedChats(int userId);
	
	public int getRoleId(int userId, int chatId);

	public int getStatusId(int userId, int chatId);

}
