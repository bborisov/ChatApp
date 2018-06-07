package com.ibm.services;

import java.util.List;

import com.ibm.dto.UserCreateDto;
import com.ibm.dto.UserLoginDto;
import com.ibm.entities.wrappers.User;

public interface UserService {

	public User createUser(UserCreateDto userCreateDto);

	public User login(UserLoginDto userLoginDto);

	public List<User> getAllUsers();

	public boolean isEmailStored(String email);

}
