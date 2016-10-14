package com.ibm.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.config.AppConfig;
import com.ibm.config.UserPrincipal;
import com.ibm.constants.ChatType;
import com.ibm.constants.InvitationStatus;
import com.ibm.constants.MemberStatus;
import com.ibm.constants.TestConstants;
import com.ibm.constants.UserStatus;
import com.ibm.dao.ChatDao;
import com.ibm.dao.InvitationDao;
import com.ibm.dao.UserDao;
import com.ibm.dto.InvitationAcceptDeclineDto;
import com.ibm.dto.InvitationCreateDto;
import com.ibm.entities.wrappers.Invitation;
import com.ibm.services.ChatService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@Rollback
@Transactional
public class InvitationControllerIT {
	// not fully tested

	@Autowired
	private InvitationController invitationController;

	@Autowired
	private InvitationDao invitationDao;

	@Autowired
	private ChatDao chatDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ChatService chatService;

	private Principal principal;

	private int userId;
	private int chatId;
	private int userToBeInvitedId;

	@Before
	public void setUp() throws Exception {
		principal = new UserPrincipal(TestConstants.STRING);

		userId = userDao.createUser(TestConstants.STRING, TestConstants.NON_EXISTING_EMAIL, UserStatus.ACTIVE).getId();
		chatId = chatDao.createChat(TestConstants.STRING, TestConstants.STRING, ChatType.PRIVATE, userId).getId();
		chatDao.joinChat(userId, chatId);
		userToBeInvitedId = userDao
				.createUser(TestConstants.STRING, TestConstants.ANOTHER_NON_EXISTING_EMAIL, UserStatus.ACTIVE).getId();
	}

	@Test
	public void testInviteToChat() throws IOException {
		// user has nothing mutual with this chat
		assertEquals(chatDao.getStatusId(userToBeInvitedId, chatId), 0);

		List<Invitation> invitationsBeforeTransaction = invitationDao.getAllInvitationsForUser(userToBeInvitedId);

		InvitationCreateDto invitationCreateDto = new InvitationCreateDto();
		invitationCreateDto.setEmail(TestConstants.ANOTHER_NON_EXISTING_EMAIL);
		invitationCreateDto.setChatId(chatId);
		invitationCreateDto.setInvitorId(userId);

		invitationController.inviteToChat(invitationCreateDto, principal);

		List<Invitation> invitationsAfterTransaction = invitationDao.getAllInvitationsForUser(userToBeInvitedId);

		assertEquals(invitationsBeforeTransaction.size() + 1, invitationsAfterTransaction.size());
		assertTrue(chatService.isInvited(userToBeInvitedId, chatId));
	}

	@Test
	public void testInviteToChatButInvited() throws IOException {
		chatDao.joinChat(userToBeInvitedId, chatId);
		chatDao.updateMemberStatus(userToBeInvitedId, chatId, MemberStatus.INVITED);

		assertTrue(chatService.isInvited(userToBeInvitedId, chatId));

		List<Invitation> invitationsBeforeTransaction = invitationDao.getAllInvitationsForUser(userToBeInvitedId);

		InvitationCreateDto invitationCreateDto = new InvitationCreateDto();
		invitationCreateDto.setEmail(TestConstants.ANOTHER_NON_EXISTING_EMAIL);
		invitationCreateDto.setChatId(chatId);
		invitationCreateDto.setInvitorId(userId);

		invitationController.inviteToChat(invitationCreateDto, principal);

		List<Invitation> invitationsAfterTransaction = invitationDao.getAllInvitationsForUser(userToBeInvitedId);

		assertEquals(invitationsBeforeTransaction.size() + 1, invitationsAfterTransaction.size());
		assertTrue(chatService.isInvited(userToBeInvitedId, chatId));
	}

	@Test
	public void testInviteToChatButDeclined() throws IOException {
		chatDao.joinChat(userToBeInvitedId, chatId);
		chatDao.updateMemberStatus(userToBeInvitedId, chatId, MemberStatus.DECLINED);

		assertTrue(chatService.isDeclined(userToBeInvitedId, chatId));

		List<Invitation> invitationsBeforeTransaction = invitationDao.getAllInvitationsForUser(userToBeInvitedId);

		InvitationCreateDto invitationCreateDto = new InvitationCreateDto();
		invitationCreateDto.setEmail(TestConstants.ANOTHER_NON_EXISTING_EMAIL);
		invitationCreateDto.setChatId(chatId);
		invitationCreateDto.setInvitorId(userId);

		invitationController.inviteToChat(invitationCreateDto, principal);

		List<Invitation> invitationsAfterTransaction = invitationDao.getAllInvitationsForUser(userToBeInvitedId);

		assertEquals(invitationsBeforeTransaction.size() + 1, invitationsAfterTransaction.size());
		assertTrue(chatService.isInvited(userToBeInvitedId, chatId));
	}

	@Test
	public void testInviteToChatButFirstCreateUser() throws IOException {
		String email = "asdqwewqeqweadsdada@abv.bg";

		InvitationCreateDto invitationCreateDto = new InvitationCreateDto();
		invitationCreateDto.setEmail(email);
		invitationCreateDto.setChatId(chatId);
		invitationCreateDto.setInvitorId(userId);

		invitationController.inviteToChat(invitationCreateDto, principal);

		List<Invitation> invitationsAfterTransaction = invitationDao
				.getAllInvitationsForUser(userDao.getUserByEmail(email).getId());

		assertEquals(1, invitationsAfterTransaction.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInviteToChatInvalidEmail() throws IOException {
		InvitationCreateDto invitationCreateDto = new InvitationCreateDto();
		invitationCreateDto.setEmail(TestConstants.INVALID_EMAIL);
		invitationCreateDto.setChatId(chatId);
		invitationCreateDto.setInvitorId(userId);

		invitationController.inviteToChat(invitationCreateDto, principal);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testInviteToChatButMember() throws IOException {
		chatDao.joinChat(userToBeInvitedId, chatId);

		InvitationCreateDto invitationCreateDto = new InvitationCreateDto();
		invitationCreateDto.setEmail(TestConstants.ANOTHER_NON_EXISTING_EMAIL);
		invitationCreateDto.setChatId(chatId);
		invitationCreateDto.setInvitorId(userId);

		invitationController.inviteToChat(invitationCreateDto, principal);
	}

	@Test
	public void testAcceptInvitationWhileInvited() throws IOException {
		chatDao.joinChat(userToBeInvitedId, chatId);
		chatDao.updateMemberStatus(userToBeInvitedId, chatId, MemberStatus.INVITED);
		int invitationId = invitationDao.inviteToChat(userToBeInvitedId, chatId, userId).getId();

		InvitationAcceptDeclineDto invitationAcceptDto = new InvitationAcceptDeclineDto();
		invitationAcceptDto.setUserId(userToBeInvitedId);
		invitationAcceptDto.setChatId(chatId);
		invitationAcceptDto.setInvitationId(invitationId);

		invitationController.acceptInvitation(invitationAcceptDto, principal);

		assertEquals(InvitationStatus.ACCEPTED, invitationDao.getEntityById(invitationId).getStatusId());
		assertTrue(chatService.isMember(userToBeInvitedId, chatId));
	}

	@Test
	public void testAcceptInvitationWhileDeclined() throws IOException {
		chatDao.joinChat(userToBeInvitedId, chatId);
		chatDao.updateMemberStatus(userToBeInvitedId, chatId, MemberStatus.DECLINED);
		int invitationId = invitationDao.inviteToChat(userToBeInvitedId, chatId, userId).getId();

		InvitationAcceptDeclineDto invitationAcceptDto = new InvitationAcceptDeclineDto();
		invitationAcceptDto.setUserId(userToBeInvitedId);
		invitationAcceptDto.setChatId(chatId);
		invitationAcceptDto.setInvitationId(invitationId);

		invitationController.acceptInvitation(invitationAcceptDto, principal);

		assertEquals(InvitationStatus.ACCEPTED, invitationDao.getEntityById(invitationId).getStatusId());
		assertTrue(chatService.isMember(userToBeInvitedId, chatId));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testAcceptInvitationButSelfJoined() throws IOException {
		chatDao.joinChat(userToBeInvitedId, chatId);
		int invitationId = invitationDao.inviteToChat(userToBeInvitedId, chatId, userId).getId();

		InvitationAcceptDeclineDto invitationAcceptDto = new InvitationAcceptDeclineDto();
		invitationAcceptDto.setUserId(userToBeInvitedId);
		invitationAcceptDto.setChatId(chatId);
		invitationAcceptDto.setInvitationId(invitationId);

		invitationController.acceptInvitation(invitationAcceptDto, principal);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testAcceptInvitationButJoinedByInvitation() throws IOException {
		chatDao.joinChat(userToBeInvitedId, chatId);
		chatDao.updateMemberStatus(userToBeInvitedId, chatId, MemberStatus.JOINED_BY_INVITATION);
		int invitationId = invitationDao.inviteToChat(userToBeInvitedId, chatId, userId).getId();

		InvitationAcceptDeclineDto invitationAcceptDto = new InvitationAcceptDeclineDto();
		invitationAcceptDto.setUserId(userToBeInvitedId);
		invitationAcceptDto.setChatId(chatId);
		invitationAcceptDto.setInvitationId(invitationId);

		invitationController.acceptInvitation(invitationAcceptDto, principal);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAcceptInvitationStatusProblem() throws IOException {
		InvitationAcceptDeclineDto invitationAcceptDto = new InvitationAcceptDeclineDto();
		invitationAcceptDto.setUserId(TestConstants.NON_EXISTING_ID);
		invitationAcceptDto.setChatId(TestConstants.NON_EXISTING_ID);
		invitationAcceptDto.setInvitationId(TestConstants.NON_EXISTING_ID);

		invitationController.acceptInvitation(invitationAcceptDto, principal);
	}

	@Test
	public void testDeclineInvitationWhileInvited() throws IOException {
		chatDao.joinChat(userToBeInvitedId, chatId);
		chatDao.updateMemberStatus(userToBeInvitedId, chatId, MemberStatus.INVITED);
		int invitationId = invitationDao.inviteToChat(userToBeInvitedId, chatId, userId).getId();

		InvitationAcceptDeclineDto invitationDeclineDto = new InvitationAcceptDeclineDto();
		invitationDeclineDto.setUserId(userToBeInvitedId);
		invitationDeclineDto.setChatId(chatId);
		invitationDeclineDto.setInvitationId(invitationId);

		invitationController.declineInvitation(invitationDeclineDto, principal);

		assertEquals(InvitationStatus.DECLINED, invitationDao.getEntityById(invitationId).getStatusId());
		assertTrue(chatService.isDeclined(userToBeInvitedId, chatId));
	}

	@Test
	public void testDeclineInvitationWhileDeclined() throws IOException {
		chatDao.joinChat(userToBeInvitedId, chatId);
		chatDao.updateMemberStatus(userToBeInvitedId, chatId, MemberStatus.DECLINED);
		int invitationId = invitationDao.inviteToChat(userToBeInvitedId, chatId, userId).getId();

		InvitationAcceptDeclineDto invitationDeclineDto = new InvitationAcceptDeclineDto();
		invitationDeclineDto.setUserId(userToBeInvitedId);
		invitationDeclineDto.setChatId(chatId);
		invitationDeclineDto.setInvitationId(invitationId);

		invitationController.declineInvitation(invitationDeclineDto, principal);

		assertEquals(InvitationStatus.DECLINED, invitationDao.getEntityById(invitationId).getStatusId());
		assertTrue(chatService.isDeclined(userToBeInvitedId, chatId));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testDeclineInvitationButSelfJoined() throws IOException {
		chatDao.joinChat(userToBeInvitedId, chatId);
		int invitationId = invitationDao.inviteToChat(userToBeInvitedId, chatId, userId).getId();

		InvitationAcceptDeclineDto invitationDeclineDto = new InvitationAcceptDeclineDto();
		invitationDeclineDto.setUserId(userToBeInvitedId);
		invitationDeclineDto.setChatId(chatId);
		invitationDeclineDto.setInvitationId(invitationId);

		invitationController.declineInvitation(invitationDeclineDto, principal);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testDeclineInvitationButJoinedByInvitation() throws IOException {
		chatDao.joinChat(userToBeInvitedId, chatId);
		chatDao.updateMemberStatus(userToBeInvitedId, chatId, MemberStatus.JOINED_BY_INVITATION);
		int invitationId = invitationDao.inviteToChat(userToBeInvitedId, chatId, userId).getId();

		InvitationAcceptDeclineDto invitationDeclineDto = new InvitationAcceptDeclineDto();
		invitationDeclineDto.setUserId(userToBeInvitedId);
		invitationDeclineDto.setChatId(chatId);
		invitationDeclineDto.setInvitationId(invitationId);

		invitationController.declineInvitation(invitationDeclineDto, principal);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeclineInvitationStatusProblem() throws IOException {
		InvitationAcceptDeclineDto invitationDeclineDto = new InvitationAcceptDeclineDto();
		invitationDeclineDto.setUserId(TestConstants.NON_EXISTING_ID);
		invitationDeclineDto.setChatId(TestConstants.NON_EXISTING_ID);
		invitationDeclineDto.setInvitationId(TestConstants.NON_EXISTING_ID);

		invitationController.declineInvitation(invitationDeclineDto, principal);
	}

	@Test
	public void testGetAllInvitationsForUser() throws IOException {
		invitationController.getAllInvitationsForUser(userToBeInvitedId, principal);

		assertNotNull(invitationDao.getAllInvitationsForUser(userToBeInvitedId));
	}

}
