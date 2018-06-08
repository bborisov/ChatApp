package com.ibm.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ibm.dao.UserDao;
import com.ibm.entities.UserEntity;
import com.ibm.entities.wrappers.User;

@SuppressWarnings("unchecked")
public class UserDaoImpl extends BasicDaoImpl<UserEntity> implements UserDao {

	@Override
	public User createUser(String userName, String email, String password, String salt, int statusId) {
		UserEntity userEntity = new UserEntity(userName, email, password, salt, statusId);

		Session session = sessionFactory.getCurrentSession();
		session.persist(userEntity);

		return (User) userEntity;
	}

	@Override
	public User getUserByEmail(String email) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(UserEntity.class);
		UserEntity userEntity = (UserEntity) criteria.add(Restrictions.eq("email", email)).uniqueResult();

		return (User) userEntity;
	}

	@Override
	public List<User> getAllUsers() {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(UserEntity.class);
		List<User> listOfUsers = (List<User>) criteria.list();

		for (User user : listOfUsers) {
			user.setPassword(null);
			user.setSalt(null);
		}

		// example
		// List<User> listOfUsers = listOfUserEntities.stream().map(u ->
		// u.toUser()).collect(Collectors.toList());

		return listOfUsers;
	}

	@Override
	public Class<UserEntity> getEntityClass() {
		return UserEntity.class;
	}

}