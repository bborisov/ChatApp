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
import com.ibm.constants.TestConstants;
import com.ibm.constants.UserStatus;
import com.ibm.dao.ChatDao;
import com.ibm.dao.MessageDao;
import com.ibm.dao.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@Rollback
@Transactional
public class MessageDaoImplIT {

	@Autowired
	private MessageDao messageDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ChatDao chatDao;

	private int chatId;
	private int messageId;

	@Before
	public void setUp() throws Exception {
		int userId = userDao.createUser(TestConstants.STRING, TestConstants.NON_EXISTING_EMAIL, TestConstants.STRING,
				TestConstants.STRING, UserStatus.ACTIVE).getId();
		chatId = chatDao.createChat(TestConstants.STRING, TestConstants.STRING, ChatType.PRIVATE, userId).getId();
		chatDao.joinChat(userId, chatId);
		messageId = messageDao.sendMessage(userId, chatId, TestConstants.STRING).getId();
	}

	@Test
	public void testSendMessage() {
		assertNotNull(messageDao.getEntityById(messageId));
	}

	@Test
	public void testEditMessage() {
		assertNotNull(messageDao.editMessage(messageId, TestConstants.STRING));

		assertEquals(TestConstants.STRING, messageDao.getEntityById(messageId).getContent());
	}

	@Test
	public void testGetAllMessagesByChat() {
		assertNotNull(messageDao.getAllMessagesByChat(chatId));
	}

}
