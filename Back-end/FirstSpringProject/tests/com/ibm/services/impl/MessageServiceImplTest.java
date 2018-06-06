package com.ibm.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import com.ibm.constants.TestConstants;
import com.ibm.dao.MessageDao;
import com.ibm.dto.MessageCreateDto;
import com.ibm.dto.MessageEditDto;
import com.ibm.entities.MessageEntity;
import com.ibm.utilities.ReflectionUtil;

public class MessageServiceImplTest {

	@InjectMocks
	private MessageServiceImpl messageServiceImpl;

	private MessageDao messageDao;

	@Before
	public void setUp() throws Exception {
		messageServiceImpl = new MessageServiceImpl();

		messageDao = Mockito.mock(MessageDao.class);
		ReflectionUtil.setField(messageServiceImpl, "messageDao", messageDao);
	}

	@Test
	public void testSendMessage() {
		MessageCreateDto messageCreateDto = new MessageCreateDto();
		messageCreateDto.setSenderId(TestConstants.ID);
		messageCreateDto.setChatId(TestConstants.ID);
		messageCreateDto.setContent(TestConstants.STRING);

		messageServiceImpl.sendMessage(messageCreateDto);

		Mockito.verify(messageDao, Mockito.times(1)).sendMessage(TestConstants.ID, TestConstants.ID,
				TestConstants.STRING);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendMessageInvalidContent() {
		MessageCreateDto messageCreateDto = new MessageCreateDto();
		messageCreateDto.setSenderId(TestConstants.ID);
		messageCreateDto.setChatId(TestConstants.ID);
		messageCreateDto.setContent(String.format("%1$256s", "a"));

		messageServiceImpl.sendMessage(messageCreateDto);
	}

	@Test
	public void testEditMessage() {
		MessageEditDto messageEditDto = new MessageEditDto();
		messageEditDto.setMessageId(TestConstants.ID);
		messageEditDto.setContent(TestConstants.STRING);

		MessageEntity messageEntity = new MessageEntity();
		messageEntity.setId(TestConstants.ID);

		Mockito.when(messageDao.getEntityById(TestConstants.ID)).thenReturn(messageEntity);

		messageServiceImpl.editMessage(messageEditDto);

		Mockito.verify(messageDao, Mockito.times(1)).editMessage(TestConstants.ID, TestConstants.STRING);
	}

	@Test(expected = NoSuchElementException.class)
	public void testEditMessageNotStored() {
		MessageEditDto messageEditDto = new MessageEditDto();
		messageEditDto.setMessageId(TestConstants.ID);
		messageEditDto.setContent(TestConstants.STRING);

		messageServiceImpl.editMessage(messageEditDto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEditMessageInvalidContent() {
		MessageEditDto messageEditDto = new MessageEditDto();
		messageEditDto.setMessageId(TestConstants.ID);
		messageEditDto.setContent(String.format("%1$256s", "a"));

		MessageEntity messageEntity = new MessageEntity();
		messageEntity.setId(TestConstants.ID);

		Mockito.when(messageDao.getEntityById(TestConstants.ID)).thenReturn(messageEntity);

		messageServiceImpl.editMessage(messageEditDto);
	}

	@Test
	public void testIsMessageStored() {
		MessageEntity messageEntity = new MessageEntity();
		messageEntity.setId(TestConstants.ID);

		Mockito.when(messageDao.getEntityById(TestConstants.ID)).thenReturn(messageEntity);

		assertTrue(messageServiceImpl.isMessageStored(TestConstants.ID));
	}

	@Test
	public void testIsNotMessageStored() {
		assertFalse(messageServiceImpl.isMessageStored(Mockito.anyInt()));
	}

}
