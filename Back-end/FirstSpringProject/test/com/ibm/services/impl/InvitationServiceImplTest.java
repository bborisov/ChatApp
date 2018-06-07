package com.ibm.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import com.ibm.constants.InvitationStatus;
import com.ibm.constants.MemberStatus;
import com.ibm.constants.TestConstants;
import com.ibm.constants.UserStatus;
import com.ibm.dao.ChatDao;
import com.ibm.dao.InvitationDao;
import com.ibm.dao.UserDao;
import com.ibm.dto.InvitationAcceptDeclineDto;
import com.ibm.dto.InvitationCreateDto;
import com.ibm.entities.UserEntity;
import com.ibm.entities.wrappers.User;
import com.ibm.services.ChatService;
import com.ibm.utilities.ReflectionUtil;

public class InvitationServiceImplTest {

	@InjectMocks
	private InvitationServiceImpl invitationServiceImpl;

	private ChatService chatService;
	private InvitationDao invitationDao;
	private ChatDao chatDao;
	private UserDao userDao;

	@Before
	public void setUp() throws Exception {
		invitationServiceImpl = new InvitationServiceImpl();

		chatService = Mockito.mock(ChatService.class);
		ReflectionUtil.setField(invitationServiceImpl, "chatService", chatService);

		invitationDao = Mockito.mock(InvitationDao.class);
		ReflectionUtil.setField(invitationServiceImpl, "invitationDao", invitationDao);

		chatDao = Mockito.mock(ChatDao.class);
		ReflectionUtil.setField(invitationServiceImpl, "chatDao", chatDao);

		userDao = Mockito.mock(UserDao.class);
		ReflectionUtil.setField(invitationServiceImpl, "userDao", userDao);
	}

	@Test
	public void testInviteToChat() {
		InvitationCreateDto invitationCreateDto = new InvitationCreateDto();
		invitationCreateDto.setEmail(TestConstants.EMAIL);
		invitationCreateDto.setChatId(TestConstants.ID);
		invitationCreateDto.setInvitorId(TestConstants.ID);

		UserEntity userEntity = new UserEntity();
		userEntity.setId(TestConstants.ID);

		Mockito.when(userDao.getUserByEmail(Mockito.anyString())).thenReturn(userEntity);

		invitationServiceImpl.inviteToChat(invitationCreateDto);

		Mockito.verify(invitationDao, Mockito.times(1)).inviteToChat(TestConstants.ID, TestConstants.ID,
				TestConstants.ID);
		Mockito.verify(chatDao, Mockito.times(1)).joinChat(TestConstants.ID, TestConstants.ID);
		Mockito.verify(chatDao, Mockito.times(1)).updateMemberStatus(TestConstants.ID, TestConstants.ID,
				MemberStatus.INVITED);
	}

	@Test
	public void testInviteToChatButInvited() {
		InvitationCreateDto invitationCreateDto = new InvitationCreateDto();
		invitationCreateDto.setEmail(TestConstants.EMAIL);
		invitationCreateDto.setChatId(TestConstants.ID);
		invitationCreateDto.setInvitorId(TestConstants.ID);

		UserEntity userEntity = new UserEntity();
		userEntity.setId(TestConstants.ID);

		Mockito.when(userDao.getUserByEmail(Mockito.anyString())).thenReturn(userEntity);
		Mockito.when(chatService.isInvited(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);

		invitationServiceImpl.inviteToChat(invitationCreateDto);

		Mockito.verify(invitationDao, Mockito.times(1)).inviteToChat(TestConstants.ID, TestConstants.ID,
				TestConstants.ID);
		Mockito.verify(chatDao, Mockito.times(0)).joinChat(TestConstants.ID, TestConstants.ID);
		Mockito.verify(chatDao, Mockito.times(0)).updateMemberStatus(TestConstants.ID, TestConstants.ID,
				MemberStatus.INVITED);
	}

	@Test
	public void testInviteToChatButDeclined() {
		InvitationCreateDto invitationCreateDto = new InvitationCreateDto();
		invitationCreateDto.setEmail(TestConstants.EMAIL);
		invitationCreateDto.setChatId(TestConstants.ID);
		invitationCreateDto.setInvitorId(TestConstants.ID);

		UserEntity userEntity = new UserEntity();
		userEntity.setId(TestConstants.ID);

		Mockito.when(userDao.getUserByEmail(Mockito.anyString())).thenReturn(userEntity);
		Mockito.when(chatService.isDeclined(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);

		invitationServiceImpl.inviteToChat(invitationCreateDto);

		Mockito.verify(invitationDao, Mockito.times(1)).inviteToChat(TestConstants.ID, TestConstants.ID,
				TestConstants.ID);
		Mockito.verify(chatDao, Mockito.times(0)).joinChat(TestConstants.ID, TestConstants.ID);
		Mockito.verify(chatDao, Mockito.times(1)).updateMemberStatus(TestConstants.ID, TestConstants.ID,
				MemberStatus.INVITED);
	}

	@Test
	public void testInviteToChatButFirstCreateUser() {
		InvitationCreateDto invitationCreateDto = new InvitationCreateDto();
		invitationCreateDto.setEmail(TestConstants.EMAIL);
		invitationCreateDto.setChatId(TestConstants.ID);
		invitationCreateDto.setInvitorId(TestConstants.ID);

		UserEntity userEntity = new UserEntity();
		userEntity.setId(TestConstants.ID);

		Mockito.when(userDao.createUser(null, TestConstants.EMAIL, null, null, UserStatus.INACTIVE))
				.thenReturn((User) userEntity);

		invitationServiceImpl.inviteToChat(invitationCreateDto);

		Mockito.verify(userDao, Mockito.times(1)).createUser(null, TestConstants.EMAIL, null, null,
				UserStatus.INACTIVE);
		Mockito.verify(invitationDao, Mockito.times(1)).inviteToChat(TestConstants.ID, TestConstants.ID,
				TestConstants.ID);
		Mockito.verify(chatDao, Mockito.times(1)).joinChat(TestConstants.ID, TestConstants.ID);
		Mockito.verify(chatDao, Mockito.times(1)).updateMemberStatus(TestConstants.ID, TestConstants.ID,
				MemberStatus.INVITED);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInviteToChatInvalidEmail() {
		InvitationCreateDto invitationCreateDto = new InvitationCreateDto();
		invitationCreateDto.setEmail("aabv.bg");
		invitationCreateDto.setChatId(TestConstants.ID);
		invitationCreateDto.setInvitorId(TestConstants.ID);

		invitationServiceImpl.inviteToChat(invitationCreateDto);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testInviteToChatButMember() {
		InvitationCreateDto invitationCreateDto = new InvitationCreateDto();
		invitationCreateDto.setEmail(TestConstants.EMAIL);
		invitationCreateDto.setChatId(TestConstants.ID);
		invitationCreateDto.setInvitorId(TestConstants.ID);

		UserEntity userEntity = new UserEntity();
		userEntity.setId(TestConstants.ID);

		Mockito.when(userDao.getUserByEmail(Mockito.anyString())).thenReturn(userEntity);
		Mockito.when(chatService.isMember(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);

		invitationServiceImpl.inviteToChat(invitationCreateDto);
	}

	@Test
	public void testAcceptInvitationWhileInvited() {
		InvitationAcceptDeclineDto invitationAcceptDto = new InvitationAcceptDeclineDto();
		invitationAcceptDto.setUserId(TestConstants.ID);
		invitationAcceptDto.setChatId(TestConstants.ID);
		invitationAcceptDto.setInvitationId(TestConstants.ID);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.INVITED);

		invitationServiceImpl.acceptInvitation(invitationAcceptDto);

		Mockito.verify(invitationDao, Mockito.times(1)).updateInvitationStatus(TestConstants.ID,
				InvitationStatus.ACCEPTED);
		Mockito.verify(chatDao, Mockito.times(1)).updateMemberStatus(TestConstants.ID, TestConstants.ID,
				MemberStatus.JOINED_BY_INVITATION);
		Mockito.verify(userDao, Mockito.times(1)).getEntityById(TestConstants.ID);
	}

	@Test
	public void testAcceptInvitationWhileDeclined() {
		InvitationAcceptDeclineDto invitationAcceptDto = new InvitationAcceptDeclineDto();
		invitationAcceptDto.setUserId(TestConstants.ID);
		invitationAcceptDto.setChatId(TestConstants.ID);
		invitationAcceptDto.setInvitationId(TestConstants.ID);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.DECLINED);

		invitationServiceImpl.acceptInvitation(invitationAcceptDto);

		Mockito.verify(invitationDao, Mockito.times(1)).updateInvitationStatus(TestConstants.ID,
				InvitationStatus.ACCEPTED);
		Mockito.verify(chatDao, Mockito.times(1)).updateMemberStatus(TestConstants.ID, TestConstants.ID,
				MemberStatus.JOINED_BY_INVITATION);
		Mockito.verify(userDao, Mockito.times(1)).getEntityById(TestConstants.ID);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testAcceptInvitationButSelfJoined() {
		InvitationAcceptDeclineDto invitationAcceptDto = new InvitationAcceptDeclineDto();
		invitationAcceptDto.setUserId(TestConstants.ID);
		invitationAcceptDto.setChatId(TestConstants.ID);
		invitationAcceptDto.setInvitationId(TestConstants.ID);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.SELF_JOINED);

		invitationServiceImpl.acceptInvitation(invitationAcceptDto);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testAcceptInvitationButJoinedByInvitation() {
		InvitationAcceptDeclineDto invitationAcceptDto = new InvitationAcceptDeclineDto();
		invitationAcceptDto.setUserId(TestConstants.ID);
		invitationAcceptDto.setChatId(TestConstants.ID);
		invitationAcceptDto.setInvitationId(TestConstants.ID);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(MemberStatus.JOINED_BY_INVITATION);

		invitationServiceImpl.acceptInvitation(invitationAcceptDto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAcceptInvitationStatusProblem() {
		InvitationAcceptDeclineDto invitationAcceptDto = new InvitationAcceptDeclineDto();
		invitationAcceptDto.setUserId(TestConstants.ID);
		invitationAcceptDto.setChatId(TestConstants.ID);
		invitationAcceptDto.setInvitationId(TestConstants.ID);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(-1);

		invitationServiceImpl.acceptInvitation(invitationAcceptDto);
	}

	@Test
	public void testDeclineInvitationWhileInvited() {
		InvitationAcceptDeclineDto invitationDeclineDto = new InvitationAcceptDeclineDto();
		invitationDeclineDto.setUserId(TestConstants.ID);
		invitationDeclineDto.setChatId(TestConstants.ID);
		invitationDeclineDto.setInvitationId(TestConstants.ID);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.INVITED);

		invitationServiceImpl.declineInvitation(invitationDeclineDto);

		Mockito.verify(invitationDao, Mockito.times(1)).updateInvitationStatus(TestConstants.ID,
				InvitationStatus.DECLINED);
		Mockito.verify(chatDao, Mockito.times(1)).updateMemberStatus(TestConstants.ID, TestConstants.ID,
				MemberStatus.DECLINED);
	}

	@Test
	public void testDeclineInvitationWhileDeclined() {
		InvitationAcceptDeclineDto invitationDeclineDto = new InvitationAcceptDeclineDto();
		invitationDeclineDto.setUserId(TestConstants.ID);
		invitationDeclineDto.setChatId(TestConstants.ID);
		invitationDeclineDto.setInvitationId(TestConstants.ID);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.DECLINED);

		invitationServiceImpl.declineInvitation(invitationDeclineDto);

		Mockito.verify(invitationDao, Mockito.times(1)).updateInvitationStatus(TestConstants.ID,
				InvitationStatus.DECLINED);
		Mockito.verify(chatDao, Mockito.times(1)).updateMemberStatus(TestConstants.ID, TestConstants.ID,
				MemberStatus.DECLINED);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testDeclineInvitationButMember() {
		InvitationAcceptDeclineDto invitationDeclineDto = new InvitationAcceptDeclineDto();
		invitationDeclineDto.setUserId(TestConstants.ID);
		invitationDeclineDto.setChatId(TestConstants.ID);
		invitationDeclineDto.setInvitationId(TestConstants.ID);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(MemberStatus.SELF_JOINED);

		invitationServiceImpl.declineInvitation(invitationDeclineDto);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testDeclineInvitationButMemberTwo() {
		InvitationAcceptDeclineDto invitationDeclineDto = new InvitationAcceptDeclineDto();
		invitationDeclineDto.setUserId(TestConstants.ID);
		invitationDeclineDto.setChatId(TestConstants.ID);
		invitationDeclineDto.setInvitationId(TestConstants.ID);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(MemberStatus.JOINED_BY_INVITATION);

		invitationServiceImpl.declineInvitation(invitationDeclineDto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeclineInvitationStatusProblem() {
		InvitationAcceptDeclineDto invitationDeclineDto = new InvitationAcceptDeclineDto();
		invitationDeclineDto.setUserId(TestConstants.ID);
		invitationDeclineDto.setChatId(TestConstants.ID);
		invitationDeclineDto.setInvitationId(TestConstants.ID);

		Mockito.when(chatDao.getStatusId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(-1);

		invitationServiceImpl.declineInvitation(invitationDeclineDto);
	}

}
