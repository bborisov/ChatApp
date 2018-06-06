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
import com.ibm.constants.MemberRole;
import com.ibm.constants.MemberStatus;
import com.ibm.constants.TestConstants;
import com.ibm.constants.UserStatus;
import com.ibm.dao.ChatDao;
import com.ibm.dao.UserDao;
import com.ibm.dto.ChatCreateDto;
import com.ibm.dto.ChatJoinDto;
import com.ibm.dto.ChatKickPromoteDto;
import com.ibm.entities.wrappers.Chat;
import com.ibm.services.ChatService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@Rollback
@Transactional
public class ChatControllerIT {
	// not fully tested

	@Autowired
	private ChatController chatController;

	@Autowired
	private ChatDao chatDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ChatService chatService;

	private Principal principal;

	private int userId;
	private int chatId;

	@Before
	public void setUp() throws Exception {
		principal = new UserPrincipal(TestConstants.STRING);

		userId = userDao.createUser(TestConstants.STRING, TestConstants.NON_EXISTING_EMAIL, UserStatus.ACTIVE).getId();
		chatId = chatDao.createChat(TestConstants.STRING, TestConstants.STRING, ChatType.PRIVATE, userId).getId();
	}

	@Test
	public void testCreateChat() throws IOException {
		List<Chat> chatsBeforeTransaction = chatDao.getAllPermittedChats(userId);

		ChatCreateDto chatCreateDto = new ChatCreateDto();
		chatCreateDto.setName(TestConstants.STRING);
		chatCreateDto.setSummary(TestConstants.STRING);
		chatCreateDto.setTypeId(ChatType.PRIVATE);
		chatCreateDto.setCreatorId(userId);

		chatController.createChat(chatCreateDto, principal);

		List<Chat> chatsAfterTransaction = chatDao.getAllPermittedChats(userId);

		assertEquals(chatsBeforeTransaction.size() + 1, chatsAfterTransaction.size());
		// TODO Validate list is equal to the fetched one
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateChatInvalidName() throws IOException {
		ChatCreateDto chatCreateDto = new ChatCreateDto();
		chatCreateDto.setSummary(TestConstants.STRING);
		chatCreateDto.setTypeId(ChatType.PRIVATE);
		chatCreateDto.setCreatorId(userId);

		chatController.createChat(chatCreateDto, principal);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateChatInvalidSummary() throws IOException {
		ChatCreateDto chatCreateDto = new ChatCreateDto();
		chatCreateDto.setName(TestConstants.STRING);
		chatCreateDto.setTypeId(ChatType.PRIVATE);
		chatCreateDto.setCreatorId(userId);

		chatController.createChat(chatCreateDto, principal);
	}

	@Test
	public void testJoinChat() throws IOException {
		// user has nothing mutual with this chat
		ChatJoinDto chatJoinDto = new ChatJoinDto();
		chatJoinDto.setUserId(userId);
		chatJoinDto.setChatId(chatId);

		chatController.joinChat(chatJoinDto, principal);

		assertTrue(chatService.isMember(userId, chatId));
	}

	@Test
	public void testJoinChatButInvited() throws IOException {
		chatDao.joinChat(userId, chatId);
		chatDao.updateMemberStatus(userId, chatId, MemberStatus.INVITED);

		ChatJoinDto chatJoinDto = new ChatJoinDto();
		chatJoinDto.setUserId(userId);
		chatJoinDto.setChatId(chatId);

		chatController.joinChat(chatJoinDto, principal);

		assertTrue(chatService.isMember(userId, chatId));
	}

	@Test
	public void testJoinChatButDeclined() throws IOException {
		chatDao.joinChat(userId, chatId);
		chatDao.updateMemberStatus(userId, chatId, MemberStatus.DECLINED);

		ChatJoinDto chatJoinDto = new ChatJoinDto();
		chatJoinDto.setUserId(userId);
		chatJoinDto.setChatId(chatId);

		chatController.joinChat(chatJoinDto, principal);

		assertTrue(chatService.isMember(userId, chatId));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testJoinChatChatAlreadyMember() throws IOException {
		chatDao.joinChat(userId, chatId);

		ChatJoinDto chatJoinDto = new ChatJoinDto();
		chatJoinDto.setUserId(userId);
		chatJoinDto.setChatId(chatId);

		chatController.joinChat(chatJoinDto, principal);
	}

	@Test
	public void testKickFromChat() throws IOException {
		// user in not an admin
		chatDao.joinChat(userId, chatId);

		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(userId);
		chatKickPromoteDto.setChatId(chatId);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		chatController.kickFromChat(chatKickPromoteDto, principal);

		assertEquals(chatDao.getStatusId(userId, chatId), 0);
	}

	@Test(expected = RuntimeException.class)
	public void testKickFromChatIncorrectUserId() throws IOException {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(TestConstants.NON_EXISTING_ID);
		chatKickPromoteDto.setChatId(chatId);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		chatController.kickFromChat(chatKickPromoteDto, principal);
	}

	@Test(expected = RuntimeException.class)
	public void testKickFromChatIncorrectChatId() throws IOException {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(userId);
		chatKickPromoteDto.setChatId(TestConstants.NON_EXISTING_ID);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		chatController.kickFromChat(chatKickPromoteDto, principal);
	}

	@Test(expected = RuntimeException.class)
	public void testKickFromChatIncorrectDoerRoleId() throws IOException {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(userId);
		chatKickPromoteDto.setChatId(chatId);
		chatKickPromoteDto.setDoerRoleId(TestConstants.NON_EXISTING_ID);

		chatController.kickFromChat(chatKickPromoteDto, principal);
	}

	@Test
	public void testMakeAdmin() throws IOException {
		// user in not an admin
		chatDao.joinChat(userId, chatId);

		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(userId);
		chatKickPromoteDto.setChatId(chatId);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		chatController.makeAdmin(chatKickPromoteDto, principal);

		assertEquals(chatDao.getRoleId(userId, chatId), MemberRole.ADMIN);
	}

	@Test(expected = RuntimeException.class)
	public void testMakeAdminIncorrectUserId() throws IOException {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(TestConstants.NON_EXISTING_ID);
		chatKickPromoteDto.setChatId(chatId);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		chatController.makeAdmin(chatKickPromoteDto, principal);
	}

	@Test(expected = RuntimeException.class)
	public void testMakeAdminIncorrectChatId() throws IOException {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(userId);
		chatKickPromoteDto.setChatId(TestConstants.NON_EXISTING_ID);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		chatController.makeAdmin(chatKickPromoteDto, principal);
	}

	@Test(expected = RuntimeException.class)
	public void testMakeAdminIncorrectDoerRoleId() throws IOException {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(userId);
		chatKickPromoteDto.setChatId(chatId);
		chatKickPromoteDto.setDoerRoleId(TestConstants.NON_EXISTING_ID);

		chatController.makeAdmin(chatKickPromoteDto, principal);
	}

	@Test
	public void testGetAllMembersFromChats() throws IOException {
		chatController.getAllMembersFromChats(chatId, principal);

		assertNotNull(chatDao.getAllMembersFromChat(chatId));
	}

	@Test
	public void testGetAllPermittedChats() throws IOException {
		chatController.getAllPermittedChats(userId, principal);

		assertNotNull(chatDao.getAllPermittedChats(userId));
	}

}
