package com.ibm.dao.impl;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ibm.dao.BasicDao;

@Component
@Transactional
public abstract class BasicDaoImpl<T> implements BasicDao<T> {

	@Autowired
	protected SessionFactory sessionFactory;

	public abstract Class<T> getEntityClass();

	@SuppressWarnings("unchecked")
	public T getEntityById(int id) {
		Session session = sessionFactory.getCurrentSession();

		Class<T> clazz = getEntityClass();

		Criteria criteria = session.createCriteria(clazz);
		Object entity = criteria.add(Restrictions.eq("id", id)).uniqueResult();

		return (T) entity;
	}

}
