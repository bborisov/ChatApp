package com.ibm.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.constants.InvitationStatus;
import com.ibm.constants.MemberStatus;
import com.ibm.constants.UserStatus;
import com.ibm.dao.ChatDao;
import com.ibm.dao.InvitationDao;
import com.ibm.dao.UserDao;
import com.ibm.dto.InvitationAcceptDeclineDto;
import com.ibm.dto.InvitationCreateDto;
import com.ibm.entities.UserEntity;
import com.ibm.entities.wrappers.Invitation;
import com.ibm.entities.wrappers.User;
import com.ibm.services.ChatService;
import com.ibm.services.InvitationService;
import com.ibm.validators.EmailValidator;

@Service
public class InvitationServiceImpl implements InvitationService {

	@Autowired
	private ChatService chatService;

	@Autowired
	private InvitationDao invitationDao;

	@Autowired
	private ChatDao chatDao;

	@Autowired
	private UserDao userDao;

	@Override
	public Invitation inviteToChat(InvitationCreateDto invitationCreateDto) {
		String email = invitationCreateDto.getEmail();

		EmailValidator emailValidator = new EmailValidator(email);

		if (!emailValidator.isValid()) {
			throw new IllegalArgumentException("Invalid e-mail address!");
		}

		User user = userDao.getUserByEmail(email);

		if (user == null) {
			user = (User) new UserEntity();
			user.setEmail(email);
			user = userDao.createUser(null, email, null, null, UserStatus.INACTIVE);
		}

		int userId = user.getId();
		int chatId = invitationCreateDto.getChatId();

		if (chatService.isMember(userId, chatId)) {
			throw new UnsupportedOperationException("Cannot invite this user - he/she is already a member!");
		}

		Invitation invitation = invitationDao.inviteToChat(userId, chatId, invitationCreateDto.getInvitorId());

		if (chatService.isInvited(userId, chatId)) {
			return invitation;
		} else if (chatService.isDeclined(userId, chatId)) {
			chatDao.updateMemberStatus(userId, chatId, MemberStatus.INVITED);
			return invitation;
		} else {
			chatDao.joinChat(userId, chatId);
			chatDao.updateMemberStatus(userId, chatId, MemberStatus.INVITED);
			return invitation;
		}
	}

	@Override
	public User acceptInvitation(InvitationAcceptDeclineDto invitationAcceptDto) {
		int userId = invitationAcceptDto.getUserId();
		int chatId = invitationAcceptDto.getChatId();

		int statusId = chatDao.getStatusId(userId, chatId);

		if (statusId == MemberStatus.INVITED || statusId == MemberStatus.DECLINED) {
			invitationDao.updateInvitationStatus(invitationAcceptDto.getInvitationId(), InvitationStatus.ACCEPTED);
			chatDao.updateMemberStatus(userId, chatId, MemberStatus.JOINED_BY_INVITATION);

			return userDao.getEntityById(userId);
		}

		if (statusId == MemberStatus.SELF_JOINED || statusId == MemberStatus.JOINED_BY_INVITATION) {
			throw new UnsupportedOperationException("Already joined!");
		}

		throw new IllegalArgumentException("Problem with the status!");
	}

	@Override
	public void declineInvitation(InvitationAcceptDeclineDto invitationDeclineDto) {
		int userId = invitationDeclineDto.getUserId();
		int chatId = invitationDeclineDto.getChatId();

		int statusId = chatDao.getStatusId(userId, chatId);

		if (statusId == MemberStatus.INVITED || statusId == MemberStatus.DECLINED) {
			invitationDao.updateInvitationStatus(invitationDeclineDto.getInvitationId(), InvitationStatus.DECLINED);
			chatDao.updateMemberStatus(userId, chatId, MemberStatus.DECLINED);

			return;
		}

		if (statusId == MemberStatus.SELF_JOINED || statusId == MemberStatus.JOINED_BY_INVITATION) {
			throw new UnsupportedOperationException("Already joined!");
		}

		throw new IllegalArgumentException("Problem with the status!");
	}

	@Override
	public List<Invitation> getAllInvitationsForUser(int userId) {
		return invitationDao.getAllInvitationsForUser(userId);
	}

}
