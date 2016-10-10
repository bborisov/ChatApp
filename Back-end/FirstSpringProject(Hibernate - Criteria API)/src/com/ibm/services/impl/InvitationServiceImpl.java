package com.ibm.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.dao.ChatDao;
import com.ibm.dao.InvitationDao;
import com.ibm.dao.UserDao;
import com.ibm.entities.UserEntity;
import com.ibm.other.Invitation;
import com.ibm.other.InvitationAcceptDeclineDto;
import com.ibm.other.InvitationCreateDto;
import com.ibm.other.MemberStatus;
import com.ibm.other.User;
import com.ibm.other.UserStatus;
import com.ibm.services.InvitationService;
import com.ibm.services.UserService;

@Service
public class InvitationServiceImpl implements InvitationService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private InvitationDao invitationDao;
	
	@Autowired
	private ChatDao chatDao;
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public Invitation inviteToChat(InvitationCreateDto invitationCreateDto) {
		String email = invitationCreateDto.getEmail();
		
		if (!userService.isEmailValid(email)) {
			throw new RuntimeException("Invalid e-mail address!");
		}
		
		User user = userDao.getUserByEmail(email);
		
		if (user == null) {
			user = (User) new UserEntity();
			user.setEmail(email);
			user = userDao.createUser(null, email, UserStatus.INACTIVE);
		}
		
		int userId = user.getId();
		int chatId = invitationCreateDto.getChatId();
		
		if (chatDao.isMember(userId, chatId)) {
			throw new RuntimeException("Cannot invite this user - he/she is already a member!");
		}
		
		Invitation invitation = invitationDao.inviteToChat(userId, chatId, invitationCreateDto.getInvitorId());
		
		if (!chatDao.isInvited(userId, chatId)) {
			chatDao.joinChat(userId, chatId);
			chatDao.updateMemberStatus(userId, chatId, MemberStatus.INVITED);
		}
		
		return invitation;
	}
	
	@Override
	public User acceptInvitation(InvitationAcceptDeclineDto invitationAcceptDto) {
		int userId = invitationAcceptDto.getUserId();
		int chatId = invitationAcceptDto.getChatId();
		
		int statusId = chatDao.getStatusId(userId, chatId);

		if (statusId == MemberStatus.INVITED) {
			return invitationDao.acceptInvitation(userId, chatId, invitationAcceptDto.getInvitationId());
		} else if (statusId == MemberStatus.SELF_JOINED || statusId == MemberStatus.JOINED_BY_INVITATION) {
			throw new RuntimeException("Already joined!");
		} else {
			throw new RuntimeException("Problem with the status!");
		}
	}

	@Override
	public void declineInvitation(InvitationAcceptDeclineDto invitationDeclineDto) {
		int userId = invitationDeclineDto.getUserId();
		int chatId = invitationDeclineDto.getChatId();
		
		int statusId = chatDao.getStatusId(userId, chatId);

		if (statusId == MemberStatus.INVITED) {
			invitationDao.declineInvitation(userId, chatId, invitationDeclineDto.getInvitationId());
		} else if (statusId == MemberStatus.SELF_JOINED || statusId == MemberStatus.JOINED_BY_INVITATION) {
			throw new RuntimeException("Already joined!");
		} else {
			throw new RuntimeException("Problem with the status!");
		}
	}

	@Override
	public List<Invitation> getAllInvitationsForUser(int userId) {
		return invitationDao.getAllInvitationsForUser(userId);
	}

}
