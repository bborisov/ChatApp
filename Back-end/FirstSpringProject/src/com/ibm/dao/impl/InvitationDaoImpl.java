package com.ibm.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ibm.constants.InvitationStatus;
import com.ibm.dao.InvitationDao;
import com.ibm.entities.InvitationEntity;
import com.ibm.entities.wrappers.Invitation;

@SuppressWarnings("unchecked")
public class InvitationDaoImpl extends BasicDaoImpl<InvitationEntity> implements InvitationDao {

	@Override
	public Invitation inviteToChat(int userId, int chatId, int invitorId) {
		InvitationEntity invitationEntity = new InvitationEntity(InvitationStatus.PENDING, chatId, userId, invitorId);

		Session session = sessionFactory.getCurrentSession();
		session.persist(invitationEntity);

		return (Invitation) invitationEntity;
	}

	@Override
	public void updateInvitationStatus(int invitationId, int statusId) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery("update InvitationEntity set statusId = :statusId where id = :invitationId");
		query.setParameter("statusId", statusId);
		query.setParameter("invitationId", invitationId);
		
		int rowsAffected = query.executeUpdate();
		
		if (rowsAffected != 1) {
			throw new RuntimeException("DB inconsistency");
		}
	}

	@Override
	public List<Invitation> getAllInvitationsForUser(int userId) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(InvitationEntity.class);
		List<Invitation> listOfInvitations = (List<Invitation>) criteria.add(Restrictions.eq("userId", userId)).list();

		return listOfInvitations;
	}

	@Override
	public Class<InvitationEntity> getEntityClass() {
		return InvitationEntity.class;
	}

}
