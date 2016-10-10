package com.ibm.services;

import java.util.List;

import com.ibm.other.Invitation;
import com.ibm.other.InvitationAcceptDeclineDto;
import com.ibm.other.InvitationCreateDto;
import com.ibm.other.User;

public interface InvitationService {
	
	public Invitation inviteToChat(InvitationCreateDto invitationCreateDto);
	
	public User acceptInvitation(InvitationAcceptDeclineDto invitationAcceptDto);
	
	public void declineInvitation(InvitationAcceptDeclineDto invitationDeclineDto);
	
	public List<Invitation> getAllInvitationsForUser(int userId);

}
