package com.ibm.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.config.AppConfig;
import com.ibm.config.UserPrincipal;
import com.ibm.constants.ChatType;
import com.ibm.constants.TestConstants;
import com.ibm.constants.UserStatus;
import com.ibm.dao.ChatDao;
import com.ibm.dao.MessageDao;
import com.ibm.dao.UserDao;
import com.ibm.dto.MessageCreateDto;
import com.ibm.dto.MessageEditDto;
import com.ibm.entities.wrappers.Message;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@Rollback
@Transactional
public class MessageControllerIT {

	@Autowired
	private MessageController messageController;

	@Autowired
	private MessageDao messageDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ChatDao chatDao;

	@Autowired
	private AbstractSubscribableChannel brokerChannel;

	private TestChannelInterceptor brokerChannelInterceptor;

	private Principal principal;

	private int userId;
	private int chatId;
	private int messageId;

	@Before
	public void setUp() throws Exception {
		principal = new UserPrincipal(TestConstants.STRING);

		brokerChannelInterceptor = new TestChannelInterceptor();
		brokerChannel.addInterceptor(brokerChannelInterceptor);

		userId = userDao.createUser(TestConstants.STRING, TestConstants.NON_EXISTING_EMAIL, TestConstants.STRING,
				TestConstants.STRING, UserStatus.ACTIVE).getId();
		chatId = chatDao.createChat(TestConstants.STRING, TestConstants.STRING, ChatType.PRIVATE, userId).getId();
		chatDao.joinChat(userId, chatId);
		messageId = messageDao.sendMessage(userId, chatId, TestConstants.STRING).getId();
	}

	@Test
	public void testSendMessage() throws IOException, InterruptedException {
		List<Message> messagesBeforeTransaction = messageDao.getAllMessagesByChat(chatId);

		MessageCreateDto messageCreateDto = new MessageCreateDto();
		messageCreateDto.setSenderId(userId);
		messageCreateDto.setChatId(chatId);
		messageCreateDto.setContent(TestConstants.STRING);

		messageController.sendMessage(messageCreateDto, principal);

		List<Message> messagesAfterTransaction = messageDao.getAllMessagesByChat(chatId);

		assertEquals(messagesBeforeTransaction.size() + 1, messagesAfterTransaction.size());

		brokerChannelInterceptor.setIncludedDestinations("/user/**");

		org.springframework.messaging.Message<?> result = brokerChannelInterceptor.awaitMessage(5);
		assertNotNull(result);

		String json = new String((byte[]) result.getPayload(), Charset.forName("UTF-8"));

		assertTrue(json.contains(String.valueOf(true)));

		brokerChannelInterceptor.setIncludedDestinations("/topic/**");

		result = brokerChannelInterceptor.awaitMessage(5);
		assertNotNull(result);

		json = new String((byte[]) result.getPayload(), Charset.forName("UTF-8"));

		assertTrue(json.contains(String.valueOf(userId)));
		assertTrue(json.contains(String.valueOf(chatId)));
		assertTrue(json.contains(TestConstants.STRING));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendMessageInvalidContent() throws IOException {
		MessageCreateDto messageCreateDto = new MessageCreateDto();
		messageCreateDto.setSenderId(TestConstants.ID);
		messageCreateDto.setChatId(TestConstants.ID);

		messageController.sendMessage(messageCreateDto, principal);
	}

	@Test
	public void testEditMessage() throws IOException, InterruptedException {
		MessageEditDto messageEditDto = new MessageEditDto();
		messageEditDto.setMessageId(messageId);
		messageEditDto.setContent(TestConstants.STRING);

		messageController.editMessage(messageEditDto, principal);

		assertEquals(TestConstants.STRING, messageDao.getEntityById(messageId).getContent());

		brokerChannelInterceptor.setIncludedDestinations("/user/**");

		org.springframework.messaging.Message<?> result = brokerChannelInterceptor.awaitMessage(5);
		assertNotNull(result);

		String json = new String((byte[]) result.getPayload(), Charset.forName("UTF-8"));

		assertTrue(json.contains(String.valueOf(true)));

		brokerChannelInterceptor.setIncludedDestinations("/topic/**");

		result = brokerChannelInterceptor.awaitMessage(5);
		assertNotNull(result);

		json = new String((byte[]) result.getPayload(), Charset.forName("UTF-8"));

		assertTrue(json.contains("\"id\":" + messageId));
		assertTrue(json.contains(TestConstants.STRING));
	}

	@Test(expected = NoSuchElementException.class)
	public void testEditMessageNotStored() throws IOException {
		MessageEditDto messageEditDto = new MessageEditDto();
		messageEditDto.setMessageId(TestConstants.NON_EXISTING_ID);

		messageController.editMessage(messageEditDto, principal);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEditMessageInvalidContent() throws IOException {
		MessageEditDto messageEditDto = new MessageEditDto();
		messageEditDto.setMessageId(messageId);

		messageController.editMessage(messageEditDto, principal);
	}

	@Test
	public void testGetAllMessagesFromChat() throws IOException, InterruptedException {
		messageController.getAllMessagesFromChat(chatId, principal);

		brokerChannelInterceptor.setIncludedDestinations("/user/**");

		org.springframework.messaging.Message<?> result = brokerChannelInterceptor.awaitMessage(5);
		assertNotNull(result);

		String json = new String((byte[]) result.getPayload(), Charset.forName("UTF-8"));

		assertTrue(json.contains("\"id\":" + messageId));
	}

}
