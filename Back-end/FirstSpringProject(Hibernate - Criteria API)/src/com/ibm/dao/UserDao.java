package com.ibm.dao;

import java.util.List;

import com.ibm.other.User;

public interface UserDao {
	
	public User createUser(String userName, String email, int statusId);
	
	public User getUserByEmail(String email);
	
	public User getUserById(int userId);
	
	public List<User> getAllUsers();
	
	public boolean isEmailStored(String email);
	
}
