package com.ibm.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import com.ibm.constants.TestConstants;
import com.ibm.constants.UserStatus;
import com.ibm.dao.UserDao;
import com.ibm.dto.UserCreateDto;
import com.ibm.entities.UserEntity;
import com.ibm.entities.wrappers.User;
import com.ibm.utilities.ReflectionUtil;

public class UserServiceImplTest {

	@InjectMocks
	private UserServiceImpl userServiceImpl;

	private UserDao userDao;

	@Before
	public void setUp() throws Exception {
		userServiceImpl = new UserServiceImpl();

		userDao = Mockito.mock(UserDao.class);
		ReflectionUtil.setField(userServiceImpl, "userDao", userDao);
	}

	@Test
	public void testCreateUser() {
		UserCreateDto userCreateDto = new UserCreateDto();
		userCreateDto.setName(TestConstants.STRING);
		userCreateDto.setEmail(TestConstants.EMAIL);

		userServiceImpl.createUser(userCreateDto);

		Mockito.verify(userDao, Mockito.times(1)).createUser(TestConstants.STRING, TestConstants.EMAIL,
				UserStatus.ACTIVE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserInvalidUserName() {
		UserCreateDto userCreateDto = new UserCreateDto();
		userCreateDto.setName(String.format("%1$51s", "a"));
		userCreateDto.setEmail(TestConstants.EMAIL);

		userServiceImpl.createUser(userCreateDto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserInvalidEmail() {
		UserCreateDto userCreateDto = new UserCreateDto();
		userCreateDto.setName(TestConstants.STRING);
		userCreateDto.setEmail("aabv.bg");

		userServiceImpl.createUser(userCreateDto);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCreateUserEmailAlreadyStored() {
		UserCreateDto userCreateDto = new UserCreateDto();
		userCreateDto.setName(TestConstants.STRING);
		userCreateDto.setEmail(TestConstants.EMAIL);

		UserEntity userEntity = new UserEntity();
		userEntity.setId(1);

		Mockito.when(userDao.getUserByEmail(Mockito.anyString())).thenReturn((User) userEntity);

		userServiceImpl.createUser(userCreateDto);
	}

	@Test
	public void testLogin() {
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1);

		Mockito.when(userDao.getUserByEmail(Mockito.anyString())).thenReturn((User) userEntity);

		userServiceImpl.login(TestConstants.EMAIL);

		Mockito.verify(userDao, Mockito.times(1)).getUserByEmail(TestConstants.EMAIL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLoginInvalidEmail() {
		userServiceImpl.login("aabv.bg");
	}

	@Test(expected = NoSuchElementException.class)
	public void testLoginEmailNotStored() {
		userServiceImpl.login(TestConstants.EMAIL);
	}

	@Test
	public void testIsEmailStored() {
		UserEntity userEntity = new UserEntity();
		userEntity.setId(Mockito.anyInt());

		Mockito.when(userDao.getUserByEmail(TestConstants.EMAIL)).thenReturn((User) userEntity);

		assertTrue(userServiceImpl.isEmailStored(TestConstants.EMAIL));
	}

	@Test
	public void testIsNotEmailStored() {
		assertFalse(userServiceImpl.isEmailStored(TestConstants.EMAIL));
	}

}
