package com.ibm.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.ibm.constants.ChatType;
import com.ibm.constants.MemberRole;
import com.ibm.constants.MemberStatus;
import com.ibm.dao.ChatDao;
import com.ibm.dto.MemberDto;
import com.ibm.entities.ChatEntity;
import com.ibm.entities.MembershipEntity;
import com.ibm.entities.wrappers.Chat;

@SuppressWarnings("unchecked")
public class ChatDaoImpl extends BasicDaoImpl<ChatEntity> implements ChatDao {

	@Override
	public Chat createChat(String chatName, String summary, int typeId, int creatorId) {
		Timestamp tms = new Timestamp(System.currentTimeMillis());
		ChatEntity chatEntity = new ChatEntity(tms, creatorId, chatName, summary, typeId, tms);

		Session session = sessionFactory.getCurrentSession();
		session.persist(chatEntity);

		return (Chat) chatEntity;
	}

	@Override
	public void joinChat(int userId, int chatId) {
		MembershipEntity membershipEntity = new MembershipEntity(MemberRole.USER, MemberStatus.SELF_JOINED, chatId,
				userId);

		Session session = sessionFactory.getCurrentSession();
		session.persist(membershipEntity);
	}

	@Override
	public void updateMemberStatus(int userId, int chatId, int statusId) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(
				"update MembershipEntity set statusId = :statusId where userId = :userId and chatId = :chatId");
		query.setParameter("statusId", statusId);
		query.setParameter("userId", userId);
		query.setParameter("chatId", chatId);

		int rowsAffected = query.executeUpdate();

		if (rowsAffected != 1) {
			throw new RuntimeException("DB inconsistency");
		}
	}

	@Override
	public void kickFromChat(int userId, int chatId) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery("delete from MembershipEntity where userId = :userId and chatId = :chatId");
		query.setParameter("userId", userId);
		query.setParameter("chatId", chatId);

		int rowsAffected = query.executeUpdate();

		if (rowsAffected != 1) {
			throw new RuntimeException("DB inconsistency");
		}
	}

	@Override
	public void makeAdmin(int userId, int chatId) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(
				"update MembershipEntity set roleId = :roleId where userId = :userId and chatId = :chatId");
		query.setParameter("roleId", MemberRole.ADMIN);
		query.setParameter("userId", userId);
		query.setParameter("chatId", chatId);

		int rowsAffected = query.executeUpdate();

		if (rowsAffected != 1) {
			throw new RuntimeException("DB inconsistency");
		}
	}

	@Override
	public List<MemberDto> getAllMembersFromChat(int chatId) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(
				"select u.id as id, u.name as name, u.email as email, u.statusId as statusId, m.roleId as roleId from MembershipEntity m join m.user u "
						+ "where m.chatId = :chatId and m.statusId not in (:invitedId,:declinedId)");
		query.setParameter("chatId", chatId);
		query.setParameter("invitedId", MemberStatus.INVITED);
		query.setParameter("declinedId", MemberStatus.DECLINED);
		query.setResultTransformer(Transformers.aliasToBean(MemberDto.class));

		List<MemberDto> listOfMembers = (List<MemberDto>) query.list();

		return listOfMembers;
	}

	@Override
	public List<Chat> getAllPermittedChats(int userId) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(
				"from ChatEntity c where c.typeId = :typeId or c.id in (select chatId from MembershipEntity where userId = :userId)");
		query.setParameter("typeId", ChatType.PUBLIC);
		query.setParameter("userId", userId);

		List<Chat> listOfChats = (List<Chat>) query.list();

		return listOfChats;
	}

	@Override
	public int getRoleId(int userId, int chatId) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(MembershipEntity.class);
		Integer roleId = (Integer) criteria.add(Restrictions.eq("userId", userId))
				.add(Restrictions.eq("chatId", chatId)).setProjection(Projections.property("roleId")).uniqueResult();

		if (roleId == null) {
			return 0;
		}

		return (int) roleId;
	}

	@Override
	public int getStatusId(int userId, int chatId) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(MembershipEntity.class);
		Integer statusId = (Integer) criteria.add(Restrictions.eq("userId", userId))
				.add(Restrictions.eq("chatId", chatId)).setProjection(Projections.property("statusId")).uniqueResult();

		if (statusId == null) {
			return 0;
		}

		return (int) statusId;
	}

	@Override
	public Class<ChatEntity> getEntityClass() {
		return ChatEntity.class;
	}

}
