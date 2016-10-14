package com.ibm.dao.impl;

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
import com.ibm.constants.TestConstants;
import com.ibm.constants.UserStatus;
import com.ibm.dao.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@Rollback
@Transactional
public class UserDaoImplIT {

	@Autowired
	private UserDao userDao;

	private int userId;

	@Before
	public void setUp() throws Exception {
		userId = userDao.createUser("pffff", TestConstants.NON_EXISTING_EMAIL, UserStatus.ACTIVE).getId();
	}

	@Test
	public void testCreateUser() {
		assertNotNull(userDao.getEntityById(userId));
	}

	@Test
	public void testGetUserByEmail() {
		assertNotNull(userDao.getUserByEmail(TestConstants.NON_EXISTING_EMAIL));
	}

	@Test
	public void testGetAllUsers() {
		assertNotNull(userDao.getAllUsers());
	}

}
