package com.ibm.dao;

import java.util.List;

import com.ibm.entities.UserEntity;
import com.ibm.entities.wrappers.User;

public interface UserDao extends BasicDao<UserEntity> {

	public User createUser(String userName, String email, String password, String salt, int statusId);

	public User getUserByEmail(String email);

	public List<User> getAllUsers();

}
