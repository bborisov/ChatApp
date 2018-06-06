package com.ibm.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.config.AppConfig;
import com.ibm.config.UserPrincipal;
import com.ibm.constants.TestConstants;
import com.ibm.constants.UserStatus;
import com.ibm.dao.UserDao;
import com.ibm.dto.UserCreateDto;
import com.ibm.entities.wrappers.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@Rollback
@Transactional
public class UserControllerIT {

	@Autowired
	private UserController userController;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AbstractSubscribableChannel brokerChannel;

	private TestChannelInterceptor brokerChannelInterceptor;

	private Principal principal;

	@Before
	public void setUp() throws Exception {
		principal = new UserPrincipal(TestConstants.STRING);

		brokerChannelInterceptor = new TestChannelInterceptor();
		brokerChannel.addInterceptor(brokerChannelInterceptor);
	}

	@Test
	public void testCreateUser() throws IOException, InterruptedException {
		UserCreateDto userCreateDto = new UserCreateDto();
		userCreateDto.setName(TestConstants.STRING);
		userCreateDto.setEmail(TestConstants.NON_EXISTING_EMAIL);

		userController.createUser(userCreateDto, principal);

		brokerChannelInterceptor.setIncludedDestinations("/user/**");

		Message<?> result = brokerChannelInterceptor.awaitMessage(5);
		assertNotNull(result);

		String json = new String((byte[]) result.getPayload(), Charset.forName("UTF-8"));

		assertNotNull(userDao.getUserByEmail(TestConstants.NON_EXISTING_EMAIL));
		assertTrue(json.contains(TestConstants.NON_EXISTING_EMAIL));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserInvalidName() throws IOException, InterruptedException {
		UserCreateDto userCreateDto = new UserCreateDto();
		userCreateDto.setEmail(TestConstants.NON_EXISTING_EMAIL);

		userController.createUser(userCreateDto, principal);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserInvalidEmail() throws IOException {
		UserCreateDto userCreateDto = new UserCreateDto();
		userCreateDto.setName(TestConstants.STRING);
		userCreateDto.setEmail(TestConstants.INVALID_EMAIL);

		userController.createUser(userCreateDto, principal);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCreateUserExistingEmail() throws IOException {
		userDao.createUser(TestConstants.STRING, TestConstants.NON_EXISTING_EMAIL, UserStatus.ACTIVE);

		UserCreateDto userCreateDto = new UserCreateDto();
		userCreateDto.setName(TestConstants.STRING);
		userCreateDto.setEmail(TestConstants.NON_EXISTING_EMAIL);

		userController.createUser(userCreateDto, principal);
	}

	@Test
	public void testLogin() throws IOException, InterruptedException {
		userDao.createUser(TestConstants.STRING, TestConstants.NON_EXISTING_EMAIL, UserStatus.ACTIVE);

		userController.login(TestConstants.NON_EXISTING_EMAIL, principal);

		brokerChannelInterceptor.setIncludedDestinations("/user/**");

		Message<?> result = brokerChannelInterceptor.awaitMessage(5);
		assertNotNull(result);

		String json = new String((byte[]) result.getPayload(), Charset.forName("UTF-8"));

		assertTrue(json.contains(TestConstants.NON_EXISTING_EMAIL));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLoginInvalidEmail() throws IOException {
		userController.login(TestConstants.INVALID_EMAIL, principal);
	}

	@Test(expected = NoSuchElementException.class)
	public void testLoginNonExistingEmail() throws IOException {
		userController.login(TestConstants.NON_EXISTING_EMAIL, principal);
	}

	@Test
	public void testGetAllUsers() throws IOException, InterruptedException {
		User user = userDao.createUser(TestConstants.STRING, TestConstants.NON_EXISTING_EMAIL, UserStatus.ACTIVE);

		userController.getAllUsers(principal);

		brokerChannelInterceptor.setIncludedDestinations("/user/**");

		Message<?> result = brokerChannelInterceptor.awaitMessage(5);
		assertNotNull(result);

		String json = new String((byte[]) result.getPayload(), Charset.forName("UTF-8"));

		assertTrue(json.contains(user.getEmail()));
	}

}
