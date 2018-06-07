package com.ibm.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.dao.ChatDao;
import com.ibm.dao.UserDao;
import com.ibm.dto.InvitationAcceptDeclineDto;
import com.ibm.dto.InvitationCreateDto;
import com.ibm.entities.wrappers.Invitation;
import com.ibm.entities.wrappers.User;
import com.ibm.services.InvitationService;

@RestController
public class InvitationController {

	@Autowired
	private InvitationService invitationService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ChatDao chatDao;

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@MessageMapping(value = "/inviteToChat")
	public void inviteToChat(InvitationCreateDto invitationCreateDto, Principal principal) throws IOException {
		boolean flag = false;
		Invitation invitation = null;
		try {
			invitation = invitationService.inviteToChat(invitationCreateDto);
			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(),
					"/queue/chats/" + invitationCreateDto.getChatId() + "/inviteToChat", flag);
			if (flag == true) {
				messagingTemplate.convertAndSend(
						"/topic/users/" + ((User) userDao.getUserByEmail(invitationCreateDto.getEmail())).getId()
								+ "/newChatForInvitation",
						chatDao.getEntityById(invitationCreateDto.getChatId()));
				messagingTemplate.convertAndSend("/topic/users/"
						+ ((User) userDao.getUserByEmail(invitationCreateDto.getEmail())).getId() + "/newInvitation",
						(invitation == null) ? false : invitation);
			}
		}
	}

	@MessageMapping(value = "/acceptInvitation")
	public void acceptInvitation(InvitationAcceptDeclineDto invitationAcceptDto, Principal principal)
			throws IOException {
		boolean flag = false;
		User user = null;
		try {
			user = invitationService.acceptInvitation(invitationAcceptDto);
			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(),
					"/queue/users/" + invitationAcceptDto.getUserId() + "/acceptInvitation", flag);
			messagingTemplate.convertAndSend("/topic/chats/" + invitationAcceptDto.getChatId() + "/newUserInChat",
					(user == null) ? false : user);
		}
	}

	@MessageMapping(value = "/declineInvitation")
	public void declineInvitation(InvitationAcceptDeclineDto invitationDeclineDto, Principal principal)
			throws IOException {
		boolean flag = false;
		try {
			invitationService.declineInvitation(invitationDeclineDto);
			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(),
					"/queue/users/" + invitationDeclineDto.getUserId() + "/declineInvitation", flag);
		}
	}

	@MessageMapping(value = "/getAllInvitationsForUser")
	public void getAllInvitationsForUser(int userId, Principal principal) throws IOException {
		List<Invitation> invitations = invitationService.getAllInvitationsForUser(userId);
		Map<Integer, Invitation> prettyInvitations = invitations.parallelStream()
				.collect(Collectors.toMap(Invitation::getId, Function.identity()));
		messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/getAllInvitationsForUser",
				prettyInvitations);
	}

}
