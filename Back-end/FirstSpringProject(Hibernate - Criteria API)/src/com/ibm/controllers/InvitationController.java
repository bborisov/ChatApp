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

import com.ibm.dao.UserDao;
import com.ibm.other.Invitation;
import com.ibm.other.InvitationAcceptDeclineDto;
import com.ibm.other.InvitationCreateDto;
import com.ibm.other.User;
import com.ibm.services.InvitationService;


@RestController
public class InvitationController {

	@Autowired
	private InvitationService invitationService;
	
	@Autowired
	private UserDao userDao;
	
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
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/chats/" + invitationCreateDto.getChatId() + "/inviteToChat", flag);
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/users/" + userDao.getUserByEmail(invitationCreateDto.getEmail()).getId() + "/newInvitation", invitation);
		}
	}
	
	@MessageMapping(value = "/acceptInvitation")
	public void acceptInvitation(InvitationAcceptDeclineDto invitationAcceptDto, Principal principal) throws IOException {
		boolean flag = false;
		User user = null;
		try {
			user = invitationService.acceptInvitation(invitationAcceptDto);
			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/users/" + invitationAcceptDto.getUserId() + "/acceptInvitation", flag);
			messagingTemplate.convertAndSend("/topic/chats/" + invitationAcceptDto.getChatId() + "/newUserInChat", (user == null) ? false:user);
		}
	}
	
	@MessageMapping(value = "/declineInvitation")
	public void declineInvitation(InvitationAcceptDeclineDto invitationDeclineDto, Principal principal) throws IOException {
		boolean flag = false;
		try {
			invitationService.declineInvitation(invitationDeclineDto);
			flag = true;
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/users/" + invitationDeclineDto.getUserId() + "/declineInvitation", flag);
		}
	}
	
	@MessageMapping(value = "/getAllInvitationsForUser")
	public void getAllInvitationsForUser(int userId, Principal principal) throws IOException {
		List<Invitation> invitations = invitationService.getAllInvitationsForUser(userId);
		Map<Integer, Invitation> prettyInvitations = invitations.parallelStream().collect(Collectors.toMap(Invitation::getId, Function.identity()));
		messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/getAllInvitationsForUser", prettyInvitations);
	}
	
//	@RequestMapping(value = "/acceptInvitation", method = RequestMethod.PATCH)
//	public void acceptInvitation(int userId, int chatId, int invitationId) throws IOException {
//		invitationService.acceptInvitation(userId, chatId, invitationId);
//	}
//	
//	// PATCH/DELETE
//	@RequestMapping(value = "/declineInvitation", method = RequestMethod.DELETE)
//	public void declineInvitation(int userId, int chatId, int invitationId) throws IOException {
//		invitationService.declineInvitation(userId, chatId, invitationId);
//	}
	
}
