package com.ibm.services;

import java.util.List;

import com.ibm.dto.InvitationAcceptDeclineDto;
import com.ibm.dto.InvitationCreateDto;
import com.ibm.entities.wrappers.Invitation;
import com.ibm.entities.wrappers.User;

public interface InvitationService {

	public Invitation inviteToChat(InvitationCreateDto invitationCreateDto);

	public User acceptInvitation(InvitationAcceptDeclineDto invitationAcceptDto);

	public void declineInvitation(InvitationAcceptDeclineDto invitationDeclineDto);

	public List<Invitation> getAllInvitationsForUser(int userId);

}
