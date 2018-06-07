package com.ibm.services.impl;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.activities.HashActivity;
import com.ibm.constants.UserStatus;
import com.ibm.dao.UserDao;
import com.ibm.dto.UserCreateDto;
import com.ibm.dto.UserLoginDto;
import com.ibm.entities.UserEntity;
import com.ibm.entities.wrappers.User;
import com.ibm.services.UserService;
import com.ibm.validators.EmailValidator;
import com.ibm.validators.PasswordValidator;
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

		String password = userCreateDto.getPassword();
		PasswordValidator passwordValidator = new PasswordValidator(password);

		if (!passwordValidator.isValid()) {
			throw new IllegalArgumentException("Invalid password - pattern not matched!");
		}

		byte[] salt = HashActivity.getRandomSalt();
		String encodedSalt = Base64.getEncoder().encodeToString(salt);
		byte[] passwordHash = HashActivity.hashPassword(password, salt);
		String encodedPasswordHash = Base64.getEncoder().encodeToString(passwordHash);

		User user = userDao.createUser(userName, email, encodedPasswordHash, encodedSalt, UserStatus.ACTIVE);
		((UserEntity) user).setPassword(null);
		((UserEntity) user).setSalt(null);

		return user;
	}

	@Override
	public User login(UserLoginDto userLoginDto) {
		String email = userLoginDto.getEmail();
		EmailValidator emailValidator = new EmailValidator(email);

		if (!emailValidator.isValid()) {
			throw new IllegalArgumentException("Invalid e-mail address!");
		}

		String password = userLoginDto.getPassword();
		PasswordValidator passwordValidator = new PasswordValidator(password);

		if (!passwordValidator.isValid()) {
			throw new IllegalArgumentException("Invalid password - pattern not matched!");
		}

		UserEntity user = userDao.getUserByEmail(email);
		if (user == null) {
			throw new NoSuchElementException("There is no user with such an e-mail!");
		}

		byte[] decodedSalt = Base64.getDecoder().decode(user.getSalt());
		byte[] decodedPasswordHash = Base64.getDecoder().decode(user.getPassword());
		if (!Arrays.equals(HashActivity.hashPassword(password, decodedSalt), (decodedPasswordHash))) {
			throw new IllegalArgumentException("Invalid password - not matched the one from DB!");
		}

		user.setPassword(null);
		user.setSalt(null);

		return (User) user;
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
