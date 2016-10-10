package com.ibm.services;

import java.util.List;

import com.ibm.other.User;
import com.ibm.other.UserCreateDto;

public interface UserService {
	
	public User createUser(UserCreateDto userCreateDto);
	
	public User login(String email);
	
	public List<User> getAllUsers();
	
	public boolean isEmailValid(String email);

}
