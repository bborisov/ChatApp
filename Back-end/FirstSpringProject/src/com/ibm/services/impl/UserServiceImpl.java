package com.ibm.services.impl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.constants.UserStatus;
import com.ibm.dao.UserDao;
import com.ibm.dto.UserCreateDto;
import com.ibm.entities.wrappers.User;
import com.ibm.services.UserService;
import com.ibm.validators.EmailValidator;
import com.ibm.validators.StringValidator;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	@Override
	public User createUser(UserCreateDto userCreateDto) {
		String userName = userCreateDto.getName();
		StringValidator userNameValidator = new StringValidator(userName, 50);

		if (!userNameValidator.isValid()) {
			throw new IllegalArgumentException("Invalid userName!");
		}

		String email = userCreateDto.getEmail();
		EmailValidator emailValidator = new EmailValidator(email);

		if (!emailValidator.isValid()) {
			throw new IllegalArgumentException("Invalid e-mail address!");
		}

		if (isEmailStored(email)) {
			throw new UnsupportedOperationException("E-mail already taken!");
		}

		return userDao.createUser(userName, email, UserStatus.ACTIVE);
	}

	@Override
	public User login(String email) {
		EmailValidator emailValidator = new EmailValidator(email);

		if (!emailValidator.isValid()) {
			throw new IllegalArgumentException("Invalid e-mail address!");
		}

		User user = userDao.getUserByEmail(email);
		if (user == null) {
			throw new NoSuchElementException("There is no user with such an e-mail!");
		}

		return user;
	}

	@Override
	public List<User> getAllUsers() {
		return userDao.getAllUsers();
	}

	@Override
	public boolean isEmailStored(String email) {
		if (userDao.getUserByEmail(email) == null) {
			return false;
		} else {
			return true;
		}
	}

}
