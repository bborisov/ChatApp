package com.ibm.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import com.ibm.constants.InvitationStatus;
import com.ibm.constants.TestConstants;
import com.ibm.constants.UserStatus;
import com.ibm.dao.ChatDao;
import com.ibm.dao.InvitationDao;
import com.ibm.dao.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@Rollback
@Transactional
public class InvitationDaoImplIT {

	@Autowired
	private InvitationDao invitationDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ChatDao chatDao;

	private int anotherUserId;
	private int invitationId;

	@Before
	public void setUp() throws Exception {
		int userId = userDao.createUser(TestConstants.STRING, TestConstants.NON_EXISTING_EMAIL, UserStatus.ACTIVE)
				.getId();
		int chatId = chatDao.createChat(TestConstants.STRING, TestConstants.STRING, ChatType.PRIVATE, userId).getId();
		chatDao.joinChat(userId, chatId);
		anotherUserId = userDao
				.createUser(TestConstants.STRING, TestConstants.ANOTHER_NON_EXISTING_EMAIL, UserStatus.ACTIVE).getId();
		invitationId = invitationDao.inviteToChat(anotherUserId, chatId, userId).getId();
	}

	@Test
	public void testInviteToChat() {
		assertNotNull(invitationDao.getEntityById(invitationId));
	}

	@Test
	public void testUpdateInvitationStatus() {
		invitationDao.updateInvitationStatus(invitationId, InvitationStatus.ACCEPTED);

		assertEquals(InvitationStatus.ACCEPTED, invitationDao.getEntityById(invitationId).getStatusId());
	}

	@Test(expected = RuntimeException.class)
	public void testUpdateInvitationStatusNonExistingInvit() {
		invitationDao.updateInvitationStatus(1111111111, InvitationStatus.ACCEPTED);
	}

	@Test
	public void testGetAllInvitationsForUser() {
		assertNotNull(invitationDao.getAllInvitationsForUser(anotherUserId));
	}

}
