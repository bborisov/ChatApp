package com.ibm.services.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.dao.UserDao;
import com.ibm.other.User;
import com.ibm.other.UserCreateDto;
import com.ibm.other.UserStatus;
import com.ibm.services.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDao userDao;
	
	private static final String EMAIL_REGEX = "[A-Z0-9_]+@[A-Z0-9]+\\.[A-Z]{2,4}";
	
	@Override
	public User createUser(UserCreateDto userCreateDto) {
		String userName = userCreateDto.getName();
		if (userName == null || userName.trim().equals("") || userName.length() > 50) {
			throw new RuntimeException("Invalid userName!");
		}
		
		String email = userCreateDto.getEmail();
		if (!isEmailValid(email)) {
			throw new RuntimeException("Invalid e-mail address!");
		}
		
		if (userDao.isEmailStored(email)) {
			throw new RuntimeException("E-mail already taken!");
		}
		
		return userDao.createUser(userName, email, UserStatus.ACTIVE);
	}

	@Override
	public User login(String email) {
		if (!isEmailValid(email)) {
			throw new RuntimeException("Invalid e-mail address!");
		}
		
		if (!userDao.isEmailStored(email)) {
			throw new RuntimeException("There is no user with such an e-mail!");
		}
		
		return userDao.getUserByEmail(email);
	}

	@Override
	public List<User> getAllUsers() {
		return userDao.getAllUsers();
	}
	
	@Override
	public boolean isEmailValid(String email) {
		Pattern pattern = Pattern.compile(EMAIL_REGEX);
		Matcher matcher = pattern.matcher(email.toUpperCase());
		
		if (matcher.matches()) {
			return true;
		}
		
		return false;
	}

}
