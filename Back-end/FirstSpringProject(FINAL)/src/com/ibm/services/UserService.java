package com.ibm.services;

import java.util.List;

import com.ibm.dto.UserCreateDto;
import com.ibm.entities.wrappers.User;

public interface UserService {

	public User createUser(UserCreateDto userCreateDto);

	public User login(String email);

	public List<User> getAllUsers();

	public boolean isEmailStored(String email);

}
