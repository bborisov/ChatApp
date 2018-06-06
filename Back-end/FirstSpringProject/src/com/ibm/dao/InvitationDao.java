package com.ibm.dao;

import java.util.List;

import com.ibm.entities.InvitationEntity;
import com.ibm.entities.wrappers.Invitation;

public interface InvitationDao extends BasicDao<InvitationEntity> {

	public Invitation inviteToChat(int userId, int chatId, int invitorId);

	public void updateInvitationStatus(int invitationId, int statusId);

	public List<Invitation> getAllInvitationsForUser(int userId);

}
