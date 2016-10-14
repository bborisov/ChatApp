package com.ibm.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import com.ibm.constants.MemberRole;
import com.ibm.constants.MemberStatus;
import com.ibm.constants.TestConstants;
import com.ibm.dao.ChatDao;
import com.ibm.dao.UserDao;
import com.ibm.dto.ChatCreateDto;
import com.ibm.dto.ChatJoinDto;
import com.ibm.dto.ChatKickPromoteDto;
import com.ibm.entities.UserEntity;
import com.ibm.utilities.ReflectionUtil;

public class ChatServiceImplTest {

	@InjectMocks
	private ChatServiceImpl chatServiceImpl;

	private ChatDao chatDao;
	private UserDao userDao;

	@Before
	public void setUp() throws Exception {
		chatServiceImpl = new ChatServiceImpl();

		chatDao = Mockito.mock(ChatDao.class);
		ReflectionUtil.setField(chatServiceImpl, "chatDao", chatDao);

		userDao = Mockito.mock(UserDao.class);
		ReflectionUtil.setField(chatServiceImpl, "userDao", userDao);
	}

	@Test
	public void testCreateChat() {
		ChatCreateDto chatDto = new ChatCreateDto();
		chatDto.setName(TestConstants.STRING);
		chatDto.setSummary(TestConstants.STRING);
		chatDto.setTypeId(TestConstants.ID);
		chatDto.setCreatorId(TestConstants.ID);

		chatServiceImpl.createChat(chatDto);

		Mockito.verify(chatDao, Mockito.times(1)).createChat(TestConstants.STRING, TestConstants.STRING,
				TestConstants.ID, TestConstants.ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateChatNameTooLong() {
		ChatCreateDto chatDto = new ChatCreateDto();
		chatDto.setName(String.format("%1$51s", "a"));
		chatDto.setSummary(TestConstants.STRING);

		chatServiceImpl.createChat(chatDto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateChatSummaryTooLong() {
		ChatCreateDto chatDto = new ChatCreateDto();
		chatDto.setName(TestConstants.STRING);
		chatDto.setSummary(String.format("%1$256s", "a"));

		chatServiceImpl.createChat(chatDto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateChatNameNull() {
		ChatCreateDto chatDto = new ChatCreateDto();
		chatDto.setSummary(TestConstants.STRING);

		chatServiceImpl.createChat(chatDto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateChatSummaryNull() {
		ChatCreateDto chatDto = new ChatCreateDto();
		chatDto.setName(TestConstants.STRING);

		chatServiceImpl.createChat(chatDto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateChatNameEmptyString() {
		ChatCreateDto chatDto = new ChatCreateDto();
		chatDto.setName("");
		chatDto.setSummary(TestConstants.STRING);

		chatServiceImpl.createChat(chatDto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateChatSummaryEmptyString() {
		ChatCreateDto chatDto = new ChatCreateDto();
		chatDto.setName(TestConstants.STRING);
		chatDto.setSummary("");

		chatServiceImpl.createChat(chatDto);
	}

	@Test
	public void testJoinChatChat() {
		ChatJoinDto chatJoinDto = new ChatJoinDto();
		chatJoinDto.setUserId(TestConstants.ID);
		chatJoinDto.setChatId(TestConstants.ID);

		UserEntity userEntity = new UserEntity();
		userEntity.setId(TestConstants.ID);

		Mockito.when(userDao.getEntityById(Mockito.anyInt())).thenReturn(userEntity);

		chatServiceImpl.joinChat(chatJoinDto);

		Mockito.verify(chatDao, Mockito.times(0)).updateMemberStatus(TestConstants.ID, TestConstants.ID,
				MemberStatus.SELF_JOINED);
		Mockito.verify(chatDao, Mockito.times(1)).joinChat(TestConstants.ID, TestConstants.ID);
		Mockito.verify(userDao, Mockito.times(1)).getEntityById(TestConstants.ID);
	}

	@Test
	public void testJoinChatChatButInvited() {
		ChatJoinDto chatJoinDto = new ChatJoinDto();
		chatJoinDto.setUserId(TestConstants.ID);
		chatJoinDto.setChatId(TestConstants.ID);

		Mockito.when(chatDao.getStatusId(TestConstants.ID, TestConstants.ID)).thenReturn(MemberStatus.INVITED);

		UserEntity userEntity = new UserEntity();
		userEntity.setId(TestConstants.ID);

		Mockito.when(userDao.getEntityById(Mockito.anyInt())).thenReturn(userEntity);

		chatServiceImpl.joinChat(chatJoinDto);

		Mockito.verify(chatDao, Mockito.times(1)).updateMemberStatus(TestConstants.ID, TestConstants.ID,
				MemberStatus.SELF_JOINED);
		Mockito.verify(chatDao, Mockito.times(0)).joinChat(TestConstants.ID, TestConstants.ID);
		Mockito.verify(userDao, Mockito.times(1)).getEntityById(TestConstants.ID);
	}

	@Test
	public void testJoinChatChatButDeclined() {
		ChatJoinDto chatJoinDto = new ChatJoinDto();
		chatJoinDto.setUserId(TestConstants.ID);
		chatJoinDto.setChatId(TestConstants.ID);

		Mockito.when(chatDao.getStatusId(TestConstants.ID, TestConstants.ID)).thenReturn(MemberStatus.DECLINED);

		UserEntity userEntity = new UserEntity();
		userEntity.setId(TestConstants.ID);

		Mockito.when(userDao.getEntityById(Mockito.anyInt())).thenReturn(userEntity);

		chatServiceImpl.joinChat(chatJoinDto);

		Mockito.verify(chatDao, Mockito.times(1)).updateMemberStatus(TestConstants.ID, TestConstants.ID,
				MemberStatus.SELF_JOINED);
		Mockito.verify(chatDao, Mockito.times(0)).joinChat(TestConstants.ID, TestConstants.ID);
		Mockito.verify(userDao, Mockito.times(1)).getEntityById(TestConstants.ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testJoinChatChatAlreadyMember() {
		ChatJoinDto chatJoinDto = new ChatJoinDto();
		chatJoinDto.setUserId(TestConstants.ID);
		chatJoinDto.setChatId(TestConstants.ID);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.SELF_JOINED);

		chatServiceImpl.joinChat(chatJoinDto);
	}

	@Test
	public void testKickFromChat() {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(TestConstants.ID);
		chatKickPromoteDto.setChatId(TestConstants.ID);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.SELF_JOINED);

		chatServiceImpl.kickFromChat(chatKickPromoteDto);

		Mockito.verify(chatDao, Mockito.times(1)).kickFromChat(TestConstants.ID, TestConstants.ID);
	}

	@Test(expected = RuntimeException.class)
	public void testKickFromChatIncorrectUserId() {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(1111111111);
		chatKickPromoteDto.setChatId(TestConstants.ID);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		chatServiceImpl.kickFromChat(chatKickPromoteDto);
	}

	@Test(expected = RuntimeException.class)
	public void testKickFromChatIncorrectChatId() {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(TestConstants.ID);
		chatKickPromoteDto.setChatId(1111111111);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		chatServiceImpl.kickFromChat(chatKickPromoteDto);
	}

	@Test(expected = RuntimeException.class)
	public void testKickFromChatIncorrectDoerRoleId() {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(TestConstants.ID);
		chatKickPromoteDto.setChatId(TestConstants.ID);
		chatKickPromoteDto.setDoerRoleId(1111111111);

		chatServiceImpl.kickFromChat(chatKickPromoteDto);
	}

	@Test
	public void testMakeAdmin() {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(TestConstants.ID);
		chatKickPromoteDto.setChatId(TestConstants.ID);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.SELF_JOINED);

		chatServiceImpl.makeAdmin(chatKickPromoteDto);

		Mockito.verify(chatDao, Mockito.times(1)).makeAdmin(TestConstants.ID, TestConstants.ID);
	}

	@Test(expected = RuntimeException.class)
	public void testMakeAdminIncorrectUserId() {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(1111111111);
		chatKickPromoteDto.setChatId(TestConstants.ID);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		chatServiceImpl.makeAdmin(chatKickPromoteDto);
	}

	@Test(expected = RuntimeException.class)
	public void testMakeAdminIncorrectChatId() {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(TestConstants.ID);
		chatKickPromoteDto.setChatId(1111111111);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		chatServiceImpl.makeAdmin(chatKickPromoteDto);
	}

	@Test(expected = RuntimeException.class)
	public void testMakeAdminIncorrectDoerRoleId() {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(TestConstants.ID);
		chatKickPromoteDto.setChatId(TestConstants.ID);
		chatKickPromoteDto.setDoerRoleId(1111111111);

		chatServiceImpl.makeAdmin(chatKickPromoteDto);
	}

	@Test
	public void testIsKickPromoteValid() {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(TestConstants.ID);
		chatKickPromoteDto.setChatId(TestConstants.ID);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.SELF_JOINED);
		assertTrue(chatServiceImpl.isKickPromoteValid(chatKickPromoteDto));

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(MemberStatus.JOINED_BY_INVITATION);
		assertTrue(chatServiceImpl.isKickPromoteValid(chatKickPromoteDto));
	}

	@Test
	public void testIsNotKickPromoteValid() {
		ChatKickPromoteDto chatKickPromoteDto = new ChatKickPromoteDto();
		chatKickPromoteDto.setUserId(TestConstants.ID);
		chatKickPromoteDto.setChatId(TestConstants.ID);
		chatKickPromoteDto.setDoerRoleId(MemberRole.ADMIN);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.INVITED);
		assertFalse(chatServiceImpl.isKickPromoteValid(chatKickPromoteDto));

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.DECLINED);
		assertFalse(chatServiceImpl.isKickPromoteValid(chatKickPromoteDto));

		chatKickPromoteDto.setDoerRoleId(MemberRole.USER);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.SELF_JOINED);
		assertFalse(chatServiceImpl.isKickPromoteValid(chatKickPromoteDto));

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(MemberStatus.JOINED_BY_INVITATION);
		assertFalse(chatServiceImpl.isKickPromoteValid(chatKickPromoteDto));

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.INVITED);
		assertFalse(chatServiceImpl.isKickPromoteValid(chatKickPromoteDto));

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.DECLINED);
		assertFalse(chatServiceImpl.isKickPromoteValid(chatKickPromoteDto));
	}

	@Test
	public void isMember() {
		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.SELF_JOINED);
		assertTrue(chatServiceImpl.isMember(Mockito.anyInt(), Mockito.anyInt()));

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(MemberStatus.JOINED_BY_INVITATION);
		assertTrue(chatServiceImpl.isMember(Mockito.anyInt(), Mockito.anyInt()));
	}

	@Test
	public void isNotMember() {
		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.INVITED);
		assertFalse(chatServiceImpl.isMember(Mockito.anyInt(), Mockito.anyInt()));

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.DECLINED);
		assertFalse(chatServiceImpl.isMember(Mockito.anyInt(), Mockito.anyInt()));
	}

	@Test
	public void isInvited() {
		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.INVITED);
		assertTrue(chatServiceImpl.isInvited(Mockito.anyInt(), Mockito.anyInt()));
	}

	@Test
	public void isNotInvited() {
		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.SELF_JOINED);
		assertFalse(chatServiceImpl.isInvited(Mockito.anyInt(), Mockito.anyInt()));

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(MemberStatus.JOINED_BY_INVITATION);
		assertFalse(chatServiceImpl.isInvited(Mockito.anyInt(), Mockito.anyInt()));

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.DECLINED);
		assertFalse(chatServiceImpl.isInvited(Mockito.anyInt(), Mockito.anyInt()));
	}

	@Test
	public void isDeclined() {
		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.DECLINED);
		assertTrue(chatServiceImpl.isDeclined(Mockito.anyInt(), Mockito.anyInt()));
	}

	@Test
	public void isNotDeclined() {
		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.SELF_JOINED);
		assertFalse(chatServiceImpl.isDeclined(Mockito.anyInt(), Mockito.anyInt()));

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(MemberStatus.JOINED_BY_INVITATION);
		assertFalse(chatServiceImpl.isDeclined(Mockito.anyInt(), Mockito.anyInt()));

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.INVITED);
		assertFalse(chatServiceImpl.isDeclined(Mockito.anyInt(), Mockito.anyInt()));
	}

	@Test
	public void isAdmin() {
		assertTrue(chatServiceImpl.isAdmin(MemberRole.ADMIN));
	}

	@Test
	public void isNotAdmin() {
		assertFalse(chatServiceImpl.isAdmin(MemberRole.USER));
	}

}
