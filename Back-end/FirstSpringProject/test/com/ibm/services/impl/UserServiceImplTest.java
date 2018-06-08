package com.ibm.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Base64;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.ibm.activities.HashActivity;
import com.ibm.constants.TestConstants;
import com.ibm.dao.UserDao;
import com.ibm.dto.UserCreateDto;
import com.ibm.dto.UserLoginDto;
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
		userCreateDto.setPassword(TestConstants.PASSWORD);

		userServiceImpl.createUser(userCreateDto);

		Mockito.verify(userDao, Mockito.times(1)).createUser(Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyString(), Matchers.anyInt());
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
		userCreateDto.setEmail(TestConstants.INVALID_EMAIL);

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
		byte[] salt = HashActivity.getRandomSalt();
		byte[] passwordHash = HashActivity.hashPassword(TestConstants.PASSWORD, salt);

		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setEmail(TestConstants.EMAIL);
		userLoginDto.setPassword(TestConstants.PASSWORD);

		UserEntity userEntity = new UserEntity();
		userEntity.setId(1);
		userEntity.setSalt(Base64.getEncoder().encodeToString(salt));
		userEntity.setPassword(Base64.getEncoder().encodeToString(passwordHash));

		Mockito.when(userDao.getUserByEmail(Mockito.anyString())).thenReturn((User) userEntity);

		User user = userServiceImpl.login(userLoginDto);

		Mockito.verify(userDao, Mockito.times(1)).getUserByEmail(TestConstants.EMAIL);
		assertNull(user.getPassword());
		assertNull(user.getSalt());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLoginInvalidEmail() {
		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setEmail(TestConstants.INVALID_EMAIL);

		userServiceImpl.login(userLoginDto);
	}

	@Test(expected = NoSuchElementException.class)
	public void testLoginEmailNotStored() {
		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setEmail(TestConstants.EMAIL);
		userLoginDto.setPassword(TestConstants.PASSWORD);

		userServiceImpl.login(userLoginDto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLoginInvalidPassword() {
		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setEmail(TestConstants.EMAIL);
		userLoginDto.setPassword(TestConstants.INVALID_PASSWORD);

		userServiceImpl.login(userLoginDto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLoginPasswordNotMatchTheOneInDb() {
		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setEmail(TestConstants.EMAIL);
		userLoginDto.setPassword(TestConstants.PASSWORD);

		UserEntity userEntity = new UserEntity();
		userEntity.setSalt(Base64.getEncoder().encodeToString("salt".getBytes()));
		userEntity.setPassword(Base64.getEncoder().encodeToString(TestConstants.PASSWORD.getBytes()));

		Mockito.when(userDao.getUserByEmail(Mockito.anyString())).thenReturn((User) userEntity);

		userServiceImpl.login(userLoginDto);
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
