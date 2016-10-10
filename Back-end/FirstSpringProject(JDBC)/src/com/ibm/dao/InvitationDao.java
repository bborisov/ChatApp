package com.ibm.dao;

import java.util.List;

import com.ibm.other.Invitation;
import com.ibm.other.User;

public interface InvitationDao {
	
	public Invitation inviteToChat(int userId, int chatId, int invitorId);
	
	public User acceptInvitation(int userId, int chatId, int invitationId);
	
	public void declineInvitation(int userId, int chatId, int invitationId);
	
	public List<Invitation> getAllInvitationsForUser(int userId);

}
