package com.ibm.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.config.AppConfig;
import com.ibm.constants.ChatType;
import com.ibm.constants.MemberRole;
import com.ibm.constants.MemberStatus;
import com.ibm.constants.TestConstants;
import com.ibm.constants.UserStatus;
import com.ibm.dao.ChatDao;
import com.ibm.dao.UserDao;
import com.ibm.services.ChatService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@Rollback
@Transactional
public class ChatDaoImplIT {

	@Autowired
	private ChatDao chatDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ChatService chatService;

	private int userId;
	private int chatId;

	@Before
	public void setUp() throws Exception {
		userId = userDao.createUser(TestConstants.STRING, TestConstants.NON_EXISTING_EMAIL, UserStatus.ACTIVE).getId();
		chatId = chatDao.createChat(TestConstants.STRING, TestConstants.STRING, ChatType.PRIVATE, userId).getId();
	}

	@Test
	public void testCreateChat() {
		assertNotNull(chatDao.getEntityById(chatId));
	}

	@Test
	public void testJoinChat() {
		chatDao.joinChat(userId, chatId);

		assertTrue(chatService.isMember(userId, chatId));
	}

	@Test
	public void testUpdateMemberStatus() {
		chatDao.joinChat(userId, chatId);
		chatDao.updateMemberStatus(userId, chatId, MemberStatus.INVITED);

		assertTrue(chatService.isInvited(userId, chatId));
	}

	@Test(expected = RuntimeException.class)
	public void testUpdateMemberStatusNotExistingMember() {
		chatDao.updateMemberStatus(TestConstants.NON_EXISTING_ID, TestConstants.NON_EXISTING_ID,
				MemberStatus.JOINED_BY_INVITATION);
	}

	@Test
	public void testKickFromChat() {
		chatDao.joinChat(userId, chatId);

		chatDao.kickFromChat(userId, chatId);

		assertEquals(0, chatDao.getStatusId(userId, chatId));
	}

	@Test(expected = RuntimeException.class)
	public void testKickFromChatNonExistingMember() {
		chatDao.kickFromChat(TestConstants.NON_EXISTING_ID, TestConstants.NON_EXISTING_ID);
	}

	@Test
	public void testMakeAdmin() {
		chatDao.joinChat(userId, chatId);

		chatDao.makeAdmin(userId, chatId);

		assertEquals(MemberRole.ADMIN, chatDao.getStatusId(userId, chatId));
	}

	@Test(expected = RuntimeException.class)
	public void testMakeAdminNonExistingMember() {
		chatDao.makeAdmin(TestConstants.NON_EXISTING_ID, TestConstants.NON_EXISTING_ID);
	}

	@Test
	public void testGetAllMembersFromChat() {
		assertNotNull(chatDao.getAllMembersFromChat(chatId));
	}

	@Test
	public void testGetAllPermittedChats() {
		assertNotNull(chatDao.getAllPermittedChats(userId));
	}

	@Test
	public void testGetRoleId() {
		chatDao.joinChat(userId, chatId);

		assertTrue(chatDao.getRoleId(userId, chatId) == MemberRole.USER);
	}

	@Test
	public void testGetRoleIdNonExistingMember() {
		assertEquals(chatDao.getRoleId(TestConstants.NON_EXISTING_ID, TestConstants.NON_EXISTING_ID), 0);
	}

	@Test
	public void testGetStatusId() {
		chatDao.joinChat(userId, chatId);

		assertTrue(chatDao.getStatusId(userId, chatId) == MemberStatus.SELF_JOINED);
	}

	@Test
	public void testGetStatusIdNonExistingMember() {
		assertEquals(chatDao.getStatusId(TestConstants.NON_EXISTING_ID, TestConstants.NON_EXISTING_ID), 0);
	}

}
